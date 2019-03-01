package application_projet4_groupe12.entities;

/**
 * Entities of this class represent users of the app.
 */
public class User {

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
