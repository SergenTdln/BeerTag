package application_projet4_groupe12.utils;

import android.app.Activity;
import android.content.Intent;

import com.facebook.AccessToken;

public class ActivityUtils {

    private static ActivityUtils sActivityUtils = null;

    public static ActivityUtils getInstance() {
        if (sActivityUtils == null) {
            sActivityUtils = new ActivityUtils();
        }
        return sActivityUtils;
    }

    public void invokeActivity(Activity activity, Class<?> tClass, boolean shouldFinish) {
        Intent intent = new Intent(activity, tClass);
        activity.startActivity(intent);
        if (shouldFinish) {
            activity.finish();
        }
    }

    public boolean isLoggedInFacebook() {
        AccessToken token;
        token = AccessToken.getCurrentAccessToken();

        return token != null;
    }
}
