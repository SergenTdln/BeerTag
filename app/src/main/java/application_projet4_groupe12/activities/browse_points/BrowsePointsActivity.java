package application_projet4_groupe12.activities.browse_points;

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

public class BrowsePointsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_points);
        listView = (ListView) findViewById(R.id.browse_listview);

        //Obtain list of "Associations"
        List<Association> elements = new ArrayList<>();
        SQLHelper db = null;
        try{
            db = new SQLHelper(this);
            SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
            String session_email = shared.getString("email", "");
            Log.i(Global.debug_text, "login session email "+session_email);
            elements = db.getAllPoints(session_email);

        } catch (IOException e) {
            //TODO what do we do here ?
            Log.i(Global.debug_text, "Browse points error "+e);
            // (exception in SQLHelper constructor)
        } finally {
            if(db != null) {
                db.close();
            }
        }

        BrowseResultsRowAdapter brra = new BrowseResultsRowAdapter(BrowsePointsActivity.this, elements);
        listView.setAdapter(brra);
    }
}
