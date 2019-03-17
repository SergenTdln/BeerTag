package application_projet4_groupe12.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.squareup.picasso.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import application_projet4_groupe12.R;

public class FacebookUtils {

    private ImageView outputBitmap;

    public URL getFacebookProfilePic(){
        String id = Profile.getCurrentProfile().getId();
        URL profile_pic;
        JSONObject object;

        try {
            profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
            Log.i(Global.debug_text, "photo de profil: "+profile_pic);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return profile_pic;
    }

    public String getFacebookId(){
        String id = Profile.getCurrentProfile().getId();
        return id;
    }




}
