package application_projet4_groupe12.activities.browse_clients;

import android.content.Context;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Address;
import application_projet4_groupe12.entities.Shop;
import application_projet4_groupe12.entities.User;

public class BrowseClientsShopDataAssociation {

    private int shopID;
    private Address shopAddress;
    private List<BrowseClientsClientDataAssociation> list;

    public BrowseClientsShopDataAssociation(Context c, int shopID, List<BrowseClientsClientDataAssociation> list){

        this.shopID = shopID;
        this.list = list;

        SQLHelper db = null;
        try{
            db = new SQLHelper(c);
            this.shopAddress = db.getShopAddress(shopID);

        } catch (IOException e) {
            //TODO what do we do here ?
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }

    public int getShopID(){
        return shopID;
    }

    public List<BrowseClientsClientDataAssociation> getList(){
        return list;
    }

    public Address getShopAddress(){
        return shopAddress;
    }
}