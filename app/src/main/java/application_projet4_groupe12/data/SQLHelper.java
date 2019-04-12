package application_projet4_groupe12.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import application_projet4_groupe12.activities.browse_clients.BrowseClientsClientDataAssociation;
import application_projet4_groupe12.activities.browse_clients.BrowseClientsShopDataAssociation;
import application_projet4_groupe12.activities.browse_points.BrowsePointsAssociation;
import application_projet4_groupe12.entities.Address;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.Promotion;
import application_projet4_groupe12.entities.Shop;
import application_projet4_groupe12.entities.ShopFrame;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.UnknownUserException;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;

import static application_projet4_groupe12.utils.AppUtils.occurrences;


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
        try {
            String path = DATABASE_PATH + DATABASE_NAME;

            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteCantOpenDatabaseException f){
            //The DB couldn't be opened -> it doesn't exist yet
            //This could be considered abusive use of exceptions (?)
            System.out.println("Database has to be created");
        } catch (SQLiteException e){
            //The DB couldn't be opened -> it doesn't exist yet
            //This could be considered abusive use of exceptions (?)
            System.out.println("Database has to be created.");
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
     * @return An LinkedList<String> containing all the results, or an empty list if there was none
     */
    private LinkedList<String> getElementFromDB(String table, String column, String conditionSQL) throws SQLiteException{
        LinkedList<String> requestResult = new LinkedList<>();
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


    //*****
    //GETTER METHODS
    //*****

    /**
     * Retrieves information on an Address from the database and returns it as an Address instance.
     * @param addressID the internal id of the Address to retrieve
     * @return an Address instance, or null if this id was not present in the database
     */
    public Address getAddress(long addressID){
        Cursor c = getEntriesFromDB("Address", null, "_id = \""+addressID+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() != 1){
                //Duplicate Address in the database
                Toast.makeText(context, "A duplicated entry of Address with ID \""+addressID+"\" was found in the database", Toast.LENGTH_LONG).show();
            }
            Address out = new Address(c.getLong(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("country")),
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
     * Retrieves information on a Partner from the database and returns it as a Partner instance.
     * @param partnerID the internal id of the Partner to retrieve
     * @return a Partner instance, or null if this id was not present in the database
     */
    public Partner getPartner(long partnerID){
        Cursor c = getEntriesFromDB("Partner", null, "_id = \""+partnerID+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() > 1){
                //Duplicate Partner in the database
                Toast.makeText(context, "A duplicated entry of partner with ID \""+partnerID+"\" was found in the database", Toast.LENGTH_LONG).show();
            }
            Partner out = new Partner(c.getLong(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("tva")),
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
     * Retrieves information on a Promotion from the database and returns it as a Promotion instance.
     * @param promotionID the internal id of the Promotion to retrieve
     * @return a Promotion instance, or null if this id was not present in the database
     */
    public Promotion getPromotion(long promotionID){
        Cursor c = getEntriesFromDB("Promotion", null, "_id = \""+promotionID+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() > 1){
                //Duplicate Promotion in the database
                Toast.makeText(context, "A duplicated entry of promotion with ID \""+promotionID+"\" was found in the database", Toast.LENGTH_LONG).show();
            }
            boolean isReusable = (c.getInt(c.getColumnIndex("is_reusable")) == 1);
            boolean isActive = (c.getInt(c.getColumnIndex("active")) == 1);
            Promotion out = new Promotion(c.getLong(c.getColumnIndex("_id")),
                                        c.getLong(c.getColumnIndex("id_partner")),
                                        c.getLong(c.getColumnIndex("id_shop")),
                                        c.getInt(c.getColumnIndex("points_required")),
                                        isReusable,
                                        c.getString(c.getColumnIndex("description")),
                                        c.getString(c.getColumnIndex("image_path")),
                                        isActive,
                                        c.getString(c.getColumnIndex("end_date")));
            c.close();
            return out;
        } else {
            //No such promotion in the database
            c.close();
            return null;
        }
    }

    /**
     * Retrieves information on a picture Frame from the database and returns it as a ShopFrame instance.
     * @param frameID the internal id of the Promotion to retrieve
     * @return a ShopFrame instance, or null if this id was not present in the database
     */
    public ShopFrame getFrame(long frameID){
        Cursor c = getEntriesFromDB("Shop_frame", null, "_id = \""+frameID+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() > 1){
                //Duplicate Frame in the database
                Toast.makeText(context, "A duplicated entry of picture frame with ID \""+frameID+"\" was found in the database", Toast.LENGTH_LONG).show();
            }
            ShopFrame out = new ShopFrame(c.getLong(c.getColumnIndex("_id")),
                                        c.getLong(c.getColumnIndex("id_shop")),
                                        c.getString(c.getColumnIndex("image_path")));
            c.close();
            return out;
        } else {
            //No such frame in the database
            c.close();
            return null;
        }
    }

    /**
     * Retrieves information on a Shop location from the database and returns it as a Shop instance.
     * @param shopID the internal id of the Shop to retrieve
     * @return a Shop instance, or null if this id was not present in the database
     */
    public Shop getShop(long shopID){
        Cursor c = getEntriesFromDB("Shop_location", null, "_id = \""+shopID+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() > 1){
                //Duplicate Shop in the database
                Toast.makeText(context, "A duplicated entry of Shop with ID \""+shopID+"\" was found in the database", Toast.LENGTH_LONG).show();
            }
            Shop out = new Shop(c.getLong(c.getColumnIndex("_id")),
                    c.getLong(c.getColumnIndex("id_partner")),
                    c.getLong(c.getColumnIndex("address")),
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
     * Retrieves information on a User from the database and returns it as an User instance.
     * @param userId the ID of the User to retrieve
     * @return a User instance, or null if this id was not present in the database
     */
    public User getUser(long userId){
        Cursor c = getEntriesFromDB("User", null, "_id = \""+userId+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() > 1){
                //Duplicate User in the database
                Toast.makeText(context, "A duplicated entry of User with id \""+userId+"\" was found in the database", Toast.LENGTH_LONG).show();
            }
            User out = new User(c.getLong(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("username")),
                    c.getString(c.getColumnIndex("password")),
                    c.getString(c.getColumnIndex("created_on")),
                    c.getString(c.getColumnIndex("first_name")),
                    c.getString(c.getColumnIndex("last_name")),
                    c.getString(c.getColumnIndex("birthday")),
                    c.getString(c.getColumnIndex("image_path")),
                    this.isAdmin(c.getString(c.getColumnIndex("username")))
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
     * Retrieves information on a User from the database and returns it as an User instance.
     * @param username the email of the User to retrieve
     * @return a User instance, or null if this username was not present in the database
     */
    public User getUser(String username){
        Cursor c = getEntriesFromDB("User", null, "username = \""+username+"\"", null);
        if(c.moveToFirst()){
            if(c.getCount() > 1){
                //Duplicate User in the database
                Toast.makeText(context, "A duplicated entry of User with username \""+username+"\" was found in the database", Toast.LENGTH_LONG).show();
            }
            //for(int i=0; i<c.getColumnCount(); i++){
            //    System.out.println("Column "+i+" is "+c.getColumnName(i));
            //}
            User out = new User(c.getLong(c.getColumnIndex("_id")),
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
     * Returns the internal ID of the Partner owning/running the Shop passed as argument
     * @param shopID the Shop_location to look for
     * @return an internal Partner ID as a long, or -1 if <code>shopID</code> was not present in the database
     */
    public long getPartnerID(long shopID){
        Cursor c = getEntriesFromDB("Shop_location",
                new String[]{"id_partner"},
                "_id = \""+shopID+"\"",
                null);
        // c should only contain one "cell"
        if(c.moveToFirst()){
            long out = c.getLong(c.getColumnIndex("id_partner"));
            c.close();
            return out;
        } else {
            //This shop does not exist
            c.close();
            return -1;
        }
    }

    /**
     * Returns the internal ID of the user identified by the passed <code>email</code> argument.
     * @param email the email of the user we are looking for
     * @return The ID of this user as a long, or -1 if this <code>email</code> is not present in the database.
     */
    private long getUserID(String email){
        List<String> res = this.getElementFromDB("User", "_id", "username = \""+email+"\"");
        if( res.isEmpty() ){
            //No user with such email was found in the database
            return -1;
        } else {
            return Long.parseLong(res.get(0));
        }
    }

    /**
     * Returns the public username of the user identified by the passed <code>userID</code> argument.
     * @param userID the internal ID of the user we are looking for
     * @return The username of this User as a String, or null if this <code>userID</code> is not present in the database.
     */
    public String getUsername(long userID){
        List<String> res = this.getElementFromDB("User", "username", "_id = \""+userID+"\"");
        if( res.isEmpty() ){
            //No user with such ID was found in the database
            return null;
        } else {
            return res.get(0);
        }
    }

    /**
     * Returns the amount of points the passed User currently has at the passed Shop.
     * @param userID the username (email) to look for
     * @param shopID the internal ID of the shop to look for
     * @return the amount of points the user has earned as a long. Returns 0 if the User has never earned points at this Shop before.
     */
    public int getPoints(long userID, long shopID){
        List<String> res = this.getElementFromDB("User_points",
                "points",
                "id_user = \""+userID+"\" AND id_shop = \""+shopID+"\"");
        int l = res.size();
        if( l == 0 ){
            // No pair userID - shopID was found in the User_points table.
            // This user has never earned points here before.
            return 0;
        } else {
            return Integer.parseInt(res.get(0));
        }
    }

    /**
     * Returns the hashed password of the User identified by <code>email</code>.
     * @param email the username/e-mail of the User to look for
     * @return the hashed password of this User as a String
     */
    public String getHashedPassword(String email) throws UnknownUserException {
        List<String> res = getElementFromDB("User", "password", "username = \""+email+"\"");
        if(res.isEmpty()){
            throw new UnknownUserException("User \""+email+"\" is not present in the database");
        } else {
            return res.get(0);
        }
    }

    /**
     * Retrieves information on an Shop Address from the database and returns it as an Address instance.
     * @param shopID the internal id of the Shop to look for
     * @return an Address instance, or null if this Shop id was not present in the database
     */
    public Address getShopAddress(long shopID){
        List<String> res = getElementFromDB("Shop_location", "id_address", "_id = \""+shopID+"\"");
        if(res.isEmpty()){
            //This shopID is invalid
            return null;
        } else {
            return getAddress(Long.parseLong(res.get(0)));
        }
    }

    /**
     * Returns the internal ID of the Partner that this user is administrating.
     * @param userId the internal ID of an admin User.
     * @return The ID of the Partner that this User is administrating as a long, or -1 if thhis User is not an administrator.
     */
    public long getPartnerIDFromUser(long userId){
        List<String> list = getElementFromDB("Admin_user", "id_partner", "id_user = \""+userId+"\"");
        if(list.isEmpty()){
            return -1;
        } else {
            return Long.parseLong(list.get(0));
        }
    }

    /**
     * Returns a list of the IDs of the Shops that this User has marked as favorites
     * @param userId the User to look for
     * @return a <code>List</code> of <code>Long</code> instances. This <code>List</code> might be empty if this User doesn't have any favorite shops.
     */
    public List<Long> getFavoriteShopsIDs(long userId){
        List<Long> ret = new LinkedList<>();
        List<String> res = getElementFromDB("Favorite_shops", "id_shop", "id_user = \""+userId+"\"");
        for (String s : res) {
            ret.add(Long.parseLong(s));
        }
        return ret;
    }

    /**
     * Returns a list of the Shops that this User has marked as favorites
     * @param userId the User to look for
     * @return a <code>List</code> of <code>Shop</code> instances. This <code>List</code> might be empty if this User doesn't have any favorite shops.
     */
    public List<Shop> getFavoriteShops(long userId){
        List<Shop> ret = new LinkedList<>();
        List<Long> res = getFavoriteShopsIDs(userId);
        for (long id : res) {
            ret.add(getShop(id));
        }
        return ret;
    }

    /**
     * Returns a list of Shop IDs representing all this Partner's physical shops.
     * @param partnerID the partner to look for
     * @return a list of Longs. This list can be empty if this Partner has no Shop registered in the database.
     */
    public List<Long> getAllShopsIDs(long partnerID){
        List<Long> res = new LinkedList<>();
        List<String> shopIds = getElementFromDB("Shop_location", "_id", "id_partner = \""+partnerID+"\"");
        for (String shopID : shopIds) {
            res.add(Long.parseLong(shopID));
        }
        return res;
    }

    /**
     * Returns a list of User IDs representing all this Shop's clients.
     * @param shopID the Shop to look for
     * @return a list of Longs. This list can be empty if this Shop has no client registered in the database.
     */
    public List<Long> getAllClientsUserIDs(long shopID){
        List<Long> res = new LinkedList<>();
        List<String> userIds = getElementFromDB("User_points", "id_user", "id_shop = \""+shopID+"\"");
        for (String userID : userIds){
            res.add(Long.parseLong(userID));
        }
        return res;
    }

    /**
     * Returns a list of Shop instances representing all this Partner's physical shops.
     * @param partnerID the partner to look for
     * @return a list of Shop instances. This list can be empty if this Partner has no Shop registered in the database.
     */
    public List<Shop> getAllShops(long partnerID){
        List<Shop> res = new LinkedList<>();
        List<String> shopIds = getElementFromDB("Shop_location", "_id", "id_partner = \""+partnerID+"\"");
        for (String shopID : shopIds) {
            res.add(this.getShop(Long.parseLong(shopID)));
        }
        return res;
    }

    /**
     * Returns a List of <code>User</code> instances representing all the administrators of the <code>Partner</code> passed in argument.
     * @param partnerID the ID of the <code>Partner</code> to look for
     * @return a List of <code>User</code> instances. This list might be empty if this <code>Partner</code> has no administrator. (Which should seriously be avoided)
     */
    public List<User> getAllAdmins(long partnerID){
        List<User> ret = new LinkedList<User>();
        List<String> adminsIDs = getElementFromDB("Admin_user", "id_user", "id_partner = \""+partnerID+"\"");

        for (String userId : adminsIDs) {
            ret.add(this.getUser(Long.parseLong(userId)));
        }
        return ret;
    }

    /**
     * Returns a <code>List</code> of all the usernames currently present in the local database, as Strings
     * @return a <code>List<String></code> of all the usernames in the database
     */
    public List<String> getAllUsernames(){
        return getElementFromDB("User", "username", null);
    }

    /**
     * Returns a list of all the currently active promotions present in the database as instances of promotion.
     * @return a list of Promotion instances. This list may be empty if there is no currently active promotions in the database.
     */
    public List<Promotion> getAllActivePromotions(){
        List<Promotion> ret = new LinkedList<>();
        Cursor c = getEntriesFromDB("Promotion", null, "active = \"1\"", null);
        if(c.moveToFirst()){
            for (int i=0; i<c.getCount(); i++){
                boolean isReusable = false;
                if(c.getInt(c.getColumnIndex("is_reusable"))==1){isReusable=true;}
                ret.add(new Promotion(c.getLong(c.getColumnIndex("_id")),
                                    c.getLong(c.getColumnIndex("id_partner")),
                                    c.getLong(c.getColumnIndex("id_shop")),
                                    c.getInt(c.getColumnIndex("points_required")),
                                    isReusable,
                                    c.getString(c.getColumnIndex("description")),
                                    c.getString(c.getColumnIndex("image_path")),
                                    true,
                                    c.getString(c.getColumnIndex("end_date"))));
            }
        }
        c.close();
        return ret;
    }

    /**
     * Returns a list of all the currently active promotions at the given shop.
     * @return a list of Promotion instances. This list may be empty if there is no currently active promotions in the database.
     */
    public List<Promotion> getAllActivePromotions(long shopID){
        List<Promotion> ret = new LinkedList<>();
        Cursor c = getEntriesFromDB("Promotion", null, "active = \"1\" AND id_shop = \""+shopID+"\"", null);
        if(c.moveToFirst()){
            for (int i=0; i<c.getCount(); i++){
                boolean isReusable = false;
                if(c.getInt(c.getColumnIndex("is_reusable"))==1){isReusable=true;}
                ret.add(new Promotion(c.getLong(c.getColumnIndex("_id")),
                        c.getLong(c.getColumnIndex("id_partner")),
                        c.getLong(c.getColumnIndex("id_shop")),
                        c.getInt(c.getColumnIndex("points_required")),
                        isReusable,
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("image_path")),
                        true,
                        c.getString(c.getColumnIndex("end_date"))));
            }
        }
        c.close();
        return ret;
    }

    /**
     * Returns a list of all the promotions that the passed User can get, given the information contained in the passed Association.
     * @param user the User to look for
     * @param assoc an instance of <code>BrowsePointsAssociation</code>. This contains info about the points the User has at a given shop.
     * @return a list of <code>Promotion</code> instances. This list might be empty if the User has no available Promotion.
     */
    public ArrayList<Promotion> getAllAvailablePromotions(User user, BrowsePointsAssociation assoc){
        // With this much points at this shop (see data in assoc), what promotions could user get ?
        ArrayList<Promotion> ret = new ArrayList<>();
        List<Promotion> shopPromos = getAllActivePromotions(assoc.getShopID());
        for (Promotion promo : shopPromos) {
            if(promo.getPointsRequired() <= assoc.getPoints()){
                //If the user has enough points for this promo
                ret.add(promo);
            }
        }
        return ret;
    }

    /**
     * Returns a list of BrowsePointsAssociation instances representing all the points the User has earned from all the Partners/Shops.
     * @param username the username (email) to look for
     * @return a list of BrowsePointsAssociation instances. This list might be empty if the User does not currently have any points.
     */
    public List<BrowsePointsAssociation> getAllPoints(String username){
        List<BrowsePointsAssociation> res = new ArrayList<>();
        Cursor c = this.getEntriesFromDB("User_points",
                null,
                "id_user = \""+getUserID(username)+"\"",
                null);

        if(c.moveToFirst()){
            for(int i=0; i<c.getCount(); i++){
                long shopID = c.getLong(c.getColumnIndex("id_shop"));
                long partnerID = getPartnerID(shopID);
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
    public List<BrowseClientsShopDataAssociation> getAllClientPoints(long partnerId){
        List<BrowseClientsShopDataAssociation> res = new ArrayList<>();

        List<Long> partnerShops = getAllShopsIDs(partnerId);
        for (long id : partnerShops) {
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
    public List<BrowseClientsClientDataAssociation> getAllClientsPointsAtShop(long shopID){
        List<BrowseClientsClientDataAssociation> res = new ArrayList<>();

        List<Long> users = getAllClientsUserIDs(shopID);
        for (long userID : users) {
            //For each user using this shop :
            res.add(new BrowseClientsClientDataAssociation(context, getUsername(userID), getPoints(userID, shopID)));
        }
        return res;
    }


    //*****
    //PRESENCE-CHECK METHODS
    //*****

    /**
     * Returns whether this address is present in the database
     * @param addressID the internal ID of the address to look for
     * @return True if this address already exists, False otherwise
     */
    public boolean doesAddressExist(long addressID){
        boolean out;
        Cursor c = getEntriesFromDB("Address",
                new String[]{"_id"},
                "_id = \""+addressID+"\"",
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
    public boolean doesPartnerExist(long partnerID){
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
     * Returns whether this partner is already present in the database
     * @param tvaNumber the tva number of the partner to look for
     * @return True if this tvaNumber already exists, False otherwise
     */
    public boolean doesPartnerExist(String tvaNumber){
        boolean out;
        Cursor c = getEntriesFromDB("Partner",
                new String[]{"name"},
                "tva = \""+tvaNumber+"\"",
                null);
        out = (c.moveToFirst());
        c.close();
        return out;
    }

    /**
     * Returns whether this promotion is present in the database
     * @param promotionID the internal ID of the promotion to look for
     * @return True if this promotion already exists, False otherwise
     */
    public boolean doesPromotionExist(long promotionID){
        boolean out;
        Cursor c = getEntriesFromDB("Promotion",
                new String[]{"_id"},
                "_id = \""+promotionID+"\"",
                null);
        out = (c.moveToFirst());
        c.close();
        return out;
    }

    /**
     * Returns whether this picture frame is present in the database
     * @param frameID the internal ID of the frame to look for
     * @return True if this frame already exists, False otherwise
     */
    public boolean doesFrameExist(long frameID){
        boolean out;
        Cursor c = getEntriesFromDB("Shop_frame",
                new String[]{"_id"},
                "_id = \""+frameID+"\"",
                null);
        out = (c.moveToFirst());
        c.close();
        return out;
    }

    /**
     * Returns whether this shop is present in the database
     * @param shopID the internal ID of the shop to look for
     * @return True if this shop already exists, False otherwise
     */
    public boolean doesShopExist(long shopID){
        boolean out;
        Cursor c = getEntriesFromDB("Shop_location",
                new String[]{"_id"},
                "_id = \""+shopID+"\"",
                null);
        out = (c.moveToFirst());
        c.close();
        return out;
    }

    /**
     * Returns whether this email/username is already present in the database
     * @param email the email/username of the User to look for
     * @return True if this username already exists, False otherwise
     */
    public boolean doesUserExist(String email){
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
     * Returns whether this user is already present in the database
     * @param userID the longernal ID of the User to look for
     * @return True if this user already exists, False otherwise
     */
    public boolean doesUserExist(long userID){
        boolean out;
        Cursor c = getEntriesFromDB("User",
                new String[]{"_id"},
                "_id = \""+userID+"\"",
                null);
        out = (c.moveToFirst());
        c.close();
        return out;
    }

    /**
     * Returns whether this user is an Admin, that is if he appears in the Admin_user table.
     * @param username the username to look for
     * @return True if the user identified by <code>username</code> is an Admin for any Partner, or False otherwise
     */
    public boolean isAdmin(String username){
        long id = getUserID(username);
        return (getElementFromDB("Admin_user", "id_user", "id_user = \""+id+"\"")
                .size() > 0);
    }

    /**
     * Returns whether this Promotion is reusable.
     * @param promoID the internal ID of the Promotion to look for
     * @return True if this Promotion is reusable, or False otherwise
     */
    public boolean isReusable(long promoID){
        List<String> res = getElementFromDB("Promotion", "is_reusable", "_id = \""+promoID+"\"");
        return (Integer.parseInt(res.get(0))==1);
    }

    //*****
    //VALIDITY METHODS
    //*****

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
    public static boolean isValidDate(String date){
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


    //*****
    //INSERTION/CREATION METHODS
    //*****

    /**
     * Inserts a new User in the database.
     * @param user a valid instance of User to insert in the database
     * @throws WrongEmailFormatException if user.username does not follow a valid email-address format
     * @throws WrongDateFormatException if user.creationDate does not follow the (DD/MM/YYYY) format
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     * For example, this could be caused by an SQLite error OR because this username was already present : this means that you should always
     * call <code>doesUserExist()</code> BEFORE trying to insert the user.
     */
    public boolean addUser(User user) throws WrongEmailFormatException, WrongDateFormatException {
        if(! isValidEmail(user.getUsername())){
            throw new WrongEmailFormatException(user.getUsername() + " is not a valid email address");
        }
        if ( !isValidDate(user.getCreationDate()) ){
            throw new WrongDateFormatException(user.getCreationDate() + " is not a valid date format.");
        }
        if( !isValidDate(user.getBirthday()) ){
            throw new WrongDateFormatException(user.getBirthday() + " is not a valid date format.");
        }
        if(doesUserExist(user.getUsername())){
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
    public boolean addPartner(Partner partner) throws WrongDateFormatException {
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
     * Inserts a new Promotion in the database.
     * @param promotion a valid instance of Promotion to insert in the database
     * @return True if the insertion was successful, False if it failed;
     * For example, this could be caused by an SQLite error OR because this promotion was already present : this means that you should always
     * call <code>doesPromotionExist()</code> BEFORE trying to insert the promotion.
     */
    public boolean addPromotion(Promotion promotion){
        if(doesPromotionExist(promotion.getId())){
            return false;
        }

        ContentValues cv = new ContentValues();
        cv.put("\"_id\"", promotion.getId());
        cv.put("\"id_partner\"", promotion.getIdPartner());
        cv.put("\"id_shop\"", promotion.getIdShop());
        cv.put("\"points_required\"", promotion.getPointsRequired());
        int isReusable; if(promotion.isReusable()){isReusable=1;}else{isReusable=0;}
        cv.put("\"is_reusable\"", isReusable);
        cv.put("\"description\"", promotion.getDescription());
        cv.put("\"image_path\"", promotion.getImagePath());
        int isActive; if(promotion.isActive()){isActive=1;}else{isActive=0;}
        cv.put("\"active\"", isActive);
        cv.put("\"end_date\"", promotion.getEndDate());

        return (myDB.insert("Promotion", null, cv) != -1);

        //TODO Si des utilisateurs ont souscrit aux notifs pour ce bar, cette méthode leur envoie tous une notif
    }

    /**
     * Adds an association between an existing User and a Partner.
     * @param username the User's username/e-mail address as a String
     * @param partnerID the Partner's ID in the local database as a long
     * @return True if the operation succeeded, false otherwise
     */
    public boolean addAdmin(String username, long partnerID) throws UnknownUserException {
        if(! doesUserExist(username)){
            throw new UnknownUserException("User with username \""+username+"\" does not exist.");
        }

        ContentValues cv = new ContentValues();
        cv.put("\"id_user\"", getUserID(username));
        cv.put("\"id_partner\"", partnerID);

        return ( myDB.insert("Admin_user", null, cv) != -1);
    }

    /**
     * Adds <code>amount</code> points to the User's account.
     * @param username the user's email address
     * @param amount the amount of points to add. This can be positive or negative.
     * @param shopID please note this is different from the partner's name.
     * @return True if the insertion was successful, False if it failed (for any reason not covered by a thrown exception).
     */
    public boolean addPoints(String username, int amount, long shopID){

        int currentPoints = this.getPoints(getUserID(username), shopID);

        ContentValues cv = new ContentValues();
        cv.put("\"id_user\"", getUserID(username));
        cv.put("\"id_shop\"", shopID);
        cv.put("\"points\"", (currentPoints + amount));

        return (myDB.insert("User_points", null, cv) != -1);
    }

    /**
     * Notifies the DB that this User has used the passed Promotion.
     * @param userID the user's internal ID.
     * @param promoID the promotion's internal ID.
     * @return True if the insertion was successful, False if it failed.
     */
    private boolean addUserPromotion(long userID, long promoID, String date){
        ContentValues cv = new ContentValues();
        cv.put("id_user", userID);
        cv.put("id_promotion", promoID);
        cv.put("used_on", date);
        return (myDB.insert("User_promotion", null, cv) != -1);
    }

    /**
     *Updates the values of an existing Address in the database. Its _id is the only field that can not be updated
     * @param address an instance of Address containing the new values
     * @return True if the row was successfully affected, False otherwise
     */
    public boolean updateAddressData(Address address){
        ContentValues cv = new ContentValues();
        cv.put("\"country\"", address.getCountry());
        cv.put("\"city\"", address.getCity());
        cv.put("\"street\"", address.getStreet());
        cv.put("\"numbers\"", address.getNumbers());

        return (myDB.update("Address", cv, "_id = \""+address.getId()+"\"", null) >= 1);
    }

    /**
     *Updates the values of an existing Partner in the database. Its _id is the only field that can not be updated
     * @param partner an instance of Partner containing the new values
     * @return True if the row was successfully affected, False otherwise
     */
    public boolean updatePartnerData(Partner partner){
        ContentValues cv = new ContentValues();
        cv.put("\"name\"", partner.getName());
        cv.put("\"address\"", partner.getAddress());
        cv.put("\"created_on\"", partner.getCreationDate());
        cv.put("\"image_path\"", partner.getImagePath());

        return (myDB.update("Partner", cv, "_id = \""+partner.getId()+"\"", null) >= 1);
    }

    /**
     *Updates the values of an existing Promotion in the database. Its _id is the only field that can not be updated
     * @param promotion an instance of Promotion containing the new values
     * @return True if the row was successfully affected, False otherwise
     */
    public boolean updatePromotionData(Promotion promotion){
        ContentValues cv = new ContentValues();
        cv.put("\"id_partner\"", promotion.getIdPartner());
        cv.put("\"id_shop\"", promotion.getIdShop());
        cv.put("\"points_required\"", promotion.getPointsRequired());
        int isReusable; if(promotion.isReusable()){isReusable=1;}else{isReusable=0;}
        cv.put("\"is_reusable\"", isReusable);
        cv.put("\"description\"", promotion.getDescription());
        cv.put("\"image_path\"", promotion.getImagePath());
        int isActive; if(promotion.isActive()){isActive=1;}else{isActive=0;}
        cv.put("\"active\"", isActive);
        cv.put("\"end_date\"", promotion.getEndDate());

        return (myDB.update("Promotion", cv, "_id = \""+promotion.getId()+"\"", null) >= 1);
    }

    /**
     *Updates the values of an existing User in the database. Its _id is the only field that can not be updated
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
     *Updates the values of an existing picture Frame in the database. Its _id is the only field that can not be updated
     * @param frame an instance of ShopFrame containing the new values
     * @return True if the row was successfully affected, False otherwise
     */
    public boolean updateFrameData(ShopFrame frame){
        ContentValues cv = new ContentValues();
        cv.put("\"id_shop\"", frame.getIdShop());
        cv.put("\"image_path\"", frame.getFilePath());

        return (myDB.update("Shop_frame", cv, "_id = \""+frame.getId()+"\"", null) >= 1);
    }

    /**
     *Updates the values of an existing picture Shop in the database. Its _id is the only field that can not be updated
     * @param shop an instance of Shop containing the new values
     * @return True if the row was successfully affected, False otherwise
     */
    public boolean updateShopData(Shop shop){
        ContentValues cv = new ContentValues();
        cv.put("\"id_partner\"", shop.getPartnerID());
        cv.put("\"id_address\"", shop.getAddressID());
        cv.put("\"description\"", shop.getDescription());
        cv.put("\"created_on\"", shop.getCreationDate());

        return (myDB.update("Shop_location", cv, "_id = \""+shop.getId()+"\"", null) >= 1);
    }


    //*****
    //DELETION METHODS
    //*****

    /**
     * Simulate the consumption of the Promotion with ID <code>promoID</code> by the passed User. If this Promotion was not reusable, it is deleted from the database.
     * @param promoID the ID of the Promotion to delete
     * @param user the User who activated this Promotion
     * @return
     */
    public boolean usePromotion(long promoID, User user, String date){
        if(! isReusable(promoID)) {
            removePromotion(promoID);
        }
        //TODO call addPoints(- X points) @Martin
        return addUserPromotion(user.getId(), promoID, date);
    }

    /**
     * Removes a Promotion from the database. Use this method wisely.
     * @param promoID the Promotion's ID in the local database as a long
     * @return True if the operation succeeded, false otherwise
     */
    private boolean removePromotion(long promoID){
        return (myDB.delete("Promotion", "_id = \""+promoID+"\"", null) > 0);
    }

    /**
     * Removes an association between an existing User and a Partner.
     * @param username the User's username/e-mail address as a String
     * @param partnerID the Partner's ID in the local database as a long
     * @return True if the operation succeeded, false otherwise
     */
    public boolean removeAdmin(String username, long partnerID) throws UnknownUserException {
        if(! doesUserExist(username)){
            throw new UnknownUserException("User with username \""+username+"\" does not exist.");
        }
        return (myDB.delete("Admin_user", "id_user = \""+this.getUserID(username)+"\" AND id_partner = \""+partnerID+"\"", null) > 0);
    }


    //*****
    //ID GENERATION METHODS
    //*****

    /**
     * Generates an ID from the current date, time and timezone. This allows to create a number that is almost guaranteed to be unique.
     * @return a candidate for an unique ID, as a long
     */
    private long genID(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"));
        //int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int millisec = cal.get(Calendar.MILLISECOND);
        String tzCode = TimeZone.getDefault().getDisplayName();
        char tzb = tzCode.charAt(0);
        char tze = tzCode.charAt(tzCode.length()-1);

        StringBuilder sb = new StringBuilder();
        sb.append(month).append(day).append(hour).append(min).append(sec).append(millisec);
        return Long.parseLong(sb.toString());
    }

    /**
     * Returns an unused id available for a new Address
     * @return an unused ID as a long
     */
    public long getFreeIDAddress(){
        long candidate = genID();
        while (doesAddressExist(candidate)){
            candidate = genID();
        }
        return candidate;
    }

    /**
     * Returns an unused id available for a new Partner
     * @return an unused ID as a long
     */
    public long getFreeIDPartner() {
        long candidate = genID();
        while (doesPartnerExist(candidate)){
            candidate = genID();
        }
        return candidate;
    }

    /**
     * Returns an unused id available for a new Promotion
     * @return an unused ID as a long
     */
    public long getFreeIDPromotion() {
        long candidate = genID();
        while (doesPromotionExist(candidate)){
            candidate = genID();
        }
        return candidate;
    }

    /**
     * Returns an unused id available for a new Frame
     * @return an unused ID as a long
     */
    public long getFreeIDFrame() {
        long candidate = genID();
        while (doesFrameExist(candidate)){
            candidate = genID();
        }
        return candidate;
    }

    /**
     * Returns an unused id available for a new Shop
     * @return an unused ID as a long
     */
    public long getFreeIDShop() {
        long candidate = genID();
        while (doesShopExist(candidate)){
            candidate = genID();
        }
        return candidate;
    }

    /**
     * Returns an unused id available for a new User
     * @return an unused ID as a long
     */
    public long getFreeIDUser(){
        long candidate = genID();
        while (doesUserExist(candidate)){
            candidate = genID();
        }
        return candidate;
    }
}
