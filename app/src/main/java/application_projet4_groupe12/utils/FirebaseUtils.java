package application_projet4_groupe12.utils;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;

public class FirebaseUtils {

    public static void transferFromFirebase(Context c){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            if(db.TransferUser()) {
                System.out.println("Transferred Users successfully");
            }
            if(db.TransferAddress()) {
                System.out.println("Transferred Address successfully");
            }
            if(db.TransferAdmin_user()) {
                System.out.println("Transferred Admin successfully");
            }
            if(db.TransferFavorite_shops()) {
                System.out.println("Transferred Favorite_shops successfully");
            }
            if(db.TransferPromotion()) {
                System.out.println("Transferred Promotion successfully");
            }
            if(db.TransferShop_frames()) {
                System.out.println("Transferred Shop_frames successfully");
            }
            if(db.TransferShop_location()) {
                System.out.println("Transferred Shop_locations successfully");
            }
            if(db.TransferUser_points()) {
                System.out.println("Transferred User_points successfully");
            }
            if(db.TransferUser_promotion()) {
                System.out.println("Transferred User_promotions successfully");
            }
            if(db.TransferPartner()){
                System.out.println("Transferred Partners successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "printStackTrace" + e);
        }
        finally {
            if(db!=null) {
                db.close();
            }
        }
    }
}
