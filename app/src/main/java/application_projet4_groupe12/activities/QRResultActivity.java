package application_projet4_groupe12.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import application_projet4_groupe12.BuildConfig;
import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.data.preference.AppPreference;
import application_projet4_groupe12.data.preference.PrefKey;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.ActivityUtils;
import application_projet4_groupe12.utils.Encryption;
import application_projet4_groupe12.utils.Global;

import static com.facebook.FacebookSdk.getApplicationContext;


public class QRResultActivity extends AppCompatActivity {

    private Activity mActivity;
    private Context mContext;

    private SQLHelper db;
    private TextView result;
    private FloatingActionButton copyButton;

    private Boolean valid_qr_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        initViews();
        initFunctionality();

        if(!hasQrExpired()){
            loadMainActivity();
            increaseCount();
        } else {
            loadExpiredActivity();
        }
        //invalid ok mais ko valid TODO
        /*if ( (!hasQrExpired()) && (valid_qr_code)) {
            loadMainActivity();
            increaseCount();
        } else if (!valid_qr_code){
            loadExpiredActivity();
        } else {
            loadExpiredActivity();
        }*/
    }

    private void initVars() {
        mActivity = QRResultActivity.this;
        mContext = mActivity.getApplicationContext();
    }

    private void initViews() {
        setContentView(R.layout.activity_qrscan_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        result = findViewById(R.id.result);
        copyButton = findViewById(R.id.copy);
    }

    private void initFunctionality() {
        checkConsomation();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.result));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        ArrayList<String> arrayList = AppPreference.getInstance(mContext).getStringArray(PrefKey.RESULT_LIST);
        String lastResult = arrayList.get(arrayList.size() - 1);
        String decryptedQrCode = Encryption.decryptQrCode(lastResult, this);

        /* recup des infos via qr code */
        if(decryptedQrCode == null){
            Log.v(Global.debug_text,"invalid qr code decrypted");
            valid_qr_code = false;
        } else {
            String[] data = decryptedQrCode.split("_5%/");
            int achat = Integer.parseInt(data[0]);
            long shopID = Long.valueOf(data[1]);

            if(!hasQrExpired()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            result.setText(Html.fromHtml(lastResult, Html.FROM_HTML_MODE_LEGACY));
                    result.setText(Html.fromHtml("qr code crypté= " + lastResult + "| qr code  décrypté= " + decryptedQrCode, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    result.setText(Html.fromHtml(lastResult, Html.FROM_HTML_MODE_LEGACY));
                }
                result.setMovementMethod(LinkMovementMethod.getInstance());
                try {
                    db = new SQLHelper(this);
                    db.addPoints(User.connectedUser.getUsername(), achat, shopID);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v(Global.debug_text, "" + e);
                } finally {
                    db.close();
                }
            }
        }



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(QRResultActivity.this, AdminActivity.class));
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadMainActivity() {
        SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
        shared.edit().putBoolean("dialog_share", true).apply();
        ActivityUtils.getInstance().invokeActivity(this, MainActivity.class, true);
        finish();
    }

    private void loadExpiredActivity() {
        SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
        shared.edit().putBoolean("expired_qr", false).apply();
        ActivityUtils.getInstance().invokeActivity(this, ExpiredActivity.class, true);
        finish();
    }

    private Boolean hasQrExpired() {
        Boolean has_expired = false;
        SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
        Boolean expired_qr = shared.getBoolean("expired_qr", false);
        if (expired_qr) {
            Log.v(Global.debug_text, "expired initcheckqrexp");
            has_expired = true;
        }
        return has_expired;
    }

    private void createWarningNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL = "n_alcool";
        int NOTIFICATION_ID = 1;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alcool);

        Intent notifyIntent = new Intent(this, AlcoolSensiActivity.class);
        // Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL);
        notificationBuilder.setContentIntent(notifyPendingIntent);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Beer Tag")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(getResources().getString(R.string.alcool_notification_title))
                .setContentText(getResources().getString(R.string.alcool_notification_text))
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null));

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void increaseCount() {
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        int prev_count = 0;
        prev_count = session.getInt("scan_count", prev_count);
        int new_count = prev_count + 1;
        editor.putInt("scan_count", new_count).apply();

    }

    private void checkConsomation() {
        SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
        int scan_count = 0;
        scan_count = shared.getInt("scan_count", scan_count);

        if (scan_count >= 3) {
            createWarningNotification();
        }
    }

}

