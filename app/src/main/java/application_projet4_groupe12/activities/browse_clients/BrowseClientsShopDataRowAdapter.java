package application_projet4_groupe12.activities.browse_clients;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import application_projet4_groupe12.R;

/**
 * Each element in this list adapter follows the layout in activity_browse_clients_shop_row_adapter.xml
 * A TextView (title, name of the shop)
 * A ListView (list of clients of this shop + their points there
 *  -> This class has to create the instances of BrowseClientsClientDataRowAdapter for those sub-lists
 */
class BrowseClientsShopDataRowAdapter extends ArrayAdapter<BrowseClientsShopDataAssociation> {

    public BrowseClientsShopDataRowAdapter(Context context, @NonNull List<BrowseClientsShopDataAssociation> elements){
        super(context, 0, elements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_browse_clients_shop_row_adapter, parent, false);
        }

        ViewHolder viewholder = (ViewHolder) convertView.getTag();
        if(viewholder==null) {
            viewholder = new ViewHolder();
            viewholder.shopTitle = convertView.findViewById(R.id.browse_clients_shop_row_adapter_shop_title);
            viewholder.listClients = convertView.findViewById(R.id.browse_clients_shop_row_adapter_clients_list);

            convertView.setTag(viewholder);
        }

        BrowseClientsShopDataAssociation assoc = getItem(position);
        if(assoc!=null){
            viewholder.shopTitle.setText(assoc.getShopDescr());
            viewholder.listClients.setAdapter(new BrowseClientsClientDataRowAdapter(getContext(), assoc.getList()));
        }

        return convertView;
    }

    private class ViewHolder {
        TextView shopTitle;
        ListView listClients;
    }
}
