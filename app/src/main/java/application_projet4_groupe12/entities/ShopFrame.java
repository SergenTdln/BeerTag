package application_projet4_groupe12.entities;

public class ShopFrame {

    private int id;
    private int idShop;
    String filePath;

    public ShopFrame(int id, int idShop, String filePath){
        this.id = id;
        this.idShop = idShop;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public int getIdShop() {
        return idShop;
    }

    public String getFilePath() {
        return filePath;
    }

    //TODO : Setter methods should update the DB ?

    public void setId(int id) {
        this.id = id;
    }

    public void setIdShop(int idShop) {
        this.idShop = idShop;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
