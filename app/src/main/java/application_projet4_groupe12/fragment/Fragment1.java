package application_projet4_groupe12.fragment;

import android.content.Intent;
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
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.CallbackManager;


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
    private Button fragment1_forgot_password;
    private SQLHelper db;
    private User user;

    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;

    private  CallbackManager callbackManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout, container, false);
        fragment1_sign_in = view.findViewById(R.id.fragment1_sign_in);
        fragment1_forgot_password = view.findViewById(R.id.fragment1_forgot_password);

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
                        if(true) {
                            String hashedPassword = Hash.hash(pass);
                            System.out.println("hashedPassword = "+hashedPassword);
/*
                            if (db.getHashedPassword(email).equals(hashedPassword)) { //If password is correct
*/
                                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();

                                            signIn(email);

                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            if(getActivity()!=null){
                                                getActivity().finish();
                                            }
                                        } else {Exception e = task.getException();
                                            if (e instanceof FirebaseNetworkException){
                                                Toast.makeText(getActivity(), "Could not connect to your account. Are you offline ?", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Firebase Failed" + e, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });
/*
                            } else {
                                Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_SHORT).show();
                            }
*/
                        } else {
                            Toast.makeText(getActivity(), "This username does not exist", Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }

                }
            }
        });

        fragment1_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = getView().findViewById(R.id.sign_in_input_email);
                String email = username.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(),R.string.email_field, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(),"ok", Toast.LENGTH_SHORT).show();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(),"password sent", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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

    private void signIn(String email) {
        try {
            db = new SQLHelper(getContext());

            boolean userExists = db.doesUserExist(email);
            System.out.println("Utilisateur existe :" + userExists);
            if (userExists) {

                User user = db.getUser(email);
                User.connectUser(getContext(), user);

                /* creation d'une sessions globale lors du login */
                SharedPreferences shared = getApplicationContext().getSharedPreferences("session", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("email", email); // Storing string value
                editor.apply();
                /* end */
            } else {
                Toast.makeText(getActivity(),"Username not in database", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
