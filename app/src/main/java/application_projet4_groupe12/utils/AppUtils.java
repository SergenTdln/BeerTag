package application_projet4_groupe12.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import application_projet4_groupe12.R;
import application_projet4_groupe12.activities.SignUp;
import application_projet4_groupe12.entities.User;

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
     * Returns the amount of bytes required to store a picture in the given <code>format</code> with dimensions <code>pxSide * pxSide</code>, per depth bit.
     * This means the output value has to be multiplied by the bit depth of the image (typically 16 bit).
     * This only works for square images.
     * @param format the format of the image. May be <code>png</code> or <code>jpg</code> for now.
     * @param pxSide the number of pixels for each side of the square, as an int.
     * @return the amount of bytes required to store the picture, to be multiplied later by the image's bit depth
     */
    public static int getPicSize(String format, int pxSide){
        return (pxSide*pxSide) / 8;
    }

    /**
     * TODO
     * @param cfg
     * @return
     * @throws UnsupportedOperationException
     */
    public static int getBitDepth(Bitmap.Config cfg) throws UnsupportedOperationException {
        int ret = 8;
        switch (cfg) {
            case ALPHA_8:
                ret = 1;
                break;
            case RGB_565:
                ret = 2;
                break;
            case ARGB_4444:
                ret = 2;
                break;
            case ARGB_8888:
                ret = 4;
                break;
            case RGBA_F16:
                ret = 8;
                break;
            case HARDWARE:
                throw new UnsupportedOperationException("This encoding is not supported by the application");
                //break;
        }
        return ret;
    }

    /**
     * TODO
     * @param fileIn
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
     * TODO
     * @param c
     * @return a FileOutputStream to a newly created <code>File</code> object located in the app's local private storage, or null if an error occurred
     */
    public static FileOutputStream getStreamOut(Context c){
        try {
            return c.openFileOutput(User.connectedUser.getImagePath(), 0);
        } catch (FileNotFoundException e){
            System.out.println("File could not be not created !");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * TODO
     * @param in
     * @param out
     * @return
     */
    public static boolean copyFile(FileInputStream in, FileOutputStream out){
        try {
            while(true){
                int data=in.read();
                if(data==-1){
                    //Reached EOF
                    //TODO anything else ?
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
}
