package application_projet4_groupe12.activities.settings;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.FirebaseUtils;

public class SettingsPartnerActivity extends AppCompatActivity {

    private TextView subTitle;
    private EditText newName;
    private EditText newAddress;
    private ImageView picture;
    private Button selectFileButton;

    private ListView listAdmins;
    private Spinner dropDownUsers;
    private Button addAdmin;

    private FloatingActionButton buttonOut;

    private Partner currentPartner;

    private int nbAdmins;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_partner);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // Prevents the keyboard from automatically opening up when arriving on the activity

        currentPartner = User.connectedUser.getAdministratedPartner(this);

        subTitle = (TextView) findViewById(R.id.settings_partner_sub_title);
        subTitle.setText(currentPartner.getName());

        newName = (EditText) findViewById(R.id.settings_partner_change_name);
        newName.setHint(currentPartner.getName());

        newAddress = (EditText) findViewById(R.id.settings_partner_change_address);
        newAddress.setHint(currentPartner.getAddress());

        picture = (ImageView) findViewById(R.id.settings_partner_picture);
        picture.setImageBitmap(BitmapFactory.decodeFile(this.getFilesDir()+"/"+currentPartner.getImagePath()));

        selectFileButton = (Button) findViewById(R.id.settings_partner_picture_select_button);
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initializes imagePath (thepath of the new image selected by the user)
                onBtnPickGallery();
            }
        });

        listAdmins = (ListView) findViewById(R.id.settings_partner_manage_admins_listview);
        fillListView(listAdmins, this);
        registerForContextMenu(listAdmins); //Allows for long-clicking an item in this list

        dropDownUsers = (Spinner) findViewById(R.id.settings_partner_manage_admins_spinner);
        fillSpinner(dropDownUsers, this);

        addAdmin = (Button) findViewById(R.id.settings_partner_manage_admins_add_button);
        addAdmin.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String selected = (String) dropDownUsers.getSelectedItem();
                if(selected.equals(getString(R.string.settings_partner_spinner_default))){
                    //Do nothing
                    dropDownUsers.setBackgroundResource(R.drawable.border_error);
                } else {
                    dropDownUsers.setBackground(null);
                    if(addAdmin(currentPartner, getUser(v.getContext(), selected), v.getContext())){
                        //Update the displayed list
                        fillListView(listAdmins, v.getContext());

                        //Update the Spinner
                        fillSpinner(dropDownUsers, v.getContext());

                        //Increase the counter
                        nbAdmins++;
                    } else {
                        Toast.makeText(getApplicationContext(), "An error occurred; we could not add this Admin", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        buttonOut =  findViewById(R.id.settings_partner_save_button);
        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNameS = newName.getText().toString();
                String newAddressS = newAddress.getText().toString();

                if(! newNameS.equals("")){
                    currentPartner.setName(v.getContext(), newNameS);
                    newName.setText("");
                    newName.setHint(newNameS);
                    subTitle.setText(newNameS);
                }
                if(! newAddressS.equals("")){
                    currentPartner.setAddress(v.getContext(), newAddressS);
                    newAddress.setText("");
                    newAddress.setHint(newAddressS);
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

        if(requestCode==123){
            if(resultCode == RESULT_OK){
                Uri selectedImage = imageReturnedIntent.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                if(selectedImage==null){
                    return;
                }
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if(cursor!=null) {
                    cursor.moveToFirst();


                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();


                    //New pic's name
                    String outputFilePath = currentPartner.getId() + "_pic.png";
                    FileInputStream streamIn = AppUtils.getStreamIn(new File(imagePath));
                    FileOutputStream streamOut = AppUtils.getStreamOut(this, outputFilePath);
                    if(streamIn != null && streamOut != null && AppUtils.copyFile(streamIn, streamOut)) {
                        //Image successfully coped
                        currentPartner.setImagePath(this, outputFilePath);
                        //Show new pic in this Activity
                        System.out.println("Profile pic was changed");
                        picture.setImageBitmap(BitmapFactory.decodeFile(this.getFilesDir() + "/" + outputFilePath));
                    } else {
                        //An error occurred
                        //Image  was not changed
                        System.err.println("Profile pic was not changed");
                    }
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(nbAdmins<=1){
            getMenuInflater().inflate(R.menu.delete_admin_context_empty, menu);
            menu.setHeaderTitle("Can't delete your last admin !");
        } else {
            getMenuInflater().inflate(R.menu.delete_admin_context, menu);
            menu.setHeaderTitle("Delete this Admin ?");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        SettingsPartnerAdminDataRowAdapter.ViewHolder vh = (SettingsPartnerAdminDataRowAdapter.ViewHolder) info.targetView.getTag();
        String username = vh.username.getText().toString();
        if(username.equals(User.connectedUser.getUsername())){
            Toast.makeText(this, "You can't remove yourself from the Admin position", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.delete_admin_context_item:
                    if (safeDeleteAdmin(username)) {
                        nbAdmins--;
                        Toast.makeText(this, "Admin successfully deleted", Toast.LENGTH_SHORT).show();
                        fillListView(listAdmins, this); //Refreshing the admins list
                        registerForContextMenu(listAdmins);
                        fillSpinner(dropDownUsers, this); //Refresh the spinner
                        dropDownUsers.setBackground(null);
                        return true;
                    } else {
                        Toast.makeText(this, "Could not delete this admin. Please try again.", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                case R.id.delete_admin_context_item_disabled :
                    //Do nothing ?
                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtils.end_home_admin(this);
    }

    private User getUser(Context c, String email){
        SQLHelper db = null;
        try{
            db = new SQLHelper(c);
            return db.getUser(email);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "An error occurred; we could retrieve the user from the drop-down menu", Toast.LENGTH_SHORT).show();
            return null;
        } finally {
            if(db!=null){
                db.close();
            }
        }
    }

    private void fillSpinner(Spinner spinner, Context c){

        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            List<User> currentAdmins = db.getAllAdmins(currentPartner.getId());
            List<String> allUsernames = db.getAllNonAdminUsernames();

            allUsernames.add(0, getString(R.string.settings_partner_spinner_default));
            List<String> adminUsernames = User.getUsernames(currentAdmins);

            allUsernames.removeAll(adminUsernames);

            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_settings_partner_spinner_adapter, allUsernames);
            spinner.setAdapter(adapter);
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "An error occurred; we could not update the content of the drop-down menu", Toast.LENGTH_SHORT).show();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }

    private void fillListView(ListView lv, Context c){

        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            List<User> admins = db.getAllAdmins(currentPartner.getId());
            if(admins.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Empty list", Toast.LENGTH_SHORT).show();
            }
            nbAdmins = admins.size();
            SettingsPartnerAdminDataRowAdapter adapter = new SettingsPartnerAdminDataRowAdapter(this, admins);
            lv.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "An error occurred; we could not update the content of the displayed list", Toast.LENGTH_SHORT).show();
        } finally {
            if(db!=null){
                db.close();
            }
        }
    }

    private boolean addAdmin(Partner partner, User user, Context c){
        if(user==null){
            return false;
        }
        SQLHelper db = null;
        try{
            db = new SQLHelper(c);
            boolean local = db.addAdmin(user.getId(), partner.getId());

            FirebaseUtils.firestoreAddAdmin(partner.getId(), user.getId());

            return local;
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "An error occurred; we could not add this User as an admin in the database. Please try again", Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            if(db!=null){
                db.close();
            }
        }
    }

    private boolean safeDeleteAdmin(String username){
        if (nbAdmins<=1){
            return false;
        }
        SQLHelper db = null;
        try{
            db = new SQLHelper(this);
            boolean local = db.removeAdmin(username, currentPartner.getId());

            FirebaseUtils.firestoreRemoveAdmin(currentPartner.getId(), db.getUserID(username));

            return local;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        } finally {
            if(db!=null){
                db.close();
            }
        }
    }
}
