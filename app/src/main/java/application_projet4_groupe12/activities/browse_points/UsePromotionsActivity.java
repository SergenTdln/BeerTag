package application_projet4_groupe12.activities.browse_points;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import application_projet4_groupe12.entities.Promotion;

public class UsePromotionsActivity extends AppCompatActivity {

    ArrayList<Promotion> promotions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promotions = getIntent().getParcelableArrayListExtra("Promotions");



        // TODO @Martin
        // Make an Array Adapter with all promotions
        // Each view is a button
        // Each button triggers the Promotion :
        //  -> QR code for the Partner to scan ?
        //  -> Substracts the points from DB
    }
}
