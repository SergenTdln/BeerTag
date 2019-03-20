package application_projet4_groupe12.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Vibrator;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.activities.MainActivity;
import application_projet4_groupe12.activities.QRGenerateActivity;
import application_projet4_groupe12.activities.SignUp;
import application_projet4_groupe12.activities.SplashActivity;
import application_projet4_groupe12.utils.ActivityUtils;

public class AppUtils {

    private static long backPressed = 0;

    public static void tapToExit(Activity activity) {
        if (backPressed + 2500 > System.currentTimeMillis()){
            if(activity.isTaskRoot()){
                //TODO deco de la db locale
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                ActivityUtils.getInstance().invokeActivity(activity, SignUp.class, true);
            }
            activity.finish();
        }
        else{
            showToast(activity.getApplicationContext(), activity.getResources().getString(R.string.tapAgain));
        }
        backPressed = System.currentTimeMillis();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        showToast(context, context.getResources().getString(R.string.copied));
    }
}
