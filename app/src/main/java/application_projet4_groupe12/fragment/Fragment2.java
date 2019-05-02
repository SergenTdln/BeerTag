package application_projet4_groupe12.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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

    SharedPreferences session;

    private int MIN_PASSWD_LENGTH = 6; //This is a Firebase limitation

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout, container, false);
        fragment2_sign_up = view.findViewById(R.id.sign_up_user_button_out);

        username = view.findViewById(R.id.sign_up_user_input_email);
        password = view.findViewById(R.id.sign_up_user_input_password);
        confirmPassword = view.findViewById(R.id.sign_up_user_input_password_confirm);
        firstName = view.findViewById(R.id.sign_up_user_input_first_name);
        lastName = view.findViewById(R.id.sign_up_user_input_last_name);
        birthDate = view.findViewById(R.id.sign_up_user_input_birthday);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Activity here = getActivity();
        if(here!=null) {
            session = here.getPreferences(Context.MODE_PRIVATE);
        }

        fragment2_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signUp();
            }
        });

        return view;
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

            if (db.doesUserExist(email))  {
                Toast.makeText(getActivity(),  "This email already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            if(! (pass.length() >= MIN_PASSWD_LENGTH)) {
                Toast.makeText(getActivity(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                return;
            }
            if (! pass.equals(confirmPass)) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = db.getFreeIDUser();

            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);

            user = new User(id, email, Hash.hash(pass), today, firstName.getText().toString(), lastName.getText().toString(), birthDate.getText().toString(), Long.toString(id)+"_pic.png", false);

            /*Adding the user to the DB (local)*/
            try {
                boolean success = db.addUser(user);
                System.out.println("Utilisateur inséré : " + success);
            } catch (WrongEmailFormatException e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            } catch (WrongDateFormatException e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "Invalid date format : please use DD/MM/YYYY", Toast.LENGTH_SHORT).show();
                return;
            }

            /*Creating the Firebase User*/
            mAuth.createUserWithEmailAndPassword(email, Hash.hash(pass)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseNetworkException){
                            Toast.makeText(getActivity(), "Could not create your account. Are you offline ?", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Firebase Failed" + e, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            /*Adding the user to the DB (Firestore)*/
            dab.collection("User").document(String.valueOf(id)).set(user, SetOptions.merge());
            Toast.makeText(getActivity(), "Account created", Toast.LENGTH_SHORT).show();

            //Log.d(Global.debug_text, "Firebase instance: " + mAuth);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    private void  signIn(String email) {
        try {
            db = new SQLHelper(getContext());

            boolean userExists = db.doesUserExist(email);
            System.out.println("Utilisateur existe :" + userExists);
            if (userExists) {

                User user = db.getUser(email);
                User.connectUser(getContext(), user);

                /* creation d'une sessions globale lors du login */
                SharedPreferences.Editor editor = session.edit();
                editor.putString("email", email); // Storing string value
                editor.putBoolean("login_status", true);
                editor.apply();
                /* end */

            } else {
                Toast.makeText(getActivity(),"not in database", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

//    protected void sendEmail(String email) {
//        Log.i(Global.debug_text,"Send email");
//
//        String[] TO = {email};
//        String[] CC = {"fb.sergen.tasdelen@gmail.com"}; //rien que pour tester; à virer
//        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//        emailIntent.setData(Uri.parse("mailto:"));
//        emailIntent.setType("text/plain");
//
//
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//        emailIntent.putExtra(Intent.EXTRA_CC, CC);
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
//
//        try {
//            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//            getActivity().finish();
//            Log.i("Finished sending email...", "");
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(getActivity(),
//                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
//        }
//    }
}