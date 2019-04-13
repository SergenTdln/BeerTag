package application_projet4_groupe12.activities.settings;

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

import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.entities.User;

public class SettingsPartnerAdminDataRowAdapter extends ArrayAdapter<User> {

    public SettingsPartnerAdminDataRowAdapter(Context context, @NonNull List<User> elements){
        super(context, 0, elements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_settings_partner_admins_row_adapter, parent, false);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder==null){
            viewHolder = new ViewHolder();
            viewHolder.userFullName = (TextView) convertView.findViewById(R.id.settings_partner_row_adapter_user_full_name);
            viewHolder.username = (TextView) convertView.findViewById(R.id.settings_partner_row_adapter_user_username);
            viewHolder.userPic = (ImageView) convertView.findViewById(R.id.settings_partner_row_adapter_user_pic);

            convertView.setTag(viewHolder);
        }

        User user = getItem(position);
        if(user!=null){
                viewHolder.userFullName.setText(user.getFullName());
                viewHolder.username.setText(user.getUsername());
                viewHolder.userPic.setImageBitmap(BitmapFactory.decodeFile(convertView.getContext().getFilesDir()+"/"+user.getImagePath()));
        }

        return convertView;
    }

    //TODO allow to delete an admin by long-pressing the view ? be careful about not allowing to delete the last one, though ! @Martin

    class ViewHolder {
        TextView userFullName;
        TextView username;
        ImageView userPic;
    }
}
