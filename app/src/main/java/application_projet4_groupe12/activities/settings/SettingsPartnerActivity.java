package application_projet4_groupe12.activities.settings;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import java.io.IOException;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.Pair;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_partner);

        currentPartner = User.connectedUser.getAdministratedPartner(this);

        subTitle = (TextView) findViewById(R.id.settings_partner_sub_title);
        subTitle.setText(currentPartner.getName());

        newName = (EditText) findViewById(R.id.settings_partner_change_name);
        newName.setHint(currentPartner.getName());

        newAddress = (EditText) findViewById(R.id.settings_partner_change_address);
        newAddress.setHint(Integer.toString(currentPartner.getAddressID())); //TODO should handle an Address object instead of a String -> multiple fields/spinner/etc. @Martin

        picture = (ImageView) findViewById(R.id.settings_partner_picture);
        picture.setImageBitmap(BitmapFactory.decodeFile(this.getFilesDir()+"/"+currentPartner.getImagePath()));

        selectFileButton = (Button) findViewById(R.id.settings_partner_picture_select_button);
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                //select a file from local path
                String imagePath = null;
                currentPartner.setImagePath(imagePath);
            }
        });

        listAdmins = (ListView) findViewById(R.id.settings_partner_manage_admins_listview);
        fillListView(listAdmins, this);

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
                    currentPartner.setName(newNameS);
                }
                if(! newAddressS.equals("")){
                    currentPartner.setName(newAddressS);
                }
            }
        });
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
            return (db.addAdmin(new Pair(user.getUsername(), partner.getId())));
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
}
