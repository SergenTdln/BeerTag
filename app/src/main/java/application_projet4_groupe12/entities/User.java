package application_projet4_groupe12.entities;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.exceptions.WrongDateFormatException;

/**
 * Entities of this class represent users of the app.
 */
public class User {

    public static User connectedUser;

    private long id; // Internal ID, should not be displayed to the user. Unique
    private String username; // Username/email of the user. Unique
    private String hashedPassword; //Output of hashing function
    private String creationDate; // This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
    private String firstName;
    private String lastName;
    private String birthday; //Birth date. This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
    private String imagePath; //Image path inside of the assets folder

    private boolean isAdmin;

    public User(){}

    // Call SQLHelper.getFreeIDUser to obtain an available ID to use
    public User(long id, String username, String hashedPassword, String creationDate, String firstName, String lastName, String birthday, String imagePath, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.creationDate = creationDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.imagePath = imagePath;
        this.isAdmin = isAdmin;
    }

    /**
     * Connects the User <code>u</code> to his account in the application.
     * @param u the User instance to connect
     * @return True if the user was connected
     */
    public static boolean connectUser(Context c, User u){
        if(connectedUser!=null){
            disconnectUser(c);
            connectedUser = u;
            return true;
        } else {
            connectedUser = u;
            return true;
        }
    }

    /**
     * Disconnects the currently connected User from the application. Its data is refreshed in the database before disconnecting.
     * @return True if the User was successfully disconnected from the application
     */
    public static boolean disconnectUser(Context c){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            db.updateUserData(connectedUser);
        } catch (IOException | SQLiteException e){
            //Do nothing: worst case scenario is the data from the user on this session not being saved.
        } finally {
            if(db!=null) {
                db.close();
            }
        }
        connectedUser = null;
        return true;
    }

    public static boolean isAdmin(Context c, String username){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            return db.isAdmin(username);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            if(db != null) {
                db.close();
            }
        }
    }

    /**
     * If this User is an administrator, returns the Partner instance this User administrates.
     * @return The administrated Partner instance, or null if this user is not an administrator
     */
    public Partner getAdministratedPartner(Context c){
        if(this.isAdmin){
            SQLHelper db = null;
            try {
                db = new SQLHelper(c);
                return db.getPartner(db.getPartnerIDFromUser(this.id));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(c, "Could not retrieve Partner from database.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the usernames/e-mail addresses of all the User instances in the passed <code>List</code>.
     * @param users a <code>List</code> of User instances. This list may be empty.
     * @return a <code>List</code> of Strings containing the usernames of the passed User instances, in the same order as the original <code>List</code>
     */
    public static List<String> getUsernames(@NonNull List<User> users){
        List<String> ret = new LinkedList<>();
        for (User user : users) {
            ret.add(user.getUsername());
        }
        return ret;
    }

    //******
    //Getter and setter methods
    //******
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHashed(){
        return hashedPassword;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthday(){
        return birthday;
    }

    public String getImagePath(){
        return imagePath;
    }

    public String getFullName(){
        return this.firstName+" "+this.lastName;
    }

    public boolean isAdmin(){
        return this.isAdmin;
    }

    public void setId(Context c, long id) {
        this.id = id;
        this.refreshDB(c);
    }

    public void setUsername(Context c, String username) {
        this.username = username;
        this.refreshDB(c);
    }

    public void setPasswordHashed(Context c, String hashedPassword){
        this.hashedPassword = hashedPassword;
        this.refreshDB(c);
    }

    public void setCreationDate(Context c, String creationDate) {
        this.creationDate = creationDate;
        this.refreshDB(c);
    }

    public void setFirstName(Context c, String firstName) {
        this.firstName = firstName;
        this.refreshDB(c);
    }

    public void setLastName(Context c, String lastName) {
        this.lastName = lastName;
        this.refreshDB(c);
    }

    public void setBirthday(Context c, String birthdate) throws WrongDateFormatException {
        if(! SQLHelper.isValidDate(birthdate)){
            throw new WrongDateFormatException("Invalid date format");
        }
        this.birthday = birthdate;
        this.refreshDB(c);
    }

    public void setImagePath(Context c, String imagePath){
        this.imagePath = imagePath;
        this.refreshDB(c);
    }

    /**
     * Updates the DB with the newly inserted values
     * @param c the Context used to instantiate the database helper.
     */
    private void refreshDB(Context c){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            if(! db.updateUserData(this)){
                Toast.makeText(c, "Database update did not work. Please try again", Toast.LENGTH_SHORT).show();
            }
            FirebaseFirestore dab = FirebaseFirestore.getInstance();
            dab.collection("User").document(String.valueOf(id)).set(this, SetOptions.merge());
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(c, "Could not update the database. Please try again", Toast.LENGTH_SHORT).show();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }
}
