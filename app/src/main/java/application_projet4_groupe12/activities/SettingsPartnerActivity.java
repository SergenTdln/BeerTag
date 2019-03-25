package application_projet4_groupe12.activities;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import application_projet4_groupe12.R;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;

public class SettingsPartnerActivity extends AppCompatActivity {

    TextView subTitle;
    EditText newName;
    EditText newAddress;
    ImageView picture;
    Button selectFileButton;
    ImageButton buttonOut;

    ListView listShops;

    private Partner currentPartner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_partner);

        currentPartner = User.connectedUser.getAdminPartner(this);

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
                //TODO
                //select a file from local path
                String imagePath = null;
                currentPartner.setImagePath(imagePath);
            }
        });

        // Handle the ListView - Adapter etc.
        // TODO
        listShops = (ListView) findViewById(R.id.settings_partner_manage_shops_listview);
        listShops.setAdapter(null);

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
}
