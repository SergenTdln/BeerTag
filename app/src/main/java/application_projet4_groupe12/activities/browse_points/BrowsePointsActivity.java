package application_projet4_groupe12.activities.browse_points;

import android.content.SharedPreferences;
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
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.Global;

public class BrowsePointsActivity extends AppCompatActivity {

    String sessionEmail;
    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_points);
        listView = findViewById(R.id.browse_points_listview);

        SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
        sessionEmail = shared.getString("email", "");
        Log.i(Global.debug_text, "login session email "+sessionEmail);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Obtain list of "Associations"
        List<BrowsePointsAssociation> elements = new ArrayList<>();
        SQLHelper db = null;
        try{
            db = new SQLHelper(this);

            elements = db.getAllPoints(sessionEmail);

        } catch (IOException e) {
            Toast.makeText(this, "Error while initializing the database. Cannot display results.", Toast.LENGTH_SHORT).show();
            Log.i(Global.debug_text, "Browse points error " + e);
            // (exception in SQLHelper constructor)
        } finally {
            if(db != null) {
                db.close();
            }
        }

        BrowsePointsResultsRowAdapter bprra = new BrowsePointsResultsRowAdapter(this, elements);
        listView.setAdapter(bprra);
    }

    @Override
    public void onBackPressed(){
        AppUtils.end_home(this);
    }
}
