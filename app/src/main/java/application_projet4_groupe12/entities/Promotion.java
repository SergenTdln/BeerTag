package application_projet4_groupe12.entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;

public class Promotion implements Parcelable {

    private long id; //Unique ID of the promotion
    private long idPartner;
    private long idShop;
    private int pointsRequired;
    private boolean isReusable;
    private String description;
    private String imagePath; //Image path inside of the assets folder
    private boolean active; //Is the promotion currently active
    private String endDate; //This HAS to follow this format : DD/MM/YYYY. (Example: "31/01/2000")

    public Promotion(long id, long idPartner, long idShop, int pointsRequired, boolean isReusable, String description, String imagePath, boolean active, String endDate){
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

    public long getId() {
        return id;
    }

    public long getIdPartner() {
        return idPartner;
    }

    public long getIdShop() {
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

    public void setId(Context c, long id) {
        this.id = id;
        this.refreshDB(c);
    }

    public void setIdPartner(Context c, long idPartner) {
        this.idPartner = idPartner;
        this.refreshDB(c);
    }

    public void setIdShop(Context c, long idShop) {
        this.idShop = idShop;
        this.refreshDB(c);
    }

    public void setPointsRequired(Context c, int pointsRequired) {
        this.pointsRequired = pointsRequired;
        this.refreshDB(c);
    }

    public void setReusable(Context c, boolean reusable) {
        isReusable = reusable;
        this.refreshDB(c);
    }

    public void setDescription(Context c, String description) {
        this.description = description;
        this.refreshDB(c);
    }

    public void setImagePath(Context c, String imagePath) {
        this.imagePath = imagePath;
        this.refreshDB(c);
    }

    public void setActive(Context c, boolean active) {
        this.active = active;
        this.refreshDB(c);
    }

    public void setEndDate(Context c, String endDate) {
        this.endDate = endDate;
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
            if(! db.updatePromotionData(this)){
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

    //**********
    // Implementing Parcelable Interface
    //**********

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(idPartner);
        out.writeLong(idShop);
        out.writeInt(pointsRequired);
        out.writeInt(isReusable ? 1 : 0);
        out.writeString(description);
        out.writeString(imagePath);
        out.writeInt(active ? 1 : 0);
        out.writeString(endDate);
    }
    public static final Parcelable.Creator<Promotion> CREATOR
            = new Parcelable.Creator<Promotion>() {
        public Promotion createFromParcel(Parcel in) {
            return new Promotion(in);
        }

        public Promotion[] newArray(int size) {
            return new Promotion[size];
        }
    };

    private Promotion(Parcel in) {
        id = in.readLong();
        idPartner = in.readLong();
        idShop = in.readLong();
        pointsRequired = in.readInt();
        isReusable = (in.readInt() == 1);
        description = in.readString();
        imagePath = in.readString();
        active = (in.readInt() == 1);
        endDate = in.readString();
    }
}
