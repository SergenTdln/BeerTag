package application_projet4_groupe12.entities;

public class Promotion {

    private int id; //Unique ID of the promotion
    private int idPartner;
    private int idShop;
    private int pointsRequired;
    private boolean isReusable;
    private String description;
    private String imagePath; //Image path inside of the assets folder
    private boolean active; //Is the promotion currently active
    private String endDate; //This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")

    public Promotion(int id, int idPartner, int idShop, int pointsRequired, boolean isReusable, String description, String imagePath, boolean active, String endDate){
        this.id = id;
        this.idPartner = idPartner;
        this.idShop = idShop;
        this.pointsRequired = pointsRequired;
        this.isReusable = isReusable;
        this.description = description;
        this.imagePath = imagePath;
        this.active = active;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public int getIdPartner() {
        return idPartner;
    }

    public int getIdShop() {
        return idShop;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public boolean isReusable() {
        return isReusable;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isActive() {
        return active;
    }

    public String getEndDate() {
        return endDate;
    }

    //TODO : Setter methods should update the DB ?

    public void setId(int id) {
        this.id = id;
    }

    public void setIdPartner(int idPartner) {
        this.idPartner = idPartner;
    }

    public void setIdShop(int idShop) {
        this.idShop = idShop;
    }

    public void setPointsRequired(int pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public void setReusable(boolean reusable) {
        isReusable = reusable;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
