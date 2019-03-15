package application_projet4_groupe12.activities;

import android.content.Intent;
import android.Manifest;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import application_projet4_groupe12.R;
import application_projet4_groupe12.activities.browse_points.BrowsePointsActivity;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.AppUtils;

import java.io.IOException;
import java.util.ArrayList;
import android.support.v4.view.ViewPager;
import application_projet4_groupe12.item.ItemMainPager;
import application_projet4_groupe12.data.Constants;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private Button button;

    private Activity mActivity;
    private Context mContext;

    private ViewPager mViewPager;
    private ArrayList<String> mFragmentItems;

    private void startQr(){
        initQrVars();
        initQrViews();
        initQrFunctionality();
    }

    private void initQrVars() {
        mActivity = MainActivity.this;
        mContext = mActivity.getApplicationContext();
        mFragmentItems = new ArrayList<>();
    }

    private void initQrViews() {

        setContentView(R.layout.activity_qrscan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

//        getSupportActionBar().setTitle(R.string.menu_scan);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void initQrFunctionality() {
        if ((ContextCompat.checkSelfPermission( mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission( mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSION_REQ);
        } else {
            setUpViewPager();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * Floating button - scan QR
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQr();
            }
        });

        /*
         * Floating button - generate QR
         */
        FloatingActionButton fab_gen = (FloatingActionButton) findViewById(R.id.fab_gen);
        fab_gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, QRGenerateActivity.class));
            }
        });

        /*
         * Sliding drawer
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*
         * Navigation view
         */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        /*
         * Navigation view header data
         */
        ImageView navHeaderImage = (ImageView) findViewById(R.id.activity_main_navigation_header_image);
        TextView navHeaderText1 = (TextView) findViewById(R.id.activity_main_navigation_header_text1);
        TextView navHeaderText2 = (TextView) findViewById(R.id.activity_main_navigation_header_text2);
        try {
            navHeaderImage.setImageBitmap(BitmapFactory.decodeStream(this.getAssets().open(User.connectedUser.getImagePath()))); //TODO get picture from Facebook if connected this way
        } catch (IOException e) {
            //Do nothing : leave default image
        }
        String userFullName = User.connectedUser.getFullName();
        if(navHeaderText1 != null){ navHeaderText1.setText(userFullName); } //TODO le NullPointerException était causé par le fait que navHeader1 vaut NULL ici - A FIXER
        String userUsername = User.connectedUser.getUsername();
        if(navHeaderText2 != null){ navHeaderText2.setText(userUsername); } //TODO le NullPointerException était causé par le fait que navHeader2 vaut NULL ici - A FIXER
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer == null) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_scan:

                break;

            case R.id.nav_generate:
                startActivity(new Intent(MainActivity.this, QRGenerateActivity.class));
                break;

            case R.id.nav_browse_points:
                //Toast.makeText(getApplicationContext(), "Clicked on Browse points", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, BrowsePointsActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*public void  openSignUp() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }*/

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

    private void setUpViewPager() {

        mFragmentItems.add(getString(R.string.menu_scan));

        ItemMainPager itemMainPager = new ItemMainPager(getSupportFragmentManager(), mFragmentItems);
        mViewPager.setAdapter(itemMainPager);
        itemMainPager.notifyDataSetChanged();
    }
}
