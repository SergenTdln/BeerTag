package application_projet4_groupe12.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.UnknownPartnerException;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;


/**
 * This class provides an interface between the SQL database and the Java classes of the application.
 * Documentation of the methods should be as accurate and extensive as possible, in order to allow
 * this class to be used as a "black box", from the rest of the application.
 */

public class SQLHelper extends SQLiteOpenHelper {

    /**
     * Our database
     */
    private SQLiteDatabase myDB;
    /**
     * The application context
     */
    private final Context context;
    /**
     * The database version (should be incremented in order for the onUpgrade() method to be called).
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * The database file path.
     */
    private static String DATABASE_PATH;  // = "/data/data/application_projet4_groupe12/databases/";
    private static String DATABASE_NAME = "database.sqlite";

    /***
     * Constructor. Instantiates the DB handling utility
     * @param context the application context.
     * @throws IOException if an IO exception occurred when creating a new database.
     * @throws SQLiteException if the existing database cannot be opened
     */
    public SQLHelper(Context context) throws IOException, SQLiteException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        DATABASE_PATH = context.getFilesDir().getPath()+"/";

        try{
            this.createDataBase();
        }catch (IOException e){
            throw new IOException("Error while creating the DB");
        }
        try{
            this.openDataBase();
        }catch (SQLiteException e){
            throw new SQLiteException("Error while opening the DB");
        }
        this.myDB.execSQL("PRAGMA foreign_keys = ON;"); //TODO Might cause issues in some cases; comment this line if necessary
    }

    /**
     * Creates an empty DB and replaces it with ours if it doesn't already exist
     * @throws IOException if an error occurs during DB copy.
     */
    private void createDataBase() throws IOException {
        boolean dbExists = doesDBExist();
        if(!dbExists){
            //We create a new empty DB.
            this.getReadableDatabase();
            try{
                //We override it with our DB.
                overrideDataBase();
            }catch (IOException e){
                throw new IOException("Error while copying the DB");
            }
        }
    }

    /**
     * Checks whether the DB already exists, to avoid copying it unnecessarily.
     * @return true if the DB already exists, false otherwise
     */
    private boolean doesDBExist(){
        SQLiteDatabase checkDB = null;
        try{
            String path = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null , SQLiteDatabase.OPEN_READWRITE);
        }catch (SQLiteException e){
            //The DB couldn't be opened -> it doesn't exist yet
            //This could be considered abusive use of exceptions (?)
        }
        boolean dbExists = (checkDB != null);
        if(dbExists){
            checkDB.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Copies our DB onto the empty one we have just created
     * @throws IOException if an error occurs during DB copy.
     * @author Juan-Manuel Fluxa | Code piece found on https://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
     */
    private void overrideDataBase() throws IOException{
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        String outputFile = DATABASE_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outputFile);

        byte[] buffer = new byte[1024];
        int length;
        while((length=myInput.read(buffer)) > 0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * Opens the DB on READWRITE mode
     */
    private void openDataBase() throws SQLiteException {

        String path = DATABASE_PATH + DATABASE_NAME;
        this.myDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close(){
        if(myDB!=null){
            myDB.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }

    //*****
    //BEGINNING OF THE DB QUERY METHODS
    //*****

    /**
     * Looks in the DB for all the values of the given column that comply with the given condition. Only works for one column
     * Ex: The names of all members older than 30 => getElementFromDB("Member", "Name", "Age>30")
     * @param table the name of the table in which elements will be looked for
     * @param column the column whose elements will be returned.
     * @param conditionSQL A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself).
     *                     Passing null will return all rows for the given table and column.
     * @throws SQLiteException if an error occurs while reading the DB
     * @return An ArrayList<String> containing all the results, or an empty list if there was none
     */
    private ArrayList<String> getElementFromDB(String table, String column, String conditionSQL) throws SQLiteException{
        ArrayList<String> requestResult = new ArrayList<>();
        Cursor c = myDB.query("\""+table+"\"",
                new String[]{"\""+column+"\""},
                conditionSQL,
                null,
                null,
                null,
                null,
                null
        );
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                requestResult.add(c.getString(0));
                //0 since there will never be multiple columns in this Cursor, given the method's specification
                c.moveToNext();
            }
        }
        c.close();
        return requestResult;
    }

    /**
     * Returns all the DB entries (and all the columns of these entries) matching the conditions expressed in the passed arguments
     * @param table the DB table the query is run on
     * @param columns the columns of the found entries you want in the Cursor. Passing null will return all columns of the given row
     * @param conditionSQL the SQLite WHERE clause. Passing null will return all the rows of the table
     * @param orderBy the SQLite ORDERBY clause. Passing null means the results won't be ordered in any specific way
     * @return A Cursor containing the query result
     */
    private Cursor getEntriesFromDB(String table, String[] columns, String conditionSQL, String orderBy){
        Cursor cursor = myDB.query("\""+table+"\"",
                columns,
                conditionSQL,
                null,
                null,
                null,
                orderBy,
                null
        );
        return cursor;
    }

    /**
     * Returns the internal ID of the user identified by the passed <code>email</code> argument.
     * @param email the email of the user we are looking for
     * @return The ID of this user as a String, or null if this <code>email</code> is not present in the database.
     */
    private String getUserID(String email){
        ArrayList<String> res = this.getElementFromDB("User", "_id", "User = \""+email+"\"");
        int l = res.size();
        if( l == 0 ){
            //No user with such email was found in the database
            return null;
        } else {
            return res.get(0);
        }
    }

    /**
     * Returns whether this email/username is already present in the database
     * @param email the email/username of the User to look for
     * @return True if this username already exists, False otherwise
     */
    public boolean doesUsernameExist(String email){
        boolean out;
        Cursor c = getEntriesFromDB("User",
                                    new String[]{"username"},
                                    "username = \""+email+"\"",
                                    null);
        out = (c.moveToFirst());
        c.close();
        return out;
    }

    /**
     * Returns whether this partner is already present in the database
     * @param partnerID the internal ID of the partner to look for
     * @return True if this partnerID already exists, False otherwise
     */
    private boolean doesPartnerExist(String partnerID){
        boolean out;
        Cursor c = getEntriesFromDB("Partner",
                new String[]{"name"},
                "_id = \""+partnerID+"\"",
                null);
        out = (c.moveToFirst());
        c.close();
        return out;
    }

    /**
     * Returns the number of occurrences of the character <code>pattern</code> in the <code>target</code> String.
     * @param pattern the pattern character to look for
     * @param target the String in which we are searching
     * @return the number of occurrences as a long.
     */
    private long occurrences(char pattern, String target){
        return target.codePoints().filter(c -> c==pattern).count();
    }

    /**
     * Checks whether <code>email</code> follows a valid email address format. Note this method only checks for a valid format, it does not make sure if this address actually exists.
     * @param email the email address String to check for validity
     * @return True if <code>email</code> represents a valid email address, False otherwise.
     */
    private boolean isValidEmail(String email){
        return ! (occurrences('@', email)!=1 //Email has to have exactly one '@' symbol
                || occurrences('.', email)<1 //Email has to contain at least one '.' character
                || email.lastIndexOf(".")<email.lastIndexOf("@")); //Email has to contain at least one '.' character after the '@' character
    }

    /**
     * Checks whether <code>date</code> follows the valid DD/MM/YYYY format.
     * @param date the date String to check for validity
     * @return True if <code>date</code> represents a valid date, False otherwise
     */
    private boolean isValidDate(String date){
        if((date.length()!=10) || occurrences('/', date)!=2) {
            return false;
        }

        String[] split = date.split("/");
        int day = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int year = Integer.parseInt(split[2]);
        return ! (day<1 || day>31 || month<1 || month>12 || year<2019);
    }

    /**
     * Inserts a new User in the database.
     * @param user a valid instance of User to insert in the database
     * @throws WrongEmailFormatException if user.username does not follow a valid email-address format
     * @throws WrongDateFormatException if user.creationDate does not follow the (DD/MM/YYYY) format
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     * For example, this could be caused by an SQLite error OR because this username was already present : this means that you should always
     * call <code>doesUsernameExist()</code> BEFORE trying to insert the user.
     */
    public boolean createUser(User user) throws WrongEmailFormatException, WrongDateFormatException {
        if(! isValidEmail(user.getUsername())){
            throw new WrongEmailFormatException(user.getUsername() + " is not a valid email address");
        }
        if (! isValidDate(user.getCreationDate())){
            throw new WrongDateFormatException(user.getCreationDate() + " is not a valid date format.");
        }
        if(doesUsernameExist(user.getUsername())){
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put("\"_id\"", user.getId());
        cv.put("\"oauth_profil_id\"", user.getOauthid());
        cv.put("\"username\"", user.getUsername());
        cv.put("\"created_on\"", user.getCreationDate());
        cv.put("\"first_name\"", user.getFirstName());
        cv.put("\"last_name\"", user.getLastName());

        return (myDB.insert("User", null, cv) != -1);
    }

    /**
     * Inserts a new Partner into the database.
     * @param partner the Partner instance to add to the database
     * @throws WrongDateFormatException if partner.creationDate does not follow the (DD/MM/YYYY) format
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     */
    public boolean createPartner(Partner partner) throws WrongDateFormatException {
        if (! isValidDate(partner.getCreationDate())){
            throw new WrongDateFormatException(partner.getCreationDate() + " is not a valid date format.");
        }

        ContentValues cv = new ContentValues();
        cv.put("\"_id\"", partner.getId());
        cv.put("\"name\"", partner.getName());
        cv.put("\"address\"", partner.getAddress());
        cv.put("\"created_on\"", partner.getCreationDate());

        return (myDB.insert("Partner", null, cv) != -1);
    }

    /**
     * Adds <code>amount</code> points to the User's account.
     * @param username the user's email address
     * @param amount the amount of points to add. This can be positive or negative.
     * @param partnerID Please not this is different from the partner's name.
     * @param validity_span Validity span of those points, in days. Passing 0 will make them last forever //TODO handle this better ? (later)
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     * @throws UnknownPartnerException if the passed Partner does not exist in the database
     */
    public boolean addPoints(String username, int amount, String partnerID, int validity_span) throws UnknownPartnerException {
        if(! doesPartnerExist(partnerID)){
            throw new UnknownPartnerException("Partner with ID " + partnerID + " does not exist in the database.");
        }

        int currentPoints = this.getPoints(username, partnerID);

        ContentValues cv = new ContentValues();
        cv.put("\"id_user\"", getUserID(username));
        cv.put("\"id_partner\"", partnerID);
        cv.put("\"points\"", (currentPoints + amount));
        cv.put("\"validity_span_days\"", validity_span);

        return (myDB.insert("User_points", null, cv) != -1);
    }

    /**
     * Returns the amount of points the passed User currently has by the passed Partner.
     * @param username the username (email) to look for
     * @param partnerID the internal ID of the partner to look for
     * @return the amount of points the user has earned as an int. Returns 0 if the User has never earned points by this Partner before.
     */
    public int getPoints(String username, String partnerID){
        ArrayList<String> res = this.getElementFromDB("User_points",
                                                    "points",
                                                "id_user = \""+getUserID(username)+"\" AND id_partner = \""+partnerID+"\"");
        int l = res.size();
        if( l == 0 ){
            // No pair username - partnerID was found in the User_points table.
            // This user has never earned points here before.
            return 0;
        } else {
            return Integer.parseInt(res.get(0));
        }
    }

    /**
     * Retrieves information on a User from the database and returns it as an User instance.
     * @param username the email of the User to retrieve
     * @return a User instance, or null if this username was not present in the database
     */
    public User getUser(String username){
        Cursor c = getEntriesFromDB("User", null, "username = \""+username+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() != 1){
                //Duplicate User in the database
                //TODO how to handle this ?
            }
            User out = new User(c.getInt(c.getColumnIndex("_id")),
                                c.getInt(c.getColumnIndex("oauth_profil_id")),
                                c.getString(c.getColumnIndex("username")),
                                c.getString(c.getColumnIndex("created_on")),
                                c.getString(c.getColumnIndex("first_name")),
                                c.getString(c.getColumnIndex("last_name"))
                                );
            c.close();
            return out;
        } else {
            //No such user in the database
            return null;
        }
    }

    /**
     * Retrieves information on a Partner from the database and returns it as a Partner instance.
     * @param id the internal id of the Partner to retrieve
     * @return a Partner instance, or null if this id was not present in the database
     */
    public Partner getPartner(String id){
        Cursor c = getEntriesFromDB("Partner", null, "_id = \""+id+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() != 1){
                //Duplicate Partner in the database
                //TODO how to handle this ?
            }
            Partner out = new Partner(c.getInt(c.getColumnIndex("_id")),
                                    c.getString(c.getColumnIndex("name")),
                                    c.getString(c.getColumnIndex("address")),
                                    c.getString(c.getColumnIndex("created_on")),
                                    c.getString(c.getColumnIndex("image_path")) //TODO how do we handle images ?
                                    );
            c.close();
            return out;
        } else {
            //No such user in the database
            return null;
        }
    }

    /**
     * Returns an unused id available for a new User
     * @return an unused ID as an int
     */
    public int getFreeIDUser(){
        List<String> idsAsString = getElementFromDB("User", "_id", null);
        idsAsString.sort(null);
        int i = 0;
        for (String s : idsAsString) {
            int id = Integer.parseInt(s);
            if( id==i ){
                //Advance to next
                i++;
            } else {
                return i;
            }
        }
        return i;
    }

    /**
     * Returns an unused id available for a new Partner
     * @return an unused ID as an int
     */
    public int getFreeIDPartner() {
        List<String> idsAsString = getElementFromDB("Partner", "_id", null);
        idsAsString.sort(null);
        int i = 0;
        for (String s : idsAsString) {
            int id = Integer.parseInt(s);
            if( id==i ){
                //Advance to next
                i++;
            } else {
                return i;
            }
        }
        return i;
    }

    /**
     * Returns an unused id available for a new Promotion
     * @return an unused ID as an int
     */
    public int getFreeIDPromotion() {
        List<String> idsAsString = getElementFromDB("Promotion", "_id", null);
        idsAsString.sort(null);
        int i = 0;
        for (String s : idsAsString) {
            int id = Integer.parseInt(s);
            if( id==i ){
                //Advance to next
                i++;
            } else {
                return i;
            }
        }
        return i;
    }

    //TODO getFreeID() methods for all tables

    /**
     *Updates the values of an existing user in the database. Its id is the only field that can not be updated
     * @param user an instance of User containing the new values
     * @return True if the row was successfully affected, False otherwise
     */
    public boolean updateUserData(User user){
        ContentValues cv = new ContentValues();
        cv.put("\"oauth_profil_id\"", user.getOauthid());
        cv.put("\"username\"", user.getUsername());
        cv.put("\"created_on\"", user.getCreationDate());
        cv.put("\"first_name\"", user.getFirstName());
        cv.put("\"last_name\"", user.getLastName());

        return (myDB.update("User", cv, "_id = \""+user.getId()+"\"", null) >= 1);
    }
}
