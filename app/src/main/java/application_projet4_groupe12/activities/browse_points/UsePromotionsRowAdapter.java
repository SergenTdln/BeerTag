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

import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.entities.Promotion;

public class UsePromotionsRowAdapter extends ArrayAdapter<Promotion> {

    public UsePromotionsRowAdapter(Context c, @NonNull List<Promotion> elements){
        super(c, 0, elements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_use_promotions_row_adapter, parent, false);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder==null)
        {
            viewHolder = new ViewHolder();
            viewHolder.pointsRequired = convertView.findViewById(R.id.use_promotions_row_adapter_points_amount);
            viewHolder.pointsRequiredTitle = convertView.findViewById(R.id.use_promotions_row_adapter_points_amount_title);
            viewHolder.descr = convertView.findViewById(R.id.use_promotions_row_adapter_description);
            viewHolder.endDate = convertView.findViewById(R.id.use_promotions_row_adapter_end_date);
            viewHolder.endDateTitle = convertView.findViewById(R.id.use_promotions_row_adapter_end_date_title);
            viewHolder.reusability = convertView.findViewById(R.id.use_promotions_row_adapter_reusable);
            viewHolder.promotionPic = convertView.findViewById(R.id.use_promotions_row_adapter_pic);

            convertView.setTag(viewHolder);
        }

        Promotion promotion = getItem(position);
        if(promotion!=null) {
            viewHolder.promoID = promotion.getId();
            viewHolder.shopID = promotion.getIdShop();
            viewHolder.pointsRequired.setText(String.valueOf(promotion.getPointsRequired()));
            viewHolder.pointsRequiredTitle.setText("Points required");
            viewHolder.descr.setText(promotion.getDescription());
            viewHolder.endDate.setText(promotion.getEndDate());
            viewHolder.endDateTitle.setText("Valid until :");
            String isReusable; if(promotion.isReusable()){isReusable="this promotion is reusable";}else{isReusable="This promotion is NOT reusable";}
            viewHolder.reusability.setText(isReusable);
            viewHolder.promotionPic.setImageBitmap(BitmapFactory.decodeFile(convertView.getContext().getFilesDir() + "/" + promotion.getImagePath()));
            //TODO Use string resources instead @Martin
        }
        return convertView;
    }

    class ViewHolder {
        long promoID;
        long shopID;
        TextView pointsRequired;
        TextView pointsRequiredTitle;
        TextView descr;
        TextView endDate;
        TextView endDateTitle;
        TextView reusability;
        ImageView promotionPic;
    }
}
