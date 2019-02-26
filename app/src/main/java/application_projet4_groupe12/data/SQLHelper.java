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
     * @throws
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
            //This could be considered abusive use of exceptions
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
     * Ex: The name of all members older than 30 => getElementFromDB("Member", "Name", "Age>30")
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

    private String getUserID(String email){
        //TODO
        return null;
    }

    public boolean doesUsernameExist(String email){
        //TODO
        return false;
    }

    private boolean isValidEmail(String email){
        //TODO
        return true;
    }

    private boolean isValidDate(String date){
        //TODO
        return true;
    }

    private boolean doesPartnerExist(String partnerID){
        //TODO
        return false;
    }

    /**
     * Inserts a new User in the database.
     * @param username user's email address.
     * @param firstName user's first name
     * @param lastName user's last name
     * @param creationDate current system date. This has to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     * For example, this could be caused by an SQLite error OR because this username was already present : this means that you should always
     * call <code>doesUsernameExist()</code> BEFORE trying to insert the user.
     */
    public boolean createUser(String username, String firstName, String lastName, String creationDate) throws WrongEmailFormatException, WrongDateFormatException {
        if(! isValidEmail(username)){
            throw new WrongEmailFormatException(username + " is not a valid email address");
        }
        if (! isValidDate(creationDate)){
            throw new WrongDateFormatException(creationDate + " is not a valid date format.");
        }
        if(doesUsernameExist(username)){
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put("\"username\"", username);
        cv.put("\"created_on\"", creationDate);
        cv.put("\"first_name\"", firstName);
        cv.put("\"last_name\"", lastName);

        return (myDB.insert("User", null, cv) != -1);
    }

    /**
     * Inserts a new Partner into the database.
     * @param name partner's public name
     * @param address partner's official address. This might be different from its shop's address.
     * @param creationDate current system date. This has to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     */
    public boolean createPartner(String name, String address, String creationDate) throws WrongDateFormatException {
        if (! isValidDate(creationDate)){
            throw new WrongDateFormatException(creationDate + " is not a valid date format.");
        }

        ContentValues cv = new ContentValues();
        cv.put("\"name\"", name);
        cv.put("\"address\"", address);
        cv.put("\"created_on\"", creationDate);

        return (myDB.insert("Partner", null, cv) != -1);
    }

    /**
     * Adds <code>amount</code> points to the User's account.
     * @param username the user's email address
     * @param amount the amount of points to add. This can be positive or negative.
     * @param partnerID Please not this is different from the partner's name.
     * @param validity_span Validity span of those points, in days. Passing 0 will make them last forever //TODO handle this better ? (later)
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     * @throws UnknownPartnerException
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
     *
     * @param username
     * @param partnerID
     * @return
     */
    public int getPoints(String username, String partnerID){
        //TODO use private getElementFromDB methods
        return 0;
    }
}
