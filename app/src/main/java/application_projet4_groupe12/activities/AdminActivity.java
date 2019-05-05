package application_projet4_groupe12.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import application_projet4_groupe12.activities.browse_clients.BrowseClientsActivity;
import application_projet4_groupe12.activities.settings.SettingsPartnerActivity;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.AppUtils;

import android.widget.ImageView;
import android.widget.TextView;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static boolean active = false;

    SharedPreferences session_share;

    Toolbar toolbar;
    FloatingActionButton fab_gen;
    DrawerLayout drawer;
    NavigationView navigationView;

    ImageView navHeaderImage;
    TextView navHeaderText1;
    TextView navHeaderText2;
    TextView navHeaderText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        handleToolbar();

        handleFloatingButtons();

        handleDrawer();

        handleNavigationView();
    }

    /*
        Quand on appuie sur le boutton de retour en arrière
     */
    @Override
    public void onBackPressed() {
        if(active){
            AppUtils.tapToExit(this,1);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Partner currentPartner = User.connectedUser.getAdministratedPartner(this);

        /* Remplir les données avec celles de la db locale*/
        String name = currentPartner.getName();
        navHeaderText1.setText(name);
        String userFullname = User.connectedUser.getFullName();
        navHeaderText2.setText(userFullname);
        String userEMail = User.connectedUser.getUsername();
        navHeaderText3.setText(userEMail);

        Bitmap bitmap = BitmapFactory.decodeFile(this.getFilesDir()+"/"+currentPartner.getImagePath());
        if(bitmap!=null) {
            navHeaderImage.setImageBitmap(bitmap);
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
        handleInterfaceButton();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_generate:
                startActivity(new Intent(AdminActivity.this, QRGenerateActivity.class));
                finish();
                break;

            case R.id.nav_logout:
                AppUtils.logout(this);
                break;

            case R.id.nav_admin_browse_clients:
                startActivity(new Intent(AdminActivity.this, BrowseClientsActivity.class));
                finish();
                break;

            case R.id.change_interface_admin:
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.nav_admin_settings:
                startActivity(new Intent(AdminActivity.this, SettingsPartnerActivity.class));
                finish();
                break;


            case R.id.go_to_instagram:
                AppUtils.openInstagram(this);
                finish();
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
        View headerLayout = navigationView.inflateHeaderView(R.layout.activity_main_admin_navigation_header);

        navHeaderImage = (ImageView) headerLayout.findViewById(R.id.activity_main_admin_navigation_header_image);
        navHeaderText1 = (TextView) headerLayout.findViewById(R.id.activity_main_admin_navigation_header_text1);
        navHeaderText2 = (TextView) headerLayout.findViewById(R.id.activity_main_admin_navigation_header_text2);
        navHeaderText3 = (TextView) headerLayout.findViewById(R.id.activity_main_admin_navigation_header_text3);

        MenuItem adminTitle = navigationView.getMenu().findItem(R.id.nav_admin_title);

        Partner partner = User.connectedUser.getAdministratedPartner(this);
        String partner_name = partner.getName();
        if(partner_name.length() == 0){
            adminTitle.setTitle("Account of ...");
        } else {
            adminTitle.setTitle("Account of " + User.connectedUser.getAdministratedPartner(this).getName());

        }
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
    }

    private void handleInterfaceButton(){
        if(User.connectedUser.isAdmin()) {
            MenuItem item_change_interface = findViewById(R.id.change_interface_admin);
            if(item_change_interface != null ){
                item_change_interface.setVisible(true); //View.GONE, View.INVISIBLE are available too.
            }
        }
    }
}
