package application_projet4_groupe12.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import application_projet4_groupe12.R;

import static application_projet4_groupe12.utils.AppUtils.end_home;

public class AlcoolSensiActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alcool_sensi);

        Button read = findViewById(R.id.read);
        read.setOnClickListener(view -> {
            clearCount();
//            startActivity(new Intent(AlcoolSensiActivity.this, MainActivity.class));
//            finish();
            end_home(this);
        });

    }

    private void clearCount(){
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        editor.remove("scan_count");
        editor.apply();
    }
}
