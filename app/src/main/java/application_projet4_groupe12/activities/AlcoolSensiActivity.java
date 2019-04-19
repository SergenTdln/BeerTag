package application_projet4_groupe12.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import application_projet4_groupe12.R;
import application_projet4_groupe12.utils.Global;

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
            startActivity(new Intent(AlcoolSensiActivity.this, MainActivity.class));
            finish();
        });

    }

    private void clearCount(){
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        int scan_count = 0;
        scan_count = session.getInt("scan_count", scan_count);
        Log.v(Global.debug_text, "scan count before clear "+scan_count);
        editor.remove("scan_count");
        editor.apply();

        scan_count = session.getInt("scan_count", 0);
        Log.v(Global.debug_text, "scan count after clear "+scan_count);
    }
}
