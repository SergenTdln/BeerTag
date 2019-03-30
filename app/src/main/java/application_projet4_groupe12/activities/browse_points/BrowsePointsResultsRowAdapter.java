package application_projet4_groupe12.activities.browse_points;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import application_projet4_groupe12.R;

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
            viewHolder.partnerPic = convertView.findViewById(R.id.browser_row_adapter_partner_pic);
            //viewHolder.shopDescr = (TextView) convertView.findViewById(R.id.);
            viewHolder.pointsAmount = convertView.findViewById(R.id.browse_row_adapter_points_amount);

            convertView.setTag(viewHolder);
        }

        BrowsePointsAssociation assoc = getItem(position);
        if(assoc!=null) {
            viewHolder.partnerName.setText(assoc.getPartnerName());
            viewHolder.shopAddress.setText(assoc.getShopAddress().stringRepresentation());
            //viewHolder.shopDescr.setText(assoc.getShopDescr());
            viewHolder.pointsAmount.setText(String.valueOf(assoc.getPoints()));
            viewHolder.partnerPic.setImageBitmap(BitmapFactory.decodeFile(convertView.getContext().getFilesDir() + "/" + assoc.getPartnerImagePath()));
        }
        return convertView;
    }

    private class ViewHolder {
        TextView partnerName;
        ImageView partnerPic;
        TextView shopAddress;
        //TextView shopDescr;
        TextView pointsAmount;
    }

    //TODO allow to delete an admin by long-pressing the view ? be careful about not allowing to delete the last one, though ! @Martin
}
