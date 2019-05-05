package application_projet4_groupe12.activities.browse_points;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Promotion;
import application_projet4_groupe12.entities.User;

public class BrowsePointsResultsRowAdapter extends ArrayAdapter<BrowsePointsAssociation> {


    public BrowsePointsResultsRowAdapter(Context context, @NonNull List<BrowsePointsAssociation> elements){
        super(context, 0, elements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_browse_points_row_adapter, parent, false);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder==null)
        {
            viewHolder = new ViewHolder();
            viewHolder.partnerName = convertView.findViewById(R.id.browse_row_adapter_partner_name);
            viewHolder.shopAddress = convertView.findViewById(R.id.browse_row_adapter_shop_address);
            //viewHolder.shopDescr = (TextView) convertView.findViewById(R.id.);
            viewHolder.pointsAmount = convertView.findViewById(R.id.browse_row_adapter_points_amount);
            viewHolder.arrowButton = convertView.findViewById(R.id.browse_row_adapter_arrow_button);

            convertView.setTag(viewHolder);
        }

        BrowsePointsAssociation assoc = getItem(position);
        if(assoc!=null) {
            viewHolder.partnerName.setText(assoc.getShopDescr());
            viewHolder.shopAddress.setText(assoc.getShopAddress().stringRepresentation());
            //viewHolder.shopDescr.setText(assoc.getShopDescr());
            viewHolder.pointsAmount.setText(String.valueOf(assoc.getPoints()));


            ArrayList<Promotion> availablePromos = getPromotions(getContext(), assoc);
            if(! availablePromos.isEmpty()) {
                viewHolder.arrowButton.setBackgroundResource(R.drawable.ic_media_play_light);
                viewHolder.arrowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // New Activity with available Promos and a way to "consume" them
                        Intent intent = new Intent(getContext(), UsePromotionsActivity.class);
                        intent.putExtra("ShopID", assoc.getShopID());
                        intent.putExtra("ShopDescr", assoc.getShopDescr());
                        getContext().startActivity(intent);
                    }
                });
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView partnerName;
        TextView shopAddress;
        //TextView shopDescr;
        TextView pointsAmount;
        ImageButton arrowButton;
    }

    /**
     * Returns a list of the promotions currently available to the connected User,
     * given the information about his points contained in the BrowsePointsAssociation instance.
     * @param c the Context used to instantiate the database helper.
     * @param assoc a BrowsePointsAssociaton instance containing data about the current User.
     * @return a list of Promotion instances. This list might be empty if the User isn't currently eligible for any promotion.
     */
    private ArrayList<Promotion> getPromotions(Context c, BrowsePointsAssociation assoc){
        SQLHelper db = null;
        ArrayList<Promotion> ret = new ArrayList<>();
        try {
            db = new SQLHelper(c);
            ret = db.getAllAvailablePromotions(User.connectedUser, assoc);
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(c, "An error occurred; we could not retrieve the existing promotions from the database", Toast.LENGTH_SHORT).show();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
        return ret;
    }
}
