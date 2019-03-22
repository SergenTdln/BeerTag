package application_projet4_groupe12.activities.browse_clients;

import android.content.Context;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Address;
import application_projet4_groupe12.entities.Shop;
import application_projet4_groupe12.entities.User;

public class BrowseClientsClientDataAssociation {

    private int points;
    private String userFullname;
    private String userUsername;

    public BrowseClientsClientDataAssociation(Context c, String username, int points){

        this.points = points;

        SQLHelper db = null;
        try{
            db = new SQLHelper(c);
            User user = db.getUser(username);

            this.userFullname = user.getFullName();
            this.userUsername = username;

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
}
