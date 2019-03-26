package application_projet4_groupe12.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
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
     * Returns the bit depth of the given <code>Bitmap.Config</code> object. This value represents the amount of bytes used to store one pixel of the associated <code>Bitmap</code>.
     * @param cfg the Bitmap.Config instance to analyze
     * @return the bit depth of the Bitmap as an int, defaulting to 8 if the format is unknown.
     * @throws UnsupportedOperationException in case the <code>Bitmap</code> instance is in the immutable <code>HARDWARE</code> format, which does not allow any operation but displaying it.
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


    /**
     * TODO
     * Does NOT support transparency channels when it comes to bit-depth computation : when using such images, behavior is unpredicted.
     * @param c the <code>Context</code> used to access the app's internal private storage
     * @param user the <code>User</code> on whose profile pic we want to edit. Typically, the connected User.
     * @return True if the operation succeeded, or False if an error occurred
     */
    public static boolean changeProfilePicture(Context c, User user){

        //Read new file chosen by user
        File newFile = null; //TODO (tell the user it has to be square to fit in 150*150 dp) --- get a full path
        FileInputStream newFileInputStream = getStreamIn(newFile);
        if (newFileInputStream == null) {
            //TODO
            //Return false ?
            //TODO close streams
            return false;
        }
        ImageInfo newFileII = new ImageInfo();
        newFileII.setInput(newFileInputStream);
        newFileII.setDetermineImageNumber(true);
        newFileII.setCollectComments(true);
        boolean newFileOK = newFileII.check(); //TODO show some sort of progress bar ?
        if(!newFileOK){
            //TODO
            //TODO close streams
            System.err.println("Not a supported image file format.");
            return false;
        }
        //Check succeeded : continue

        //Make sure the selected image is a square
        if(newFileII.getHeight() != newFileII.getWidth()){
            //TODO
            //TODO close streams
            Toast.makeText(c, "The selected image must be square", Toast.LENGTH_SHORT).show();
            System.err.println("The image must be square");
            return false;
        }
        //It is square : continue

        // Check if local storage is sufficient
        // 150 is the size of pictures in the app's UIs, in dips. //TODO update if it changes later in the xml files
        int pxSize = (int) convertDpToPixel((float) 150, c);
        if (c.getFilesDir().getFreeSpace() < ( pxSize*pxSize * newFileII.getBitsPerPixel() * 2)) {
            // Available storage is considered insufficient. Aborting
            //TODO close streams
            Toast.makeText(c, "Insufficient storage available on local storage. Please free some storage space, then try again.", Toast.LENGTH_SHORT).show();
            System.out.println("Insufficient storage available on local storage. Please free some space, then try again.");
            return false;
        }
        // Local storage is sufficient : continue

        //Delete old profile pic from private storage
        c.deleteFile(user.getImagePath());
        System.out.println("Deleted old profile pic !");

        //Construct a new pxSize*pxSize image from newFile
        Bitmap newFileBitmap = BitmapFactory.decodeStream(newFileInputStream);
        Bitmap resized = Bitmap.createScaledBitmap(newFileBitmap, pxSize, pxSize, true);

        //Copy this new file in private storage
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = c.openFileOutput(user.getImagePath(), 0);
        } catch (FileNotFoundException e) {
            //Should not happen as we have all rights in the app's local private storage
            //TODO problem : old pic was already deleted
            //TODO close streams
            return false;
        }
        boolean ret = resized.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream); //TODO place this in another thread, or at least get a progress bar

        //Image path does not change
        //User.connectedUser.setImagePath(oldFileName);

        return ret;
    }

    /**
     * TODO
     * @return
     */
    public static String[] getSupportedFormats(){
        return ImageInfo.FORMAT_NAMES;
    }
}
