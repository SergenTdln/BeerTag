package application_projet4_groupe12.entities;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;

/**
 * Entities of this class can represent any shop, bar or any establishment in which users can earn points.
 */
public class Partner {

    private long id; // Internal ID of the partner, should not be displayed to the user. Unique
    private String tvaNumber; //Unique
    private String name; // Name displayed to the user. Might not be unique
    private String address;
    private String creationDate; //This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")
    private String imagePath; //Image path inside of the assets folder

    public Partner(){}

    // Call SQLHelper.getFreeIDPartner() to obtain an available ID to use
    public Partner(long id, String tvaNumber, String name, String address, String creationDate, String imagePath) {
        this.id = id;
        this.tvaNumber = tvaNumber;
        this.name = name;
        this.address = address;
        this.creationDate = creationDate;
        this.imagePath = imagePath;
    }

    //******
    //Getter and setter methods
    //******
    public long getId() {
        return id;
    }

    public String getTvaNumber(){
        return tvaNumber;
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

    public void setId(Context c, long id) {
        this.id = id;
        this.refreshDB(c);
    }

    public void setTvaNumber(Context c, String tvaNumber) {
        this.tvaNumber = tvaNumber;
        this.refreshDB(c);
    }

    public void setName(Context c, String name) {
        this.name = name;
        this.refreshDB(c);
    }

    public void setAddress(Context c, String address) {
        this.address = address;
        this.refreshDB(c);
    }

    public void setCreationDate(Context c, String creationDate) {
        this.creationDate = creationDate;
        this.refreshDB(c);
    }

    public void setImagePath(Context c, String imagePath) {
        this.imagePath = imagePath;
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
            if(! db.updatePartnerData(this)){
                Toast.makeText(c, "Database update did not work. Please try again", Toast.LENGTH_SHORT).show();
            }
            FirebaseFirestore dab = FirebaseFirestore.getInstance();
            dab.collection("Partner").document(String.valueOf(id)).set(this, SetOptions.merge());
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
