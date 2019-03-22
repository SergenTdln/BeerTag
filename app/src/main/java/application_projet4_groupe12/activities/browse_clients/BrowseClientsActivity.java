package application_projet4_groupe12.activities.browse_clients;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.Global;

public class BrowseClientsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_clients);
        listView = findViewById(R.id.browse_clients_listview);

        List<BrowseClientsAssociation> elements = new ArrayList<>();
        SQLHelper db = null;
        try {
            db = new SQLHelper(this);

            elements = db.getAllCientPoints(db.getAdminFromUser(User.connectedUser.getId()));

        } catch (IOException e) {
            //TODO what do we do here ?
            Log.i(Global.debug_text, "Browse points error "+e);
            // (exception in SQLHelper constructor)
        } finally {
            if(db != null) {
                db.close();
            }
        }

        BrowseClientsResultsRowAdapter bcrra = new BrowseClientsResultsRowAdapter(this, elements);
        listView.setAdapter(bcrra);
    }
}
