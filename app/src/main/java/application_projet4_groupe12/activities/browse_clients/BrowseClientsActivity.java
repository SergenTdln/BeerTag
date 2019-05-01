package application_projet4_groupe12.activities.browse_clients;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.Global;

public class BrowseClientsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_clients);
        listView = findViewById(R.id.browse_clients_listview);

    }

    @Override
    protected void onStart() {
        super.onStart();

        List<BrowseClientsShopDataAssociation> elements = new ArrayList<>();
        SQLHelper db = null;
        try {
            db = new SQLHelper(this);

            elements = db.getAllClientPoints(db.getPartnerIDFromUser(User.connectedUser.getId()));

            if(elements.isEmpty()){
                Toast.makeText(getApplicationContext(), "Empty list", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, "Error while initializing the database. Cannot display results.", Toast.LENGTH_SHORT).show();
            Log.i(Global.debug_text, "Browse points error "+e);
            // (exception in SQLHelper constructor)
        } finally {
            if(db != null) {
                db.close();
            }
        }

        BrowseClientsShopDataRowAdapter bcrra = new BrowseClientsShopDataRowAdapter(this, elements);
        listView.setAdapter(bcrra);
    }

    @Override
    public void onBackPressed(){
        AppUtils.end_home_admin(this);
    }
}
