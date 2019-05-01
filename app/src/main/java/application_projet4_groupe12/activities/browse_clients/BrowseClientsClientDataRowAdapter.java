package application_projet4_groupe12.activities.browse_clients;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import application_projet4_groupe12.R;


public class BrowseClientsClientDataRowAdapter extends ArrayAdapter<BrowseClientsClientDataAssociation> {

    public BrowseClientsClientDataRowAdapter(Context context, @NonNull List<BrowseClientsClientDataAssociation> elements){
        super(context, 0, elements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_browse_clients_row_adapter, parent, false);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder==null){
            viewHolder = new ViewHolder();
            viewHolder.userFullname = convertView.findViewById(R.id.browse_clients_row_adapter_user_fullname);
            viewHolder.userUsername = convertView.findViewById(R.id.browse_clients_row_adapter_user_username);
            viewHolder.pointsAmount = convertView.findViewById(R.id.browse_clients_row_adapter_user_points_amount);

            convertView.setTag(viewHolder);
        }

        BrowseClientsClientDataAssociation assoc = getItem(position);
        if(assoc!=null){
            viewHolder.userFullname.setText(assoc.getFullname());
            viewHolder.userUsername.setText(assoc.getUsername());
            viewHolder.pointsAmount.setText(Integer.toString(assoc.getPoints()));
        }

        convertView.setEnabled(false);
        return convertView;
    }

    private class ViewHolder {
        TextView userFullname;
        TextView userUsername;
        TextView pointsAmount;
    }
}
