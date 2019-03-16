package application_projet4_groupe12.activities.browse_points;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;

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
            elements = db.getAllPoints(User.connectedUser.getUsername());
            //if(elements.isEmpty()){
            //    Toast.makeText(getApplicationContext(), "Empty list", Toast.LENGTH_SHORT).show();
            //}
        } catch (IOException e) {
            //TODO what do we do here ?
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
