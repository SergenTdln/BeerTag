package application_projet4_groupe12.entities;

public class ShopFrame {

    private long id;
    private long idShop;
    String filePath;

    public ShopFrame(long id, long idShop, String filePath){
        this.id = id;
        this.idShop = idShop;
        this.filePath = filePath;
    }

    public long getId() {
        return id;
    }

    public long getIdShop() {
        return idShop;
    }

    public String getFilePath() {
        return filePath;
    }

    //TODO : Setter methods should update the DB ?

    public void setId(long id) {
        this.id = id;
    }

    public void setIdShop(long idShop) {
        this.idShop = idShop;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
