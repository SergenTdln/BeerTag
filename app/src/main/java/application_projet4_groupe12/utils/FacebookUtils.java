package application_projet4_groupe12.utils;

import android.util.Log;
import android.widget.ImageView;

import com.facebook.Profile;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

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
        return Profile.getCurrentProfile().getId();
    }




}
