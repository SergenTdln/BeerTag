package application_projet4_groupe12.activities.BrowsePoints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ScrollView;

import java.io.IOException;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;

public class BrowsePointsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_points);

        //Obtain list of "Associations"
        List<Association> elements = null;
        SQLHelper db = null;
        try{
            db = new SQLHelper(this);

            //TODO do some queries

        } catch (IOException e) {
            //TODO what do we do here ?
        } finally {
            if(db != null) {
                db.close();
            }
        }

        BrowseResultsRowAdapter brra = new BrowseResultsRowAdapter(BrowsePointsActivity.this, elements);
        listView.setAdapter(brra);
    }
}
