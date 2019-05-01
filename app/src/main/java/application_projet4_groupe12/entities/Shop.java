package application_projet4_groupe12.entities;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;

/**
 * Physical shop
 */
public class Shop {

    private long id;
    private long partnerID;
    private long addressID;
    private String description;
    private String creationDate;

    public Shop(){}

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

    public void setId(Context c, long id) {
        this.id = id;
        this.refreshDB(c);
    }

    public void setPartnerID(Context c, long partnerID) {
        this.partnerID = partnerID;
        this.refreshDB(c);
    }

    public void setAddressID(Context c, long addressID) {
        this.addressID = addressID;
        this.refreshDB(c);
    }

    public void setDescription(Context c, String description) {
        this.description = description;
        this.refreshDB(c);
    }

    public void setCreationDate(Context c, String creationDate) {
        this.creationDate = creationDate;
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
            if(! db.updateShopData(this)){
                Toast.makeText(c, "Database update did not work. Please try again", Toast.LENGTH_SHORT).show();
            }
            FirebaseFirestore dab = FirebaseFirestore.getInstance();
            dab.collection("Shop_location").document(String.valueOf(id)).set(this, SetOptions.merge());
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
