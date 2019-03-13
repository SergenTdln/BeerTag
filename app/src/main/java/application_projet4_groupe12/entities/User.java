package application_projet4_groupe12.entities;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;

/**
 * Entities of this class represent users of the app.
 */
public class User {

    public static User connectedUser;

    private int id; // Internal ID, should not be displayed to the user. Unique
    private String username; // Username/email of the user. Unique
    private String hashedPassword; //Output of hashing function
    private String creationDate; // This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
    private String firstName;
    private String lastName;
    private String birthday; //Birth date. This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
    private String imagePath; //Image path inside of the assets folder

    // Call SQLHelper.getFreeIDUser to obtain an available ID to use
    public User(int id, String username, String hashedPassword, String creationDate, String firstName, String lastName, String birthday, String imagePath) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.creationDate = creationDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.imagePath = imagePath;
    }

    /**
     * Connects the User <code>u</code> to his account in the application.
     * @param u the User instance to connect
     * @return True if the user was connected
     */
    public static boolean connectUser(Context c, User u){
        if(connectedUser!=null){
            return (u.disconnectUser(c));
        } else {
            connectedUser = u;
            return true;
        }
    }

    /**
     * Disconnects the currently connected User from the application. Its data is refreshed in the database before disconnecting.
     * @return True if the User was successfully disconnected from the application
     */
    public boolean disconnectUser(Context c){
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


    //******
    //Getter and setter methods
    //******
    public int getId() {
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

    //TODO : Setter methods should update the DB ?

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHashed(String hashedPassword){
        this.hashedPassword = hashedPassword;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthday(String birthdate){
        this.birthday = birthdate;
    }

    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }
}
