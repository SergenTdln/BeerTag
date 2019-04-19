package application_projet4_groupe12.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import application_projet4_groupe12.BuildConfig;
import application_projet4_groupe12.R;
import application_projet4_groupe12.activities.browse_points.BrowsePointsActivity;
import application_projet4_groupe12.activities.find_partner.FindPartnerActivity;
import application_projet4_groupe12.activities.settings.SettingsPartnerActivity;
import application_projet4_groupe12.activities.settings.SettingsUserActivity;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.ActivityUtils;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.FacebookUtils;

import java.net.URL;

import application_projet4_groupe12.utils.Global;

import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static boolean active = false;

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
            AppUtils.tapToExit(this);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        if (drawer == null) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    @Override
    public void onStart() {
        super.onStart();

        if (ActivityUtils.getInstance().isLoggedInFacebook()) {
            /* Remplacer les données par celles du profil fb*/
            Log.i(Global.debug_text, "nav" + navigationView);
            String id = new FacebookUtils().getFacebookId();
            shared_login_choice = getSharedPreferences("session", MODE_PRIVATE);
            String session_name = shared_login_choice.getString("name", "");
            String session_email = shared_login_choice.getString("email", "");
            URL image_url = new FacebookUtils().getFacebookProfilePic();
            Log.i(Global.debug_text, "session img url / name / email " + image_url + session_name + session_email);
            Picasso.with(this).load(String.valueOf(image_url)).into(navHeaderImage);
            navHeaderText1.setText(session_name);
            navHeaderText2.setText(session_email);
        } else {
            /* Remplacer les données par celles de la db locale*/
            String userFullName = User.connectedUser.getFullName();
            Log.i(Global.debug_text, "userFullName" + userFullName);
            navHeaderText1.setText(userFullName);

            String userUsername = User.connectedUser.getUsername();
            Log.i(Global.debug_text, "getUsername" + userUsername);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        handleInterfaceButton();
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
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                finish();
                break;

            case R.id.nav_logout:
                //reset la session globale fb ou standar
                if (ActivityUtils.getInstance().isLoggedInFacebook()) {
                    String session_id = new FacebookUtils().getFacebookId();
                    SharedPreferences fb_login = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
                    fb_login.edit().clear().apply();
                } else {
                    SharedPreferences standard_login = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
                    //Déconnecter en local
                    User.disconnectUser(this);
                    standard_login.edit().clear().apply();
                    finish();
                }

                SharedPreferences shared_login_choice = getSharedPreferences("session", Context.MODE_PRIVATE);
                shared_login_choice.edit().clear().apply();

                //couper la session firebase
                FirebaseAuth.getInstance().signOut();
                //couper la session facebook
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(MainActivity.this, SignUp.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clears the Activity stack
                startActivity(intent);
                finish();
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
        View headerLayout = navigationView.inflateHeaderView(R.layout.activity_main_navigation_header);

        navHeaderImage = headerLayout.findViewById(R.id.activity_main_navigation_header_image);
        navHeaderText1 = headerLayout.findViewById(R.id.activity_main_navigation_header_text1);
        navHeaderText2 = headerLayout.findViewById(R.id.activity_main_navigation_header_text2);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
    }

    private void handleInterfaceButton() {
        if (User.connectedUser.isAdmin()) {
            MenuItem item_change_interface = findViewById(R.id.change_interface_user);
            if (item_change_interface != null) {
                item_change_interface.setVisible(true);
            }
        }
    }

    private void display_dialog_share_check(){
        SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        boolean dialog_share = session.getBoolean("dialog_share", false);
        session.edit().putBoolean("dialog_share", false).apply();
        if(dialog_share){
            display_dialog_share();
        }
    }


    private void display_dialog_share(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_share);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_version)).setText("Version " + BuildConfig.VERSION_NAME);

        dialog.findViewById(R.id.bt_getcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences shared = getSharedPreferences("session", MODE_PRIVATE);
//                shared.edit().putBoolean("expired_qr", false);
//                shared.edit().apply();
                startActivity(new Intent(MainActivity.this, ShareActivity.class));
                finish();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);

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
}
