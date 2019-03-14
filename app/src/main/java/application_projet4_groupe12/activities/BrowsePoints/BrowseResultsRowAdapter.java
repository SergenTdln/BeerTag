package application_projet4_groupe12.activities.BrowsePoints;

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

public class BrowseResultsRowAdapter extends ArrayAdapter<Association> {

    public BrowseResultsRowAdapter(Context context, @NonNull List<Association> elements){
        super(context, 0, elements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_browse_row_adapter, parent, false);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder==null)
        {
            viewHolder = new ViewHolder();
            viewHolder.partnerName = (TextView) convertView.findViewById(R.id.browse_row_adapter_partner_name);
            viewHolder.shopAddress = (TextView) convertView.findViewById(R.id.browse_row_adapter_shop_address);
            viewHolder.partnerPic = (ImageView) convertView.findViewById(R.id.browser_row_adapter_partner_pic);
            //viewHolder.shopDescr = (TextView) convertView.findViewById(R.id.);
            viewHolder.pointsAmount = (TextView) convertView.findViewById(R.id.browse_row_adapter_points_amount);

            convertView.setTag(viewHolder);
        }

        Association assoc = getItem(position);
        viewHolder.partnerName.setText(assoc.getPartnerName());
        viewHolder.shopAddress.setText(assoc.getShopAddress().stringRepresentation());
        //viewHolder.shopDescr.setText(assoc.getShopDescr());
        viewHolder.pointsAmount.setText(String.valueOf(assoc.getPoints()));
        try {
            viewHolder.partnerPic.setImageBitmap(BitmapFactory.decodeStream(getContext().getAssets().open(assoc.getPartnerImagePath())));
        } catch (IOException e){
            //Leave placeholder image
        }

        return convertView;
    }

    private class ViewHolder{
        TextView partnerName;
        ImageView partnerPic;
        TextView shopAddress;
        //TextView shopDescr;
        TextView pointsAmount;
    }
}
