package application_projet4_groupe12.activities.settings;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import application_projet4_groupe12.R;
import application_projet4_groupe12.activities.MainActivity;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.utils.ActivityUtils;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.FacebookUtils;
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // Prevents the keyboard from automatically opening up when arriving on the activity

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
            selectFileButton.setText(R.string.disabled_for_facebook);
            selectFileButton.setTextColor(getResources().getColor(R.color.grey, null));
            selectFileButton.setEnabled(false);
        } else {
            picture.setImageBitmap(BitmapFactory.decodeFile(this.getFilesDir() + "/" + User.connectedUser.getImagePath()));
        }


        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Initializes imagePath (the path of the new image selected by the user)
                onBtnPickGallery();
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
                    newFirstName.setText("");
                    newFirstName.setHint(newFirstNameS);
                    User.connectedUser.setFirstName(v.getContext(), newFirstNameS);
                    System.out.println("First Name changed");
                    Toast.makeText(v.getContext(), "Done", Toast.LENGTH_LONG).show();
                }
                if(! newLastNameS.equals("")){
                    newLastName.setText("");
                    newLastName.setHint(newLastNameS);
                    User.connectedUser.setLastName(v.getContext(), newLastNameS);
                    System.out.println("Last Name changed");
                    Toast.makeText(v.getContext(), "Done", Toast.LENGTH_LONG).show();
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
                        User.connectedUser.setPasswordHashed(v.getContext(), Hash.hash(newPasswordS));
                        currentPassword.setText("");
                        newPassword.setText("");
                        System.out.println("Password changed");
                        Toast.makeText(v.getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        currentPassword.setBackgroundResource(R.drawable.border_error);
                        newPassword.setBackgroundResource(0);
                        Toast.makeText(v.getContext(), "This is not your current password", Toast.LENGTH_SHORT).show();
                    }
                }
                if(! newBirthdayS.equals("")){
                    try {
                        newBirthday.setText("");
                        User.connectedUser.setBirthday(v.getContext(), newBirthdayS);
                        System.out.println("Birthday changed");
                        Toast.makeText(v.getContext(), "Done", Toast.LENGTH_LONG).show();
                    } catch (WrongDateFormatException e) {
                        Toast.makeText(v.getContext(), "Invalid Birthdate format", Toast.LENGTH_SHORT).show();
                        newBirthday.setBackgroundResource(R.drawable.border_error);
                    }
                }
            }
        });
    }

    /**
     * Code found on StackOverflow
     * @author Shankar Agarwal
     */
    private void onBtnPickGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 123);//one can be replaced with any action code
    }

    /**
     * Code found on StackOverflow
     * @author Shankar Agarwal
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 123:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();


                    //New pic's name
                    String outputFilePath = User.connectedUser.getId()+"_pic.png";
                    FileInputStream streamIn = AppUtils.getStreamIn(new File(imagePath));
                    FileOutputStream streamOut = AppUtils.getStreamOut(this, outputFilePath);
                    if(streamIn!=null && streamOut!=null && AppUtils.copyFile(streamIn, streamOut)) {
                        //Image successfully coped
                        User.connectedUser.setImagePath(this, outputFilePath);
                        //Show new pic in this Activity
                        System.out.println("Profile pic was changed");
                        picture.setImageBitmap(BitmapFactory.decodeFile(this.getFilesDir()+"/"+outputFilePath));
                    } else {
                        //An error occurred
                        //Image  was not changed
                        System.err.println("Profile pic was not changed");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtils.end_home(this);
    }
}
