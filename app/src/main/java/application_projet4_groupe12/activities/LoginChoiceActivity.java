package application_projet4_groupe12.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;

import application_projet4_groupe12.R;
import application_projet4_groupe12.utils.ActivityUtils;
import io.fabric.sdk.android.Fabric;

public class LoginChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_account_choice);
        RelativeLayout relativeLayout = findViewById(R.id.splashBody);

        Button goToAdmin = findViewById(R.id.button_go_to_admin);
        goToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences shared = getApplicationContext().getSharedPreferences("login_choice", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean("loggin_chosed", true); // Storing boolean - true/false
                editor.commit();
                startActivity(new Intent(LoginChoiceActivity.this, AdminActivity.class));
                finish();
            }
        });

        Button goToUser = findViewById(R.id.button_go_to_user);
        goToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences shared = getApplicationContext().getSharedPreferences("login_choice", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean("loggin_chosed", true); // Storing boolean - true/false
                editor.commit();
                startActivity(new Intent(LoginChoiceActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
