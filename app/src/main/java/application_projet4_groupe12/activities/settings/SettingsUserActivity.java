package application_projet4_groupe12.activities.settings;

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

        selectFileButton = (Button) findViewById(R.id.settings_user_picture_select_button);

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


        selectFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    //Prompts the user to select a file from local storage and COPY it into the app's internal storage

                    if(AppUtils.changeProfilePicture(v.getContext(), User.connectedUser)){
                        //Image changed
                        //Show new pic in this Activity
                        picture.setImageBitmap(BitmapFactory.decodeFile(v.getContext().getFilesDir()+"/"+User.connectedUser.getImagePath()));
                    } else {
                        //An error occurred
                        //Image  was not changed
                        System.err.println("Profile pic was not changed");
                    }

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
                    currentPassword.setBackgroundResource(R.drawable.border_error);
                    newPassword.setBackgroundResource(R.drawable.border_error);
                    return;
                }
                if( (!currPasswordS.equals("")) && (!newPasswordS.equals(""))){
                    if(newPasswordS.length()<6){
                        currentPassword.setBackgroundResource(0);
                        newPassword.setBackgroundResource(R.drawable.border_error);
                        Toast.makeText(v.getContext(), "Your password must be at least 6 characters long", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(Hash.hash(currPasswordS).equals(User.connectedUser.getPasswordHashed())) {
                        currentPassword.setBackgroundResource(0);
                        newPassword.setBackgroundResource(0);
                        User.connectedUser.setPasswordHashed(Hash.hash(newPasswordS));
                        Toast.makeText(v.getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        currentPassword.setBackgroundResource(R.drawable.border_error);
                        newPassword.setBackgroundResource(0);
                        Toast.makeText(v.getContext(), "This is not your current password", Toast.LENGTH_SHORT).show();
                    }
                }
                if(! newBirthdayS.equals("")){
                    try {
                        User.connectedUser.setBirthday(newBirthdayS);
                    } catch (WrongDateFormatException e) {
                        Toast.makeText(v.getContext(), "Invalid Birthdate format", Toast.LENGTH_SHORT).show();
                        newBirthday.setBackgroundResource(R.drawable.border_error);
                    }
                }
            }
        });
    }
}
