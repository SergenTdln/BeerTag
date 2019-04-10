package application_projet4_groupe12.activities.browse_points;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Promotion;
import application_projet4_groupe12.entities.User;

public class UsePromotionsActivity extends AppCompatActivity {

    ListView listView;
    TextView subTitle2;

    ArrayList<Promotion> promotions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promotions = getIntent().getParcelableArrayListExtra("Promotions");
        setContentView(R.layout.activity_use_promotions);

        listView = (ListView) findViewById(R.id.use_promotions_listview);
        fillListView(listView, promotions);
        registerForContextMenu(listView);

        // TODO @Martin
        // Make an Array Adapter with all promotions
        // Each view is a button
        // Each button triggers the Promotion :
        //  -> QR code for the Partner to scan ?
        //  -> Substracts the points from DB
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.use_promotion_context, menu);
        menu.setHeaderIcon(R.drawable.ic_launcher_foreground); //TODO change this icon
        menu.setHeaderTitle("Activate the promotion"); //TODO use string resources @Martin

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        UsePromotionsRowAdapter.ViewHolder vh = (UsePromotionsRowAdapter.ViewHolder) info.targetView.getTag();
        switch (item.getItemId()) {
            case R.id.use_promotion_context_item:
                if (usePromotion(this, User.connectedUser, vh, promotions)) {
                    Toast.makeText(this, "Promotion successfully used", Toast.LENGTH_SHORT).show();
                    fillListView(listView, promotions); //Refreshing the displayed list
                    registerForContextMenu(listView);
                    return true;
                } else {
                    Toast.makeText(this, "Could not activate this Promotion. Please try again.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            //case R.id.use_promotions_context_item_2 :
             //Do nothing ?
            //    return true;
            default:
                return false;
        }

    }

    private void fillListView(ListView lv, List<Promotion> elems){
        lv.setAdapter(new UsePromotionsRowAdapter(this, elems));
    }

    /**
     * This method updates the DB and modified the passed Promotions list.
     */
    private boolean usePromotion(Context c, User user, UsePromotionsRowAdapter.ViewHolder vh, List<Promotion> list){
        SQLHelper db = null;
        try{
            db = new SQLHelper(c);

            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);

            boolean fromDB = db.usePromotion(vh.promoID, user, today);
            boolean fromList = removeFromList(list, vh.promoID);

            return (fromDB && fromList);
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "An error occurred; we could ropen the database.", Toast.LENGTH_SHORT).show();
        } finally {
            if(db!=null){
                db.close();
            }
        }
        return false;
    }

    private boolean removeFromList(List<Promotion> list, long targetID){
        int i=0;
        for (Promotion p : list) {
            if(p.getId()==targetID){
                list.remove(i);
                return true;
            } else {
                i++;
            }
        }
        return false; //Was not present in the list
    }
}
