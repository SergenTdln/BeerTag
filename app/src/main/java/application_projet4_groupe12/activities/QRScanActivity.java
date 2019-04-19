package application_projet4_groupe12.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.Constants;
import application_projet4_groupe12.fragment.QRScanFragment;
import application_projet4_groupe12.item.ItemMainPager;
import application_projet4_groupe12.utils.AppUtils;

public class QRScanActivity extends AppCompatActivity {

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;

    private Activity mActivity;
    private Context mContext;

    private ViewPager mViewPager;
    private ArrayList<String> mFragmentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        mActivity = QRScanActivity.this;
        mContext = mActivity.getApplicationContext();
        mFragmentItems = new ArrayList<>();

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.viewpager);


        initQrFunctionality();

        setupViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new QRScanFragment(), "Generate Fragment");
        viewPager.setAdapter(adapter);
    }

    private void setUpViewPager() {
        mFragmentItems.add(getString(R.string.menu_scan));

        ItemMainPager itemMainPager = new ItemMainPager(getSupportFragmentManager(), mFragmentItems);
        mViewPager.setAdapter(itemMainPager);
        itemMainPager.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        if (drawer == null) {
            startActivity(new Intent(QRScanActivity.this, MainActivity.class));
            finish();
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    private void initQrFunctionality() {
        if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSION_REQ);
        } else {
            setUpViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpViewPager();
            } else {
                AppUtils.showToast(mContext, getString(R.string.permission_not_granted));
            }
        }
    }

}
