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
    private int oauthid; // What object should this be ? //TODO read doc
    private String username; // Username/email of the user. Unique
    private String creationDate; // This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
    private String firstName;
    private String lastName;

    // Call SQLHelper.getFreeIDUser to obtain an available ID to use
    public User(int id, int oauthid, String username, String creationDate, String firstName, String lastName) {
        this.id = id;
        this.oauthid = oauthid;
        this.username = username;
        this.creationDate = creationDate;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Connects the User <code>u</code> to his account in the application. The previous user must have been disconnected
     * @param u the User instance to connect
     * @return True if the user was connected, or False if another user was already connected
     */
    public boolean connectUser(User u){
        if(connectedUser!=null){
            return false;
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

    public int getOauthid() {
        return oauthid;
    }

    public String getUsername() {
        return username;
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

    public String getFullName(){
        return this.firstName+" "+this.lastName;
    }

    //TODO : Setter methods should update the DB ?

    public void setId(int id) {
        this.id = id;
    }

    public void setOauthid(int oauthid) {
        this.oauthid = oauthid;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
