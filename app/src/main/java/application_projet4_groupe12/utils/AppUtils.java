package application_projet4_groupe12.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import application_projet4_groupe12.BuildConfig;
import application_projet4_groupe12.R;
import application_projet4_groupe12.activities.AdminActivity;
import application_projet4_groupe12.activities.AlcoolSensiActivity;
import application_projet4_groupe12.activities.MainActivity;
import application_projet4_groupe12.activities.ShareActivity;
import application_projet4_groupe12.activities.SignUp;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AppUtils {

    private static long backPressed = 0;

    public static void tapToExit(Activity activity, int interface_number) {
        backPressed = System.currentTimeMillis();
        if (backPressed + 1000 > System.currentTimeMillis()){
            if(activity.isTaskRoot()){
                SharedPreferences session = getApplicationContext().getSharedPreferences("session", MODE_PRIVATE);
                session.edit().putBoolean("logged_in", true).apply();

                if (User.connectedUser.isAdmin() && interface_number == 1) {
                    ActivityUtils.getInstance().invokeActivity(activity, AdminActivity.class, true);
                } else {
                    ActivityUtils.getInstance().invokeActivity(activity, MainActivity.class, true);
                }
            }
            activity.finish();
        } else {
            showToast(activity.getApplicationContext(), activity.getResources().getString(R.string.tapAgain));
        }
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

    /**
     * Returns the number of occurrences of the character <code>pattern</code> in the <code>target</code> String.
     * @param pattern the pattern character to look for
     * @param target the String in which we are searching
     * @return the number of occurrences as a long.
     */
    public static long occurrences(char pattern, String target){
        return target.codePoints().filter(c -> c==pattern).count();
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @author user "Muhammad Nabeel Arif" on StackOverflow
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @author user "Muhammad Nabeel Arif" on StackOverflow
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Returns a <code>FileInputStream</code> instance associated with the given <code>File</code> object. This is a convenience method that handles the possible exceptions on its own.
     * @param fileIn the file to access
     * @return a FileInputStream to the given <code>File</code> object, or null if an error occurred
     */
    public static FileInputStream getStreamIn(File fileIn){
        try {
            return new FileInputStream(fileIn);
        } catch (FileNotFoundException e) {
            System.out.println("File could not be not opened !");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a <code>FileOutputStream</code> instance associated with the given <arg>imagePath</arg>. If this path does not exist, it is created in the app's private internal storage.
     * @param c the <code>Cntext</code> used to access the app's internal storage in which the file is stored.
     * @return a FileOutputStream to a newly created <code>File</code> object located in the app's local private storage, or null if an error occurred
     */
    public static FileOutputStream getStreamOut(Context c, String imagePath){
        try {
            return c.openFileOutput(imagePath, 0);
        } catch (FileNotFoundException e){
            System.out.println("File could not be not created !");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Copies all the content from a <code>FileInputStream</code> into a <code>FileOutputStream</code>. This is a convenience method that handles the possible exceptions on its own. This method does not close the streams after it completes the copy.
     * @param in the <code>FileInputStream</code> instance. Cannot be <code>null</code>.
     * @param out the <code>FileOutputStream</code> instance. Cannot be <code>null</code>.
     * @return True if the copy succeeded, or False if an error occurred.
     */
    public static boolean copyFile(@NonNull FileInputStream in, @NonNull FileOutputStream out){
        try {
            while(true){
                int data=in.read();
                if(data==-1){
                    //Reached EOF
                    return true;
                } else {
                    out.write(data);
                }
            }
        } catch (IOException e) {
            //An error occurred at some point during read or write
            System.out.println("An error occurred during the copy of the file");
            e.printStackTrace();
            return false;
        }
    }

    public static void openInstagram(Activity activity) {
        Intent intentAiguilleur;
        String scheme = "http://instagram.com/_u/beertag.ucl";
        String path = "https://instagram.com/beertag.ucl";
        String nomPackageInfo ="com.instagram.android";
        try {
            activity.getPackageManager().getPackageInfo(nomPackageInfo, 0);
            intentAiguilleur = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
            Log.v(Global.debug_text," load 1");
        } catch (Exception e) {
            intentAiguilleur = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            Log.v(Global.debug_text," load 2"+e);
        }
        activity.startActivity(intentAiguilleur);
    }

    public static void logout(Activity activity){

        //reset la session globale fb ou standar
        if (ActivityUtils.getInstance().isLoggedInFacebook()) {
            String session_id = new FacebookUtils().getFacebookId();
            SharedPreferences fb_login = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
            fb_login.edit().clear().apply();
        } else {
            SharedPreferences standard_login = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
            //Déconnecter en local
            User.disconnectUser(activity);
            standard_login.edit().clear().apply();
            activity.finish();
        }

        SharedPreferences shared_login_choice = activity.getSharedPreferences("session", Context.MODE_PRIVATE);
        shared_login_choice.edit().clear().apply();

        //couper la session firebase
        FirebaseAuth.getInstance().signOut();
        //couper la session facebook
        LoginManager.getInstance().logOut();

        Intent intent = new Intent(activity, SignUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clears the Activity stack
        activity.startActivity(intent);
        activity.finish();
    }

    public static void display_dialog_share(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_share);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_version)).setText("Version " + BuildConfig.VERSION_NAME);

        dialog.findViewById(R.id.bt_getcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
//                shared.edit().putBoolean("expired_qr", false);
//                shared.edit().apply();
                activity.startActivity(new Intent(activity, ShareActivity.class));
                activity.finish();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    public static void end_home(Activity activity){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void end_home_admin(Activity activity){
        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }
}
