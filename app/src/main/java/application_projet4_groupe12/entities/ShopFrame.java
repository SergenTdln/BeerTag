package application_projet4_groupe12.entities;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;

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

    public void setId(Context c, long id) {
        this.id = id;
        this.refreshDB(c);
    }

    public void setIdShop(Context c, long idShop) {
        this.idShop = idShop;
        this.refreshDB(c);
    }

    public void setFilePath(Context c, String filePath) {
        this.filePath = filePath;
        this.refreshDB(c);
    }


    /**
     * Updates the DB with the newly inserted values
     * @param c the Context used to instantiate the database helper.
     */
    private void refreshDB(Context c){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            if(! db.updateFrameData(this)){
                Toast.makeText(c, "Database update did not work. Please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(c, "Could not update the database. Please try again", Toast.LENGTH_SHORT).show();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }
}
