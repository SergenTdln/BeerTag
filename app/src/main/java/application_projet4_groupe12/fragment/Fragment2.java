package application_projet4_groupe12.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Any;
import java.util.Map;
import java.util.HashMap;

import com.google.firebase.firestore.FirebaseFirestore;

import application_projet4_groupe12.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import application_projet4_groupe12.activities.MainActivity;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;
import application_projet4_groupe12.utils.Hash;
import application_projet4_groupe12.utils.Global;

import static android.content.ContentValues.TAG;
import static java.lang.System.err;

public class Fragment2 extends Fragment {

    private Button fragment2_sign_up;
    private SQLHelper db;
    private User user;
    private FirebaseFirestore dab = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText firstName;
    private EditText lastName;
    private EditText birthDate;

    private int MIN_PASSWD_LENGTH = 6; //This is a Firebase limitation

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout, container, false);
        fragment2_sign_up = view.findViewById(R.id.sign_up_user_button_out);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        fragment2_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = getView().findViewById(R.id.sign_up_user_input_email);
                password = getView().findViewById(R.id.sign_up_user_input_password);
                confirmPassword = getView().findViewById(R.id.sign_up_user_input_password_confirm);
                firstName = getView().findViewById(R.id.sign_up_user_input_first_name);
                lastName = getView().findViewById(R.id.sign_up_user_input_last_name);
                birthDate = getView().findViewById(R.id.sign_up_user_input_birthday);

                signUp();
            }
        });

        return view;
    }
    public void Transfer(){
        dab.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                                try {
                                    db.createUser(user);
                                }
                                catch (WrongEmailFormatException e) {
                                    e.printStackTrace();
                                }
                                catch (WrongDateFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void Transfer2(){
        dab.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                int id = Integer.parseInt(document.getString("id"));
                                String today = document.getString("creationDate");
                                String firstname = document.getString("firstName");
                                String lastname = document.getString("lastName");
                                user = new User(id, document.getString("username"), "", today, firstname, lastname, "BirthDate", ""); //TODO à refaire
                                try {
                                    db.createUser(user);
                                }
                                catch (WrongEmailFormatException e) {
                                    e.printStackTrace();
                                }
                                catch (WrongDateFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void signUp() {
        String email = username.getText().toString();
        String pass = password.getText().toString();
        String confirmPass = confirmPassword.getText().toString();
        if( email.equals("") || pass.equals("") || confirmPass.equals("") ||
            firstName.getText().toString().equals("") ||
            lastName.getText().toString().equals("") ||
            birthDate.getText().toString().equals("") )
        {
            Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            db = new SQLHelper(getContext());

            if (db.doesUsernameExist(email))  {
                Toast.makeText(getActivity(),  "This email already exists", Toast.LENGTH_SHORT).show();
            }
            else {
                if(pass.length() >= MIN_PASSWD_LENGTH) {
                    if (pass.equals(confirmPass)) {
                        int id = db.getFreeIDUser();

                        Date date = Calendar.getInstance().getTime();
                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String today = formatter.format(date);

                        user = new User(id, email, Hash.hash(pass), today, firstName.getText().toString(), lastName.getText().toString(), birthDate.getText().toString(), "");
                        try {
                            System.out.println("Utilisateur inséré : " + db.createUser(user));
                        } catch (WrongEmailFormatException e){
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Invalid email format", Toast.LENGTH_SHORT).show();
                            return;
                        } catch (WrongDateFormatException e){
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Invalid date format : please use DD/MM/YYYY", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dab.collection("Users").add(user);
                        Toast.makeText(getActivity(), "Account created", Toast.LENGTH_SHORT).show();

                        Log.d(Global.debug_text, "Firebase instance: " + mAuth);
                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();

                                    signIn(email);
                                } else {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseNetworkException){
                                        Toast.makeText(getActivity(), "Could not create your account. Are you offline ?", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Firebase Failed" + e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                    } else {
                        Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.close();
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