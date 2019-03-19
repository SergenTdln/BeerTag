package application_projet4_groupe12.entities;

/**
 * Physical shop
 */
public class Shop {

    private int id;
    private int partnerID;
    private int addressID;
    private String description;
    private String creationDate;

    public Shop(int id, int partnerID, int addressID, String description, String creationDate){
        this.id = id;
        this.partnerID = partnerID;
        this.addressID = addressID;
        this.description = description;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public int getPartnerID() {
        return partnerID;
    }

    public int getAddressID() {
        return addressID;
    }

    public String getDescription() {
        return description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    //TODO : Setter methods should update the DB ?

    public void setId(int id) {
        this.id = id;
    }

    public void setPartnerID(int partnerID) {
        this.partnerID = partnerID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
