package application_projet4_groupe12.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import application_projet4_groupe12.R;
import application_projet4_groupe12.utils.AppUtils;

public class ExpiredActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_qr_invalid);
        RelativeLayout relativeLayout = findViewById(R.id.invalid_body);

        AppCompatButton exit_button = findViewById(R.id.exit_button);
        exit_button.setOnClickListener(view -> {
//            startActivity(new Intent(ExpiredActivity.this, MainActivity.class));
//            finish();
            AppUtils.end_home(this);
        });
    }
}