package application_projet4_groupe12.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import application_projet4_groupe12.R;
import application_projet4_groupe12.activities.browse_clients.BrowseClientsActivity;
import application_projet4_groupe12.activities.settings.SettingsPartnerActivity;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.ActivityUtils;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.FacebookUtils;

import java.net.URL;

import application_projet4_groupe12.utils.Global;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static boolean active = false;

    SharedPreferences shared_login_choice;

    Toolbar toolbar;
    FloatingActionButton fab_gen;
    DrawerLayout drawer;
    NavigationView navigationView;

    ImageView navHeaderImage;
    TextView navHeaderText1;
    TextView navHeaderText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        handleToolbar();

        handleFloatingButtons();

        handleDrawer();

        handleNavigationView();
        /* Useless on this screen : see MainActivity.java instead
        shared_login_choice = getSharedPreferences("login_choice", MODE_PRIVATE);
        boolean choice_made = shared_login_choice.getBoolean("loggin_chosed", false);
        Log.v(Global.debug_text,"choice made "+choice_made);
        Log.v(Global.debug_text,"is admin"+User.connectedUser.isAdmin());
        if( User.connectedUser.isAdmin() &&  (!choice_made)){
            startActivity(new Intent(AdminActivity.this, LoginChoiceActivity.class));
            finish();
        }*/
    }

    /*
        Quand on appuie sur le boutton de retour en arrière
     */
    @Override
    public void onBackPressed() {
        if(active){
            AppUtils.tapToExit(this);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_admin);
        if (drawer == null) {
            Intent intent = new Intent(AdminActivity.this, AdminActivity.class);
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
            Log.i(Global.debug_text, "nav" + navHeaderImage);
            String id = new FacebookUtils().getFacebookId();
            SharedPreferences shared = getSharedPreferences(id, MODE_PRIVATE);
            String session_name = shared.getString("name", "");
            String session_email = shared.getString("email", "");
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

            Bitmap bitmap = BitmapFactory.decodeFile(this.getFilesDir()+"/"+User.connectedUser.getImagePath());
            if(bitmap!=null) {
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_generate:
                startActivity(new Intent(AdminActivity.this, QRGenerateActivity.class));
                break;

            case R.id.nav_logout:
                //reset la session globale fb ou standar
                if (ActivityUtils.getInstance().isLoggedInFacebook()) {
                    String session_id = new FacebookUtils().getFacebookId();
                    SharedPreferences fb_login = getApplicationContext().getSharedPreferences(session_id, Context.MODE_PRIVATE);
                    fb_login.edit().clear().apply();
                } else {
                    SharedPreferences standard_login = getApplicationContext().getSharedPreferences("session", Context.MODE_PRIVATE);
                    //Déconnecter en local
                    User.disconnectUser(this);
                    standard_login.edit().clear().apply();
                    finish();
                }

                SharedPreferences login_choice  =getSharedPreferences("login_choice",Context.MODE_PRIVATE);
                login_choice.edit().clear().apply();

                //couper la session firebase
                FirebaseAuth.getInstance().signOut();
                //couper la session facebook
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(AdminActivity.this, SignUp.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Clears the Activity stack
                startActivity(intent);
                finish();
                break;

            case R.id.nav_admin_browse_clients:
                startActivity(new Intent(AdminActivity.this, BrowseClientsActivity.class));
                break;
            case R.id.nav_admin_settings:
                startActivity(new Intent(AdminActivity.this, SettingsPartnerActivity.class));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleToolbar() {
        /*
         * Toolbar
         */
        toolbar = findViewById(R.id.app_bar_main_admin_toolbar);
        setSupportActionBar(toolbar);
    }

    private void handleFloatingButtons() {
        /*
         * Floating button - generate QR
         */
        fab_gen = findViewById(R.id.app_bar_main_admin_fab_gen);
        fab_gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, QRGenerateActivity.class));
                finish();
            }
        });
    }

    private void handleDrawer() {
        /*
         * Sliding drawer
         */
        drawer = findViewById(R.id.drawer_layout_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void handleNavigationView() {
        /*
         * Navigation view
         */
        navigationView = findViewById(R.id.admin_nav_view);
        navigationView.inflateMenu(R.menu.activity_main_navigation_drawer_admin);
        View headerLayout = navigationView.inflateHeaderView(R.layout.activity_main_navigation_header);

        navHeaderImage = (ImageView) headerLayout.findViewById(R.id.activity_main_navigation_header_image);
        navHeaderText1 = (TextView) headerLayout.findViewById(R.id.activity_main_navigation_header_text1);
        navHeaderText2 = (TextView) headerLayout.findViewById(R.id.activity_main_navigation_header_text2);

        MenuItem adminTitle = navigationView.getMenu().findItem(R.id.nav_admin_title);
        adminTitle.setTitle("Account of " + User.connectedUser.getAdministratedPartner(this).getName());
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
    }
}