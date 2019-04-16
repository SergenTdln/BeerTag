package application_projet4_groupe12.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.data.preference.AppPreference;
import application_projet4_groupe12.data.preference.PrefKey;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.ActivityUtils;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.Encryption;
import application_projet4_groupe12.utils.Global;


public class QRResultActivity extends AppCompatActivity {

    private Activity mActivity;
    private Context mContext;

    private SQLHelper db;
    private TextView result;
    private FloatingActionButton copyButton;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVars();
        initViews();
        initFunctionality();

        if(initCheckQrExp()){
            loadShareActivity();
        } else{
            loadExpiredActivity();
        }
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

        getSupportActionBar().setTitle(getString(R.string.result));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ArrayList<String> arrayList = AppPreference.getInstance(mContext).getStringArray(PrefKey.RESULT_LIST);
        String lastResult = arrayList.get(arrayList.size() - 1);
        String decryptedQrCode = Encryption.decryptQrCode(lastResult, this);

        /* recup des infos via qr code */
        String[] data = decryptedQrCode.split("_5%/");
        int achat = Integer.parseInt(data[0]);
        Long partnerId = Long.valueOf(data[1]);

        Log.v(Global.debug_text, "montant= " + achat + " partnerId= " + partnerId);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            result.setText(Html.fromHtml(lastResult, Html.FROM_HTML_MODE_LEGACY));
            result.setText(Html.fromHtml("qr code crypté= " + lastResult + "| qr code  décrypté= " + decryptedQrCode, Html.FROM_HTML_MODE_LEGACY));
        } else {
            result.setText(Html.fromHtml(lastResult));
        }
        result.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            db = new SQLHelper(this);
            db.addPoints(User.connectedUser.getUsername(), achat, partnerId);
//            db.addPoints(User.connectedUser.getUsername(), Integer.parseInt(encryptedQrCode), 1); //TODO à terminer : il me faut accès à l'ID du shop qui a généré le qr code
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "" + e);
        } finally {
            db.close();
        }
    }

//    private void initListeners() {
//        copyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AppUtils.copyToClipboard(mContext, result.getText().toString());
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                finish()
                startActivity(new Intent(QRResultActivity.this, AdminActivity.class));
                finish();
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadShareActivity(){
        ActivityUtils.getInstance().invokeActivity(this, ShareActivity.class, true);
        finish();
    }

    private void loadExpiredActivity(){
        ActivityUtils.getInstance().invokeActivity(this, ExpiredActivity.class, true);
        finish();
    }

    private Boolean initCheckQrExp(){
        Boolean expired = false;
        SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
        Boolean expired_qr = shared.getBoolean("expired_qr", false);
        if(expired_qr){
            expired = true;
//            startActivity(new Intent(QRResultActivity.this, MainActivity.class));
//            finish();
        }
        return expired;
    }

}

