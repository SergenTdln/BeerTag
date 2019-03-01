package application_projet4_groupe12.entities;

/**
 * Entities of this class can represent any shop, bar or any establishment in which users can earn points.
 */
public class Partner {

    private int id; // Internal ID of the partner, should not be displayed to the user. Unique
    private String name; // Name displayed to the user. Might not be unique
    private String address;
    private String creationDate; //This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
    private String imagePath; //TODO : Should we store all files in the assets folder or "stream" them from the database ?

    // Call SQLHelper.getFreeIDPartner to obtain an available ID to use
    public Partner(int id, String name, String address, String creationDate, String imagePath) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.creationDate = creationDate;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    //TODO : Setter methods should update the DB ?

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
