package application_projet4_groupe12.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import application_projet4_groupe12.activities.browse_clients.BrowseClientsClientDataAssociation;
import application_projet4_groupe12.activities.browse_clients.BrowseClientsShopDataAssociation;
import application_projet4_groupe12.activities.browse_points.BrowsePointsAssociation;
import application_projet4_groupe12.entities.Address;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.Shop;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.UnknownPartnerException;
import application_projet4_groupe12.exceptions.UnknownUserException;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;
import application_projet4_groupe12.utils.Pair;
import application_projet4_groupe12.utils.Triplet;


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
    private static String DATABASE_PATH; // = "/data/data/application_projet4_groupe12/databases/";
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
        } catch (IOException e){
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
        } catch (SQLiteException e){
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
     * @return The ID of this user as an instance of Integer, or null if this <code>email</code> is not present in the database.
     */
    private Integer getUserID(String email){
        ArrayList<String> res = this.getElementFromDB("User", "_id", "username = \""+email+"\"");
        if( res.isEmpty() ){
            //No user with such email was found in the database
            return null;
        } else {
            return Integer.parseInt(res.get(0));
        }
    }

    /**
     * Returns the public username of the user identified by the passed <code>userID</code> argument.
     * @param userID the internal ID of the user we are looking for
     * @return The username of this User as a String, or null if this <code>userID</code> is not present in the database.
     */
    public String getUsername(int userID){
        ArrayList<String> res = this.getElementFromDB("User", "username", "_id = \""+userID+"\"");
        if( res.isEmpty() ){
            //No user with such ID was found in the database
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
    public boolean doesPartnerExist(int partnerID){
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
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        return ! (day<1 || day>31 || month<1 || month>12 || year<1900 || year>currentYear);
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
        if ( !isValidDate(user.getCreationDate()) ){
            throw new WrongDateFormatException(user.getCreationDate() + " is not a valid date format.");
        }
        if( !isValidDate(user.getBirthday()) ){
            throw new WrongDateFormatException(user.getBirthday() + " is not a valid date format.");
        }
        if(doesUsernameExist(user.getUsername())){
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put("\"_id\"", user.getId());
        cv.put("\"username\"", user.getUsername());
        cv.put("\"password\"", user.getPasswordHashed());
        cv.put("\"created_on\"", user.getCreationDate());
        cv.put("\"first_name\"", user.getFirstName());
        cv.put("\"last_name\"", user.getLastName());
        cv.put("\"birthday\"", user.getBirthday());
        cv.put("\"image_path\"", user.getImagePath());

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
        cv.put("\"image_path\"", partner.getImagePath());

        return (myDB.insert("Partner", null, cv) != -1);
    }

    /**
     * Adds <code>amount</code> points to the User's account.
     * @param username the user's email address
     * @param amount the amount of points to add. This can be positive or negative.
     * @param shopID Please not this is different from the partner's name.
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     * @throws UnknownPartnerException if the passed Partner does not exist in the database
     */
    public boolean addPoints(String username, int amount, int shopID) throws UnknownPartnerException {

        int currentPoints = this.getPoints(getUserID(username), shopID);

        ContentValues cv = new ContentValues();
        cv.put("\"id_user\"", getUserID(username));
        cv.put("\"id_shop\"", shopID);
        cv.put("\"points\"", (currentPoints + amount));

        return (myDB.insert("User_points", null, cv) != -1);
    }

    /**
     * Returns the amount of points the passed User currently has by the passed Partner.
     * @param userID the username (email) to look for
     * @param shopID the internal ID of the shop to look for
     * @return the amount of points the user has earned as an int. Returns 0 if the User has never earned points by this Partner before.
     */
    public int getPoints(int userID, int shopID){
        ArrayList<String> res = this.getElementFromDB("User_points",
                                                    "points",
                                                "id_user = \""+userID+"\" AND id_shop = \""+shopID+"\"");
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
     * Returns a list of Shop IDs representing all this Partner's physical shops.
     * @param partnerID the partner to look for
     * @return a list of Integers. This list can be empty if this Partner has no Shop registered in the database.
     */
    public List<Integer> getAllShopsIDs(int partnerID){
        List<Integer> res = new ArrayList<>();
        List<String> shopIds = getElementFromDB("Shop_location", "_id", "id_partner = \""+partnerID+"\"");
        for (String shopID : shopIds) {
            res.add(Integer.parseInt(shopID));
        }
        return res;
    }

    /**
     * Returns a list of User IDs representing all this Shop's clients.
     * @param shopID the Shop to look for
     * @return a list of Integers. This list can be empty if this Shop has no client registered in the database.
     */
    public List<Integer> getAllUserIDs(int shopID){
        List<Integer> res = new ArrayList<>();
        List<String> userIds = getElementFromDB("User_points", "id_user", "id_shop = \""+shopID+"\"");
        for (String userID : userIds){
            res.add(Integer.parseInt(userID));
        }
        return res;
    }

    /**
     * Returns a list of Shop instances representing all this Partner's physical shops.
     * @param partnerID the partner to look for
     * @return a list of Shop instances. This list can be empty if this Partner has no Shop registered in the database.
     */
    public List<Shop> getAllShops(int partnerID){
        List<Shop> res = new ArrayList<>();
        List<String> shopIds = getElementFromDB("Shop_location", "_id", "id_partner = \""+partnerID+"\"");
        for (String shopID : shopIds) {
            res.add(this.getShop(Integer.parseInt(shopID)));
        }
        return res;
    }

    /**
     * Returns the internal ID of the Partner owning/running the Shop passed as argument
     * @param shopID the Shop_location to look for
     * @return an internal Partner ID as an int, or -1 if <code>shopID</code> was not present in the database
     */
    public int getPartnerID(int shopID){
        Cursor c = getEntriesFromDB("Shop_location",
                                    new String[]{"id_partner"},
                                    "_id = \""+shopID+"\"",
                                    null);
        // c should only contain one "cell"
        if(c.moveToFirst()){
            int out = c.getInt(c.getColumnIndex("id_partner"));
            c.close();
            return out;
        } else {
            //This shop does not exist
            c.close();
            return -1;
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

            for(int i=0; i<c.getColumnCount(); i++){
                System.out.println("Column "+i+" is "+c.getColumnName(i));
            }
            User out = new User(c.getInt(c.getColumnIndex("_id")),
                                c.getString(c.getColumnIndex("username")),
                                c.getString(c.getColumnIndex("password")),
                                c.getString(c.getColumnIndex("created_on")),
                                c.getString(c.getColumnIndex("first_name")),
                                c.getString(c.getColumnIndex("last_name")),
                                c.getString(c.getColumnIndex("birthday")),
                                c.getString(c.getColumnIndex("image_path")),
                                this.isAdmin(username)
                                );
            c.close();
            return out;
        } else {
            //No such user in the database
            c.close();
            return null;
        }
    }

    /**
     * Retrieves information on a Partner from the database and returns it as a Partner instance.
     * @param partnerID the internal id of the Partner to retrieve
     * @return a Partner instance, or null if this id was not present in the database
     */
    public Partner getPartner(int partnerID){
        Cursor c = getEntriesFromDB("Partner", null, "_id = \""+partnerID+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() != 1){
                //Duplicate Partner in the database
                //TODO how to handle this ?
            }
            Partner out = new Partner(c.getInt(c.getColumnIndex("_id")),
                                    c.getString(c.getColumnIndex("name")),
                                    c.getString(c.getColumnIndex("address")),
                                    c.getString(c.getColumnIndex("created_on")),
                                    c.getString(c.getColumnIndex("image_path"))
                                    );
            c.close();
            return out;
        } else {
            //No such partner in the database
            c.close();
            return null;
        }
    }

    /**
     * Returns an unused id available for a new User
     * @return an unused ID as an int
     */
    public int getFreeIDUser(){
        int i = 1;
        List<String> list = getElementFromDB("User", "_id", null);
        while( list.contains( Integer.toString(i) )) {
            i++;
        }
        return i;
        //TODO re-code all similar methods based on this format
    }

    /**
     * Returns an unused id available for a new Partner
     * @return an unused ID as an int
     */
    public int getFreeIDPartner() {
        List<String> idsAsString = getElementFromDB("Partner", "_id", null);
        idsAsString.sort(null);
        int i = 1;
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
        int i = 1;
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
     *Updates the values of an existing user in the database. Its _id is the only field that can not be updated
     * @param user an instance of User containing the new values
     * @return True if the row was successfully affected, False otherwise
     */
    public boolean updateUserData(User user){
        ContentValues cv = new ContentValues();
        cv.put("\"username\"", user.getUsername());
        cv.put("\"password\"", user.getPasswordHashed());
        cv.put("\"created_on\"", user.getCreationDate());
        cv.put("\"first_name\"", user.getFirstName());
        cv.put("\"last_name\"", user.getLastName());
        cv.put("\"birthday\"", user.getBirthday());
        cv.put("\"image_path\"", user.getImagePath());

        return (myDB.update("User", cv, "_id = \""+user.getId()+"\"", null) >= 1);
    }

    /**
     * Retrieves information on a Shop location from the database and returns it as a Shop instance.
     * @param shopID the internal id of the Shop to retrieve
     * @return a Shop instance, or null if this id was not present in the database
     */
    public Shop getShop(int shopID){
        Cursor c = getEntriesFromDB("Shop_location", null, "_id = \""+shopID+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() != 1){
                //Duplicate Shop in the database
                //TODO how to handle this ?
            }
            Shop out = new Shop(c.getInt(c.getColumnIndex("_id")),
                    c.getInt(c.getColumnIndex("id_partner")),
                    c.getInt(c.getColumnIndex("id_address")),
                    c.getString(c.getColumnIndex("description")),
                    c.getString(c.getColumnIndex("created_on"))
            );
            c.close();
            return out;
        } else {
            //No such shop in the database
            c.close();
            return null;
        }
    }

    /**
     * Retrieves information on an Address from the database and returns it as an Address instance.
     * @param addressID the internal id of the Address to retrieve
     * @return an Address instance, or null if this id was not present in the database
     */
    public Address getAddress(int addressID){
        Cursor c = getEntriesFromDB("Address", null, "_id = \""+addressID+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() != 1){
                //Duplicate Address in the database
                //TODO how to handle this ?
            }
            Address out = new Address(c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("city")),
                    c.getString(c.getColumnIndex("street")),
                    c.getString(c.getColumnIndex("numbers"))
            );
            c.close();
            return out;
        } else {
            //No such address in the database
            c.close();
            return null;
        }
    }

    /**
     * Retrieves information on an Shop Address from the database and returns it as an Address instance.
     * @param shopID the internal id of the Shop to look for
     * @return an Address instance, or null if this Shop id was not present in the database
     */
    public Address getShopAddress(int shopID){
        List<String> res = getElementFromDB("Shop_location", "id_address", "_id = \""+shopID+"\"");
        if(res.isEmpty()){
            //This shopID is invalid
            return null;
        } else {
            return getAddress(Integer.parseInt(res.get(0)));
        }
    }

    /**
     * Returns the hashed password of the User identified by <code>email</code>.
     * @param email the username/e-mail of the User to look for
     * @return the hashed password of this User as a String
     */
    public String getHashedPassword(String email) throws UnknownUserException {
        ArrayList<String> res = getElementFromDB("User", "password", "username = \""+email+"\"");
        if(res.isEmpty()){
            throw new UnknownUserException("User \""+email+"\" is not present in the database");
        } else {
            return res.get(0);
        }
    }

    /**
     * Adds an association between an existing User and a Partner.
     * @param adminPair a pair instance. a must contain the User's username/e-mail address as a String, and b must be the Partner's ID in the local database as an Integer
     * @return True if the operation succeeded, false otherwise
     */
    public boolean addAdmin(Pair adminPair) throws UnknownUserException {
        if(! doesUsernameExist((String) adminPair.getA())){
            throw new UnknownUserException("User with username \""+adminPair.getA()+"\" does not exist.");
        }

        ContentValues cv = new ContentValues();
        cv.put("\"id_user\"", getUserID((String) adminPair.getA()));
        cv.put("\"id_partner\"", (Integer) adminPair.getB());

        return ( myDB.insert("Admin_user", null, cv) != -1);
    }

    /**
     * Returns whether this user is an Admin, that is if he appears in the Admin_user table.
     * @param username the username to look for
     * @return True if the user identified by <code>username</code> is an Admin for any Partner, or False otherwise
     */
    public boolean isAdmin(String username){
        int id = getUserID(username);
        return (getElementFromDB("Admin_user", "id_user", "id_user = \""+id+"\"")
                .size() > 0);
    }

    /**
     * Returns the internal ID of the Partner that this user is administrating.
     * @param userId the internal ID of an admin User.
     * @return The ID of the Partner that this User is administrating as an int, or -1 if thhis User is not an administrator.
     */
    public int getAdminFromUser(int userId){
        List<String> list = getElementFromDB("Admin_user", "id_partner", "id_user = \""+userId+"\"");
        if(list.isEmpty()){
            return -1;
        } else {
            return Integer.parseInt(list.get(0));
        }
    }

    /**
     * Returns a <code>List</code> of all the usernames currently present in the local database, as Strings
     * @return a <code>List<String></code> of all the usernames in the database
     */
    public List<String> getAllUsernames(){
        return getElementFromDB("User", "username", null);
    }

    /**
     * Returns a list of BrowsePointsAssociation instances representing all the points the User has earned from all the Partners/Shops.
     * @param username the username (email) to look for
     * @return a list of BrowsePointsAssociation instances. This list might be empty if the User does not currently have any points.
     */
    public List<BrowsePointsAssociation> getAllPoints(String username){
        ArrayList<BrowsePointsAssociation> res = new ArrayList<>();
        Cursor c = this.getEntriesFromDB("User_points",
                null,
                "id_user = \""+getUserID(username)+"\"",
                null);

        if(c.moveToFirst()){
            for(int i=0; i<c.getCount(); i++){
                int shopID = c.getInt(c.getColumnIndex("id_shop"));
                int partnerID = getPartnerID(shopID);
                res.add(new BrowsePointsAssociation(context,
                        partnerID,
                        shopID,
                        c.getInt(c.getColumnIndex("points"))
                ));
                c.moveToNext();
            }
        }
        c.close();
        return res;
    }

    /**
     * Returns a list of BrowseClientsShopDataAssociation instances representing all the points all the Users have earned from this Partner.
     * @param partnerId the Partner to look for
     * @return a list of BrowseClientsShopDataAssociation instances. This list might be empty if no User has earned points from this Partner so far.
     */
    public List<BrowseClientsShopDataAssociation> getAllClientPoints(int partnerId){
        List<BrowseClientsShopDataAssociation> res = new ArrayList<>();

        List<Integer> partnerShops = getAllShopsIDs(partnerId);
        for (int id : partnerShops) {
            res.add(new BrowseClientsShopDataAssociation(context,
                                                            id,
                                                            getAllClientsPointsAtShop(id)));
        }
        return res;
    }

    /**
     * Returns a list of BrowseClientsClientDataAssociation instances representing all the points all the Users have earned from this Shop.
     * @param shopID the Shop to look for
     * @return a list of BrowseClientsClientDataAssociation instances. This list might be empty if no User has earned points from this Shop so far.
     */
    public List<BrowseClientsClientDataAssociation> getAllClientsPointsAtShop(int shopID){
        List<BrowseClientsClientDataAssociation> res = new ArrayList<>();

        List<Integer> users = getAllUserIDs(shopID);
        for (int userID : users) {
            //For each user using this shop :
            res.add(new BrowseClientsClientDataAssociation(context, getUsername(userID), getPoints(userID, shopID)));
        }
        return res;
    }
}
