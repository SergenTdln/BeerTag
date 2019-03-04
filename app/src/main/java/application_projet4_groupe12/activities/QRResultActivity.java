package application_projet4_groupe12.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.preference.AppPreference;
import application_projet4_groupe12.data.preference.PrefKey;
import application_projet4_groupe12.utils.AppUtils;


public class QRResultActivity extends AppCompatActivity {

    private Activity mActivity;
    private Context mContext;

    private TextView result;
    private FloatingActionButton copyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVars();
        initViews();
        initFunctionality();
        initListeners();
    }

    private void initVars() {
        mActivity = QRResultActivity.this;
        mContext = mActivity.getApplicationContext();
    }

    private void initViews() {
        setContentView(R.layout.activity_qrscan_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        result = (TextView) findViewById(R.id.result);
        copyButton = (FloatingActionButton) findViewById(R.id.copy);
    }

    private void initFunctionality() {

        getSupportActionBar().setTitle(getString(R.string.result));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ArrayList<String> arrayList = AppPreference.getInstance(mContext).getStringArray(PrefKey.RESULT_LIST);
        String lastResult = arrayList.get(arrayList.size()-1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result.setText(Html.fromHtml(lastResult, Html.FROM_HTML_MODE_LEGACY));
        } else {
            result.setText(Html.fromHtml(lastResult));
        }
        result.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void initListeners() {
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.copyToClipboard(mContext, result.getText().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                finish()
                startActivity(new Intent(QRResultActivity.this, MainActivity.class));
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
