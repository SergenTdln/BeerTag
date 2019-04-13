package application_projet4_groupe12.activities.settings;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.AppUtils;

public class SettingsPartnerActivity extends AppCompatActivity {

    private TextView subTitle;
    private EditText newName;
    private EditText newAddress;
    private ImageView picture;
    private Button selectFileButton;

    private ListView listAdmins;
    private Spinner dropDownUsers;
    private Button addAdmin;

    private ImageButton buttonOut;

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
            public void onClick(View v) {//TODO @Martin see UserSettings
                //Prompts the user to select a file from local storage and COPY it into the app's internal storage

                if(AppUtils.changeProfilePicture(v.getContext(), currentPartner)){
                    //Image changed
                    String imagePath = null;
                    currentPartner.setImagePath(v.getContext(), imagePath);
                    //Show new pic in this Activity
                    picture.setImageBitmap(BitmapFactory.decodeFile(v.getContext().getFilesDir()+"/"+currentPartner.getImagePath()));
                } else {
                    //An error occurred
                    //Image  was not changed
                    System.err.println("Partner logo was not changed");
                }
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

        buttonOut = (ImageButton) findViewById(R.id.settings_partner_save_button);
        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNameS = newName.getText().toString();
                String newAddressS = newAddress.getText().toString();

                if(! newNameS.equals("")){
                    currentPartner.setName(v.getContext(), newNameS);
                }
                if(! newAddressS.equals("")){
                    currentPartner.setName(v.getContext(), newAddressS);
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(nbAdmins<=1){
            getMenuInflater().inflate(R.menu.delete_admin_context_empty, menu);
            //menu.setHeaderIcon(R.drawable.ic_launcher_foreground); //TODO change this icon : a stop sign, for example. Also make it actually show an icon
            menu.setHeaderTitle("Can't delete your last admin !");
        } else {
            getMenuInflater().inflate(R.menu.delete_admin_context, menu);
            //menu.setHeaderIcon(R.drawable.googleg_disabled_color_18); //TODO change this icon : a trash can, for example. Also make it actually show an icon
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
                        return true;
                    } else {
                        Toast.makeText(this, "Could not delete this admin. Please try again.", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    //case R.id.delete_admin_context_item_disabled :
                    //Do nothing ?
                    //    return true;
                default:
                    return false;
            }
        }
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
            List<String> allUsernames = db.getAllUsernames();

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
            return (db.addAdmin(user.getUsername(), partner.getId()));
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
        boolean ret;
        try{
            db = new SQLHelper(this);
            ret = db.removeAdmin(username, currentPartner.getId());
        } catch (IOException e){
            e.printStackTrace();
            return false;
        } finally {
            if(db!=null){
                db.close();
            }
        }
        return ret;
    }
}
