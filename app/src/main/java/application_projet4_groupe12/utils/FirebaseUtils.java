package application_projet4_groupe12.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;

public class FirebaseUtils {

    public void FirebebaseSync(Context context){
        SQLHelper db;
        try {
            db = new SQLHelper(context);
            db.TransferUser();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

        try {
            db = new SQLHelper(context);
            db.TransferAddress();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

        try {
            db = new SQLHelper(context);
            db.TransferAdmin_user();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

        try {
            db = new SQLHelper(context);
            db.TransferFavorite_shops();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

        try {
            db = new SQLHelper(context);
            db.TransferPromotion();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

        try {
            db = new SQLHelper(context);
            db.TransferShop_frames();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

        try {
            db = new SQLHelper(context);
            db.TransferShop_location();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

        try {
            db = new SQLHelper(context);
            db.TransferUser_points();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

        try {
            db = new SQLHelper(context);
            db.TransferUser_promotion();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        }

    }
}
