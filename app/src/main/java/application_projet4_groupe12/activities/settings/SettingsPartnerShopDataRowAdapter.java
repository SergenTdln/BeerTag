package application_projet4_groupe12.activities.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Address;
import application_projet4_groupe12.entities.Shop;

public class SettingsPartnerShopDataRowAdapter extends ArrayAdapter<Shop> {

    public SettingsPartnerShopDataRowAdapter(Context context, @NonNull List<Shop> elements){
        super(context, 0, elements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_settings_partner_shop_row_adapter, parent, false);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder==null){
            viewHolder = new ViewHolder();
            viewHolder.shopDescr = (TextView) convertView.findViewById(R.id.settings_partner_row_adapter_shop_name);
            viewHolder.button = (ImageButton) convertView.findViewById(R.id.settings_partner_row_adapter_button);

            convertView.setTag(viewHolder);
        }

        Shop shop = getItem(position);
        Address address = null;
        if(shop!=null){
            SQLHelper db = null;
            try {
                db = new SQLHelper(getContext());
                address = db.getShopAddress(shop.getId());
            } catch (IOException e) {
                //TODO
            } finally {
                if(db!=null) {
                    db.close();
                }
            }
            if(address==null){
                //Skip this view
                System.err.println("Error instantiating view at position "+position);
            } else {
                viewHolder.shopDescr.setText(R.string.browse_clients_shop_title + address.stringRepresentation());
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO Launch new activity
                    }
                });
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView shopDescr;
        ImageButton button;
    }
}
