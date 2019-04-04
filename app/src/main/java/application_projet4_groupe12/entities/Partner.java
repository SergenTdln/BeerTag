package application_projet4_groupe12.entities;

/**
 * Entities of this class can represent any shop, bar or any establishment in which users can earn points.
 */
public class Partner {

    private long id; // Internal ID of the partner, should not be displayed to the user. Unique
    private String name; // Name displayed to the user. Might not be unique
    private long addressID;
    private String creationDate; //This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
    private String imagePath; //Image path inside of the assets folder

    // Call SQLHelper.getFreeIDPartner() to obtain an available ID to use
    public Partner(long id, String name, long addressID, String creationDate, String imagePath) {
        this.id = id;
        this.name = name;
        this.addressID = addressID;
        this.creationDate = creationDate;
        this.imagePath = imagePath;
    }

    //******
    //Getter and setter methods
    //******
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getAddressID() {
        return addressID;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    //TODO : Setter methods should update the DB ?

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddressID(long addressID) {
        this.addressID = addressID;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
