package application_projet4_groupe12.activities;

import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import application_projet4_groupe12.R;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.utils.ActivityUtils;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.FacebookUtils;
import application_projet4_groupe12.utils.Global;
import application_projet4_groupe12.utils.Hash;

public class SettingsUserActivity extends AppCompatActivity {

    // 150 is the size of pictures in this UI, in dips. //TODO update if it changes later in the xml file
    private long MIN_REQUIRED_SPACE_FOR_PIC_PER_DEPTH_BIT_BYTES = AppUtils.getPicSize("png", (int) AppUtils.convertDpToPixel((float) 150, this));

    TextView subTitle;
    EditText newFirstName;
    EditText newLastName;
    EditText currentPassword;
    EditText newPassword;
    EditText newBirthday;
    ImageView picture;
    Button selectFileButton;
    ImageButton buttonOut;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_user);

        subTitle = (TextView) findViewById(R.id.settings_user_sub_title);
        subTitle.setText(User.connectedUser.getUsername());

        newFirstName = (EditText) findViewById(R.id.settings_user_change_first_name);
        newFirstName.setHint(User.connectedUser.getFirstName());
        newLastName = (EditText) findViewById(R.id.settings_user_change_last_name);
        newLastName.setHint(User.connectedUser.getLastName());
        currentPassword = (EditText) findViewById(R.id.settings_user_change_password_current);
        newPassword = (EditText) findViewById(R.id.settings_user_change_password_new);
        newBirthday = (EditText) findViewById(R.id.settings_user_change_birthday);

        picture = (ImageView) findViewById(R.id.settings_user_picture);
        if (ActivityUtils.getInstance().isLoggedInFacebook()){
            URL image_url = new FacebookUtils().getFacebookProfilePic();
            Picasso.with(this).load(String.valueOf(image_url)).into(picture);
            selectFileButton.setText("Disabled for accounts created from Facebook");
            selectFileButton.setTextColor(getResources().getColor(R.color.grey, null));
            selectFileButton.setClickable(false);
        } else {
            picture.setImageBitmap(BitmapFactory.decodeFile(this.getFilesDir() + "/" + User.connectedUser.getImagePath()));
        }


        selectFileButton = (Button) findViewById(R.id.settings_user_picture_select_button);
        selectFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    //select a file from local path and COPY it into the app's internal storage

                    //Obtain old profile picture File data
                    String oldFilePath = v.getContext().getFilesDir() + "/" + User.connectedUser.getImagePath();
                    Bitmap oldPicture = BitmapFactory.decodeFile(oldFilePath); //TODO handle the case where no file existed before
                    Bitmap.Config oldPicCfg = oldPicture.getConfig();

                    //Read new file chosen by user
                    File newFile = null; //TODO (tell the user it has to be square to fit in 150*150 dp) --- get a full path
                    Bitmap newPicture = BitmapFactory.decodeFile(newFile.getPath());
                    Bitmap.Config newPicCfg = newPicture.getConfig();
                    int bitDepth = 8; //Default value : worst case scenario
                    try {
                        bitDepth = AppUtils.getBitDepth(newPicCfg);
                    } catch (UnsupportedOperationException e) {
                        e.printStackTrace();
                        Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //Check if local storage is sufficient
                    if (getFilesDir().getFreeSpace() < (MIN_REQUIRED_SPACE_FOR_PIC_PER_DEPTH_BIT_BYTES * bitDepth * 2)) {
                        // Available storage is considered unsufficient. Aborting
                        Toast.makeText(v.getContext(), "Unsufficient storage available on local storage. Please free some storage space, then try again.", Toast.LENGTH_SHORT).show();
                        System.out.println("Unsufficient storage available on local storage. Please free some space, then try again.");
                        return;
                    }
                    // Local storage is sufficient, continue :

                    //Delete old profile pic from private storage
                    deleteFile(User.connectedUser.getImagePath());
                    System.out.println("Deleted old profile pic !");

                    //Copy it in private storage
                    FileInputStream fileInputStream = AppUtils.getStreamIn(newFile);
                    FileOutputStream fileOutputStream = AppUtils.getStreamOut(v.getContext());
                    if (fileInputStream != null && fileOutputStream != null) {
                        if (AppUtils.copyFile(fileInputStream, fileOutputStream)) {
                            //Success
                        } else {
                            System.out.println("An error occurred while copying your new Picture onto the Application. Please try again later");
                            Toast.makeText(v.getContext(), "An error occurred while copying your new Picture onto the Application. Please try again later", Toast.LENGTH_LONG).show();
                        }
                        try {
                            fileInputStream.close();
                            fileOutputStream.close();
                        } catch (IOException e) {
                            //Do Nothing
                        }
                    } else {
                        System.out.println("Could not copy your new Picture onto the Application. Please try again later");
                        Toast.makeText(v.getContext(), "Could not copy your new Picture onto the Application. Please try again later", Toast.LENGTH_LONG).show();
                    }

                    //Image path does not change
                    //User.connectedUser.setImagePath(oldFileName);

                    //Show new pic in this Activity
                    picture.setImageBitmap(BitmapFactory.decodeFile(v.getContext().getFilesDir()+"/"+User.connectedUser.getImagePath()));
                }
            });

        buttonOut = (ImageButton) findViewById(R.id.settings_user_save_button);
        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstNameS = newFirstName.getText().toString();
                String newLastNameS = newLastName.getText().toString();
                String currPasswordS = currentPassword.getText().toString();
                String newPasswordS = newPassword.getText().toString();
                String newBirthdayS = newBirthday.getText().toString();

                if(! newFirstNameS.equals("")){
                    User.connectedUser.setFirstName(newFirstNameS);
                }
                if(! newLastNameS.equals("")){
                    User.connectedUser.setLastName(newLastNameS);
                }
                if( (currPasswordS.equals("")) != (newPasswordS.equals(""))){ //XOR
                    currentPassword.setBackgroundColor(getResources().getColor(R.color.light_red, null));
                    newPassword.setBackgroundColor(getResources().getColor(R.color.light_red, null));
                    return;
                }
                if( (!currPasswordS.equals("")) && (!newPasswordS.equals(""))){
                    if(Hash.hash(currPasswordS).equals(User.connectedUser.getPasswordHashed())) {
                        User.connectedUser.setPasswordHashed(Hash.hash(newPasswordS));
                    } else {
                        currentPassword.setBackgroundColor(getResources().getColor(R.color.light_red, null));
                        return;
                    }
                }
                if(! newBirthdayS.equals("")){
                    try {
                        User.connectedUser.setBirthday(newBirthdayS);
                    } catch (WrongDateFormatException e) {
                        Toast.makeText(v.getContext(), "Invalid Birthdate format", Toast.LENGTH_SHORT).show();
                        newBirthday.setBackgroundColor(getResources().getColor(R.color.light_red, null));
                    }
                }
            }
        });
    }
}
