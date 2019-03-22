package application_projet4_groupe12.activities.browse_clients;

import android.content.Context;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Address;
import application_projet4_groupe12.entities.Shop;
import application_projet4_groupe12.entities.User;

public class BrowseClientsAssociation {

    private int points;
    private String userFullname;
    private String userUsername;
    private Address shopAddress;
    private String shopDescr;

    public BrowseClientsAssociation(Context c, String username, int shopID, int points){

        this.points = points;

        SQLHelper db = null;
        try{
            db = new SQLHelper(c);
            User user = db.getUser(username);

            this.userFullname = user.getFullName();
            this.userUsername = username;

            Shop shop = db.getShop(shopID);

            this.shopDescr = shop.getDescription();
            int addressID = shop.getAddressID();
            this.shopAddress = db.getAddress(addressID);

        } catch (IOException e) {
            //TODO what do we do here ?
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }

    int getPoints(){
        return points;
    }

    String getFullname(){
        return userFullname;
    }

    String getUsername(){
        return userUsername;
    }

    Address getShopAddress(){
        return shopAddress;
    }

    String getShopDescr(){
        return shopDescr;
    }
}
