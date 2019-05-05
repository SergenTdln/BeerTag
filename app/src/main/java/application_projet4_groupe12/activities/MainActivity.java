package application_projet4_groupe12.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import application_projet4_groupe12.activities.find_partner.FindPartnerActivity;
import application_projet4_groupe12.activities.settings.SettingsUserActivity;
import application_projet4_groupe12.data.Constants;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.ActivityUtils;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.FacebookUtils;

import java.net.URL;

import application_projet4_groupe12.utils.Global;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    static boolean active = false;
    private AdView mAdView;

    SharedPreferences shared_login_choice;
    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;

    ImageView navHeaderImage;
    TextView navHeaderText1;
    TextView navHeaderText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_user);

        loadAdMob();

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSION_REQ);
        }

        handleToolBar();

        handleFloatingButtons();

        handleDrawer();

        handleNavigationView();

        AdminChoiceCheck();

        display_dialog_share_check();

    }

    /*
        Quand on appuie sur le boutton de retour en arrière
     */
    @Override
    public void onBackPressed() {
        if (active) {
            AppUtils.tapToExit(this, 0);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        if (drawer == null) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ActivityUtils.getInstance().isLoggedInFacebook()) {
            /* Remplacer les données par celles du profil fb*/
            shared_login_choice = getSharedPreferences("session", MODE_PRIVATE);
            String session_name = shared_login_choice.getString("name", "");
            String session_email = shared_login_choice.getString("email", "");
            URL image_url = new FacebookUtils().getFacebookProfilePic();
            Picasso.with(this).load(String.valueOf(image_url)).into(navHeaderImage);
            navHeaderText1.setText(session_name);
            navHeaderText2.setText(session_email);
        } else {
            /* Remplacer les données par celles de la db locale*/
            String userFullName = User.connectedUser.getFullName();
            navHeaderText1.setText(userFullName);

            String userUsername = User.connectedUser.getUsername();
            navHeaderText2.setText(userUsername);

            System.err.println("Loading profile picture in Navigation View");
            Bitmap bitmap = BitmapFactory.decodeFile(this.getFilesDir() + "/" + User.connectedUser.getImagePath());
            if (bitmap != null) {
                navHeaderImage.setImageBitmap(bitmap);
            }
        }

        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_scan:
                startActivity(new Intent(MainActivity.this, QRScanActivity.class));
                break;
            case R.id.nav_browse_points:
                //Toast.makeText(getApplicationContext(), "Clicked on Browse points", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, BrowsePointsActivity.class));
                break;
            case R.id.nav_find_partner:
                startActivity(new Intent(MainActivity.this, FindPartnerActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, SettingsUserActivity.class));
                onStop();
                break;

            case R.id.change_interface_user:
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.go_to_instagram:
                AppUtils.openInstagram(this);
                finish();
                break;

            case R.id.nav_logout:
                AppUtils.logout(this);
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleToolBar() {
        /*
         * Toolbar
         */
        toolbar = findViewById(R.id.app_bar_main_user_toolbar);
        setSupportActionBar(toolbar);
    }

    private void handleFloatingButtons() {
        /*
         * Floating button - scan QR
         */
        fab = findViewById(R.id.app_bar_main_user_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, QRScanActivity.class));
                finish();
            }
        });
    }

    private void handleDrawer() {
        /*
         * Sliding drawer
         */
        drawer = findViewById(R.id.drawer_layout_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void handleNavigationView() {
        /*
         * Navigation view
         */
        navigationView = findViewById(R.id.user_nav_view);
        navigationView.inflateMenu(R.menu.activity_main_navigation_drawer_user);
        MenuItem interface_change_button = navigationView.findViewById(R.id.change_interface_user);
        handleInterfaceButton();
        View headerLayout = navigationView.inflateHeaderView(R.layout.activity_main_user_navigation_header);

        navHeaderImage = headerLayout.findViewById(R.id.activity_main_navigation_header_image);
        navHeaderText1 = headerLayout.findViewById(R.id.activity_main_navigation_header_text1);
        navHeaderText2 = headerLayout.findViewById(R.id.activity_main_navigation_header_text2);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
    }

    private void handleInterfaceButton() {
        if (!User.connectedUser.isAdmin()) {
            Menu navMenuLogIn = navigationView.getMenu();
            navMenuLogIn.findItem(R.id.change_interface_user).setVisible(false);
        }
    }


    private void display_dialog_share_check() {
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        boolean dialog_share = session.getBoolean("dialog_share", false);
        session.edit().putBoolean("dialog_share", false).apply();
        if (dialog_share) {
            AppUtils.display_dialog_share(this);
        }
    }


    private void AdminChoiceCheck() {
        shared_login_choice = getSharedPreferences("session", MODE_PRIVATE);
        boolean choice_made = shared_login_choice.getBoolean("loggin_chosed", false);
        Log.v(Global.debug_text, "choice made " + choice_made);
        Log.v(Global.debug_text, "is admin" + User.connectedUser.isAdmin());
        if (User.connectedUser.isAdmin() && (!choice_made)) {
            startActivity(new Intent(MainActivity.this, LoginChoiceActivity.class));
            finish();
        }
    }

    private void loadAdMob() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdView mAdView2 = (AdView) findViewById(R.id.adView2);
        AdRequest request = new AdRequest.Builder()
                .build();
        mAdView.loadAd(request);
        mAdView2.loadAd(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AppUtils.showToast(this, getString(R.string.permission_granted));
            } else {
                AppUtils.showToast(this, getString(R.string.permission_not_granted));
            }
        }
    }
}
