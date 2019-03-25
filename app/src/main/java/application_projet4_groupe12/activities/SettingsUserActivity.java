package application_projet4_groupe12.activities;

import android.graphics.BitmapFactory;
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

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

import application_projet4_groupe12.R;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.utils.ActivityUtils;
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

        picture = (ImageView) findViewById(R.id.settings_user_picture);
        selectFileButton = (Button) findViewById(R.id.settings_user_picture_select_button);

        if (ActivityUtils.getInstance().isLoggedInFacebook()){
            URL image_url = new FacebookUtils().getFacebookProfilePic();
            Picasso.with(this).load(String.valueOf(image_url)).into(picture);
            selectFileButton.setText("Disabled for accounts created from Facebook");
            selectFileButton.setTextColor(getResources().getColor(R.color.grey, null));
            selectFileButton.setClickable(false);
        } else {
            try {
                picture.setImageBitmap(BitmapFactory.decodeStream(this.getAssets().open(User.connectedUser.getImagePath())));
            } catch (IOException e){
                //Leave a default profile picture
                e.printStackTrace();
                Toast.makeText(this, "Error while loading the profile picture from local storage", Toast.LENGTH_SHORT).show();
            }

            selectFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    //select a file from local path
                    String imagePath = null;
                    User.connectedUser.setImagePath(imagePath);
                }
            });
        }

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
                        Toast.makeText(getBaseContext(), "Invalid Birthdate format", Toast.LENGTH_SHORT).show();
                        newBirthday.setBackgroundColor(getResources().getColor(R.color.light_red, null));
                    }
                }
            }
        });
    }
}
