package application_projet4_groupe12.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.Constants;
import application_projet4_groupe12.fragment.QRScanFragment;
import application_projet4_groupe12.item.ItemMainPager;
import application_projet4_groupe12.utils.AppUtils;

public class QRScanActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

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

        ArrayList<String> mFragmentItems = new ArrayList<>();
        mFragmentItems.add(getString(R.string.menu_scan));

        ItemMainPager itemMainPager = new ItemMainPager(getSupportFragmentManager(), mFragmentItems);
        mViewPager.setAdapter(itemMainPager);
        itemMainPager.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed(){
        AppUtils.end_home(this);
    }

    private void initQrFunctionality() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSION_REQ);
        } else {
            setUpViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpViewPager();
            } else {
                AppUtils.showToast(this, getString(R.string.permission_not_granted));
            }
        }
    }

}
