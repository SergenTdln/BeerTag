package application_projet4_groupe12.activities.BrowsePoints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.List;

import application_projet4_groupe12.R;

public class BrowsePointsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_points);

        //Obtain list of "Associations"
        List<Association> elements = null; //TODO

        BrowseResultsRowAdapter brra = new BrowseResultsRowAdapter(BrowsePointsActivity.this, elements);
        listView.setAdapter(brra);
    }
}
