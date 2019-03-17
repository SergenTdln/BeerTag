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
            Log.i("beertag", "photo de profil: "+profile_pic);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return profile_pic;
    }

    public String getFacebookUsername(){
        String id = Profile.getCurrentProfile().getId();
        URL profile_pic;
        JSONObject object;
        String username;


        AccessToken accesToken;
        accesToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(
                accesToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();

        return id;
    }

//    public Bitmap get_fb_image(URL profile_pic) {
//
//        Bitmap image = null;
//        try {
//            image = BitmapFactory.decodeStream(profile_pic.openConnection().getInputStream());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return image;
//    }
//


}
