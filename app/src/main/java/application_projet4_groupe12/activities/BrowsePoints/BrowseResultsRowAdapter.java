package application_projet4_groupe12.activities.BrowsePoints;

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
import application_projet4_groupe12.entities.Partner;

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
            viewHolder.partnerText = (TextView) convertView.findViewById(R.id.browse_row_adapter_partner_name);
            //etc TODO

            convertView.setTag(viewHolder);
        }

        Association assoc = getItem(position);
        viewHolder.partnerText.setText(assoc.getPartnerName());
        //etc TODO

        return convertView;
    }

    private class ViewHolder{
        TextView partnerText;
        TextView pointsAmount;
    }
}
