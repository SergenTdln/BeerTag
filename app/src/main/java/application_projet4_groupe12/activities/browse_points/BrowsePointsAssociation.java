package application_projet4_groupe12.activities.browse_points;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Address;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.Shop;

public class BrowsePointsAssociation {

    private int points;
    private long shopID;
    private String partnerName;
    private String partnerImagePath;
    private Address shopAddress;
    private String shopDescr;

    public BrowsePointsAssociation(Context c, long partnerID, long shopID, int points){

        //this.partnerID = partnerID;
        this.shopID = shopID;
        this.points = points;

        SQLHelper db = null;
        try{
            db = new SQLHelper(c);
            Partner partner = db.getPartner(partnerID);
            Shop shop = db.getShop(shopID);
            long addressID = shop.getAddressID();

            this.partnerName = partner.getName();
            this.partnerImagePath = partner.getImagePath();

            this.shopAddress = db.getAddress(addressID);
            this.shopDescr = shop.getDescription();

        } catch (IOException | SQLiteException e) {
            // Just skip this instance
            e.printStackTrace();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }

    public long getShopID() {
        return shopID;
    }

    public String getPartnerName(){
        return this.partnerName;
    }

    public String getPartnerImagePath(){
        return this.partnerImagePath;
    }

    public Address getShopAddress(){
        return this.shopAddress;
    }

    public int getPoints(){
        return this.points;
    }

    public String getShopDescr(){
        return this.shopDescr;
    }
}
