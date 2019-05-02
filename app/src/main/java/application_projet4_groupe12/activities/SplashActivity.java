package application_projet4_groupe12.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import application_projet4_groupe12.R;
import application_projet4_groupe12.utils.ActivityUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import application_projet4_groupe12.utils.FirebaseUtils;
import io.fabric.sdk.android.Fabric;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        MobileAds.initialize(this, "ca-app-pub-7502022090495179~2813129044");
        RelativeLayout relativeLayout = findViewById(R.id.splashBody);

        FirebaseUtils.transferFromFirebase(this);

        relativeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.getInstance().invokeActivity(SplashActivity.this, SignUp.class, true);
            }
        }, 2000);
    }
}