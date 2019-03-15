package application_projet4_groupe12.fragment;

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
import com.google.firebase.auth.FirebaseAuth;
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

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout, container, false);
        fragment2_sign_up= view.findViewById(R.id.fragment2_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        fragment2_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = getView().findViewById(R.id.editText3);
                password = getView().findViewById(R.id.editText4);
                confirmPassword = getView().findViewById(R.id.editText5);

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
        try {
            db = new SQLHelper(getContext());

            if (db.doesUsernameExist(username.getText().toString()))  {
                Toast.makeText(getActivity(),  "This email already exists", Toast.LENGTH_SHORT).show();
            }
            else {
                if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                    int id = db.getFreeIDUser();
                    Date date = Calendar.getInstance().getTime();
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String today = formatter.format(date);
                    user = new User(id, username.getText().toString(), "", today,"albert", "le chat", "01/01/2000", "");
                    System.out.println("Utilisateur inséré : "+db.createUser(user));
                    dab.collection("Users").add(user);
                    Toast.makeText(getActivity(), "Account created", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WrongEmailFormatException e) {
            e.printStackTrace();
        } catch (WrongDateFormatException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}