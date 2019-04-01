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
    private String partnerName;
    private String partnerImagePath;
    private Address shopAddress;
    private String shopDescr;

    public BrowsePointsAssociation(Context c, int partnerID, int shopID, int points){

        //this.partnerID = partnerID;
        //this.shopID = shopID;
        this.points = points;

        SQLHelper db = null;
        try{
            db = new SQLHelper(c);
            Partner partner = db.getPartner(partnerID);
            Shop shop = db.getShop(shopID);
            int addressID = shop.getAddressID();

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


    String getPartnerName(){
        return this.partnerName;
    }

    String getPartnerImagePath(){
        return this.partnerImagePath;
    }

    Address getShopAddress(){
        return this.shopAddress;
    }

    int getPoints(){
        return this.points;
    }

    String getShopDescr(){
        return this.shopDescr;
    }
}
