package application_projet4_groupe12.entities;

/**
 * Physical shop
 */
public class Shop {

    private long id;
    private long partnerID;
    private long addressID;
    private String description;
    private String creationDate;

    public Shop(long id, long partnerID, long addressID, String description, String creationDate){
        this.id = id;
        this.partnerID = partnerID;
        this.addressID = addressID;
        this.description = description;
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public long getPartnerID() {
        return partnerID;
    }

    public long getAddressID() {
        return addressID;
    }

    public String getDescription() {
        return description;
    }

    public String getCreationDate() {
        return creationDate;
    }

    //TODO : Setter methods should update the DB ?

    public void setId(long id) {
        this.id = id;
    }

    public void setPartnerID(long partnerID) {
        this.partnerID = partnerID;
    }

    public void setAddressID(long addressID) {
        this.addressID = addressID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
