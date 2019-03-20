package application_projet4_groupe12.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;
import application_projet4_groupe12.fragment.Fragment1;
import application_projet4_groupe12.fragment.Fragment2;
import application_projet4_groupe12.activities.SignUp;
import application_projet4_groupe12.fragment.Fragment3;
import application_projet4_groupe12.utils.FacebookUtils;
import application_projet4_groupe12.utils.Global;
import application_projet4_groupe12.utils.ActivityUtils;


public class SignUp extends AppCompatActivity {

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    private FirebaseFirestore db_firebase = FirebaseFirestore.getInstance();

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private SQLHelper db;


    LoginButton loginButton;
    CallbackManager mCallbackManager;
    String TAG ="debug";
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ActivityUtils.getInstance().isLoggedInFacebook()){
            finish();
            startActivity(new Intent(SignUp.this, MainActivity.class));
        }
        //FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_sign_up);
        loginButton = findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.application_projet4_groupe12",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i(Global.debug_text, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email, public_profile");

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.i(TAG,"Hello"+loginResult.getAccessToken().getToken());
//                Toast.makeText(SignUp.this, "Token:"+loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Retrieve facebook user data
                                try {
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String name = object.getString("name");

                                    Log.v(Global.debug_text, "id fb"+id);
                                    Log.v(Global.debug_text, "email fb"+email);
                                    Log.v(Global.debug_text, "name fb"+name);
//
                                    /* initalize facebook session
                                    * src: https://webkul.com/blog/how-to-manage-session-in-android-app/ */
                                    SharedPreferences shared = getApplicationContext().getSharedPreferences(id, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = shared.edit();

//                                    URL image_url = new FacebookUtils().getFacebookProfilePic();
//                                    Log.i(Global.debug_text,"login session image url string"+image_url);


                                    editor.putBoolean("loggedIn", true); // Storing boolean - true/false
                                    editor.putString("id_facebook", id); // Storing boolean - true/false
                                    editor.putString("email", email); // Storing string value
                                    editor.putString("name", name); // Storing integer value
//                                    editor.putString("session_img_url", String.valueOf(image_url)); // Storing integer value
                                    editor.commit();
                                    /* end */

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "canceled madafaka");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener(){


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user!=null){
                    name = user.getDisplayName();
                    Toast.makeText(SignUp.this,""+user.getDisplayName(),Toast.LENGTH_LONG).show();
                    Log.d(Global.debug_text, "user connected"+user.getDisplayName());
                }else {
                    Toast.makeText(SignUp.this,"something went wrong",Toast.LENGTH_LONG).show();
                }


            }
        };

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(Global.debug_text, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.w(Global.debug_text, "signInWithCredential", task.getException());
                            String user_id = Profile.getCurrentProfile().getId();
                            Log.w(Global.debug_text, "facebook user ud "+user_id, task.getException());
//                            Toast.makeText(SignUp.this, "Success",
//                                    Toast.LENGTH_SHORT).show();

                            //TODO : récupérer le prénom, l'adresse email et la photo de profil.
                            //sauvegarder prénom email et photo en bdd local
                            //synchroniser photo dans le cloud
                            /* synchroniser avec le cloud > firebase */

                            String session_id = new FacebookUtils().getFacebookId();
                            SharedPreferences shared = getSharedPreferences(session_id, MODE_PRIVATE);
                            String session_name = shared.getString("name", "");
                            String session_email = shared.getString("email", "");

                            /* on split l'username name en firstname et lastname*/
                            String[] str = session_name.split(" ");  //now str[0] is "hello" and str[1] is "goodmorning,2,1"
                            String firstname = str[0];  //hello
                            String lastname = str[1];  //hello
                            URL imagepath = new FacebookUtils().getFacebookProfilePic();

//                            User user = new User(
//                                    Integer.valueOf(session_id),
//                                    name,
//                                    null,
//                                    "04/05/2010",
//                                    firstname,
//                                    lastname,
//                                    "04/05/2010",
//                                    imagepath.toString());

                            /*
                            TODO : faire l'ajout en DB
                             */

//                            Context ct = getApplicationContext();
//                            try {
//                                db = new SQLHelper(ct);
////                                db = new SQLHelper(getContext());
//
//                                if(db.doesUsernameExist(session_email)){
//                                    Log.d(Global.debug_text, "fb user already exists in db");
//                                } else {
////                                    int id = Integer.valueOf(session_id);
//                                    int id = Integer.valueOf(session_id);
//                                    Date date = Calendar.getInstance().getTime();
//                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//                                    String today = formatter.format(date);
//
//                                    User user = new User(
//                                            id,
//                                            name,
//                                            null,
//                                            today,
//                                            firstname,
//                                            lastname,
//                                            "04/05/2010",
//                                            imagepath.toString()
//                                    );
//
//                                    Log.d(Global.debug_text, "utilisateur fb inséré");
//
//                                    db_firebase.collection("Users").add(user);
//                                    Log.d(Global.debug_text, "utilisateur fb inséré à firebase");
//
//                                }
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Log.d(Global.debug_text, "fb login db error"+e);
//                            }


                            startActivity(new Intent(SignUp.this, MainActivity.class));
                        }else{
                            Toast.makeText(SignUp.this, "Authentication error",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private void setupViewPager(ViewPager viewPager){
        if(!ActivityUtils.getInstance().isLoggedInFacebook()){
            SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new Fragment1(), "Fragment1");
            adapter.addFragment(new Fragment2(), "Fragment2");
            adapter.addFragment(new Fragment3(), "Fragment3");
            viewPager.setAdapter(adapter);
        }

    }

    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
    }


}