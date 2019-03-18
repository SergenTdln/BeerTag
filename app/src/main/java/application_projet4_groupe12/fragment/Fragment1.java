package application_projet4_groupe12.fragment;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;


import application_projet4_groupe12.R;

import java.io.IOException;

import application_projet4_groupe12.activities.MainActivity;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.Hash;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment1 extends Fragment {

    private Button fragment1_sign_in;
    private SQLHelper db;
    private User user;

    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;

    private  CallbackManager callbackManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout, container, false);
        fragment1_sign_in = view.findViewById(R.id.fragment1_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        fragment1_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = getView().findViewById(R.id.sign_in_input_email);
                password = getView().findViewById(R.id.sign_in_input_password);

                String email = username.getText().toString();
                String pass = password.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                    Toast.makeText(getActivity(),R.string.login_fields, Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        db = new SQLHelper(getContext());

                        if(db.getHashedPassword(email).equals(Hash.hash(pass))) { //If password is correct

                            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();

                                        signIn(email);
                                    } else {
                                        Toast.makeText(getActivity(), R.string.login_check_credentials, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }

                }
            }
        });

        /*callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });*/

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null) {
            signIn(currentUser.getEmail());
        }
    }

    private void  signIn(String email) {
        try {
            db = new SQLHelper(getContext());

            boolean userExists = db.doesUsernameExist(email);
            System.out.println("Utilisateur existe :" + userExists);
            if (userExists) {

                User user = db.getUser(email);
                User.connectUser(getContext(), user);

                Intent intent = new Intent(getActivity(), MainActivity.class);

                /* creation d'une sessions globale lors du login */
                SharedPreferences shared = getApplicationContext().getSharedPreferences("session", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("email", email); // Storing string value
                editor.commit();
                /* end */

                startActivity(intent);
            } else {
                Toast.makeText(getActivity(),"not in database", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
