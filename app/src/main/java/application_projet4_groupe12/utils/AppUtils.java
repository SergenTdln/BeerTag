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


import application_projet4_groupe12.R;

public class AppUtils {

    private static long backPressed = 0;

    public static void tapToExit(Activity activity) {
        if (backPressed + 2500 > System.currentTimeMillis()){
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


    public static void vibrateDevice(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }

    public static void share(Activity activity, String fileUrl) {//, String text) {
        // todo
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        vibrateDevice(context);
        showToast(context, context.getResources().getString(R.string.copied));
    }


}
