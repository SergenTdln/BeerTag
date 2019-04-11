package application_projet4_groupe12.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.UnknownUserException;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.utils.Global;
import application_projet4_groupe12.utils.Pair;

import static android.content.ContentValues.TAG;

public class Fragment3 extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button fragment3_sign_up;
    private SQLHelper db;
    private Partner partner;
    private FirebaseFirestore dab = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private EditText name;
    private EditText address;
    private Spinner dropDownUsers;

    String selectedUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3_layout, container, false);
        fragment3_sign_up = view.findViewById(R.id.sign_up_partner_button_out);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        dropDownUsers = view.findViewById(R.id.sign_up_partner_input_spinner_admin);

        try {
            db = new SQLHelper(getContext());

            List<String> allUsernamesList = db.getAllUsernames();
            allUsernamesList.add(0, "Please select an User");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.fragment3_spinner_adapter, allUsernamesList);
            dropDownUsers.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "IOException : could not retrieve existing users. Try refreshing the view", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }

        dropDownUsers.setOnItemSelectedListener(this);

        fragment3_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = getView().findViewById(R.id.sign_up_partner_input_name);
                address = getView().findViewById(R.id.sign_up_partner_input_address);

                signUp();
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String mSelectedUsername = (String) parent.getItemAtPosition(position);
        if(position==0) {
            //Default item is selected : do nothing
        } else if(User.isAdmin(getContext(), mSelectedUsername)){
            //An User cannot be admin of two Partners at the same time
            Toast.makeText(getContext(), "This User is already an admin for another Partner. Please select another User account", Toast.LENGTH_SHORT).show();
            //Do nothing
            dropDownUsers.setSelection(0);
        } else {
            selectedUsername = mSelectedUsername;
            Toast.makeText(getContext(), "You selected User \""+mSelectedUsername+"\" as an Admin for this Partner", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    public void Transfer(){
        dab.collection("Partner")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                partner = document.toObject(Partner.class);
                                try {
                                    db.addPartner(partner);
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
                                Partner partner = document.toObject(Partner.class);
                                int id = Integer.parseInt(document.getString("id"));
                                String creationDate = document.getString("created_on");
                                String address = document.getString("id_address");
                                String name = document.getString("name");
                                String imagePath = document.getString("image_path");
                                partner = new Partner(id, name, address, creationDate, imagePath);
                                try {
                                    db.addPartner(partner);
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
        String mName = name.getText().toString();
        String mAddress = address.getText().toString();
        if( mName.equals("") || mAddress.equals("") )
        {
            Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            db = new SQLHelper(getContext());

            /**
             * Deleted those lines because two Partners could have the same name.
             * //TODO Maybe check if a Partner with ALL same fields exist ? THEN stop creating a duplicate
             if (db.doesPartnerExist(name))  {
             Toast.makeText(getActivity(),  "This email already exists", Toast.LENGTH_SHORT).show();
             }
             else {
             **/
            long id = db.getFreeIDPartner();

            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);

            partner = new Partner(id, mName, mAddress, today, ""); //ImagePath will be edited later by partner in Settings Activity
            try {
                System.out.println("Partner inséré : " + db.addPartner(partner));
            } catch (WrongDateFormatException e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "Invalid date format : please use DD/MM/YYYY", Toast.LENGTH_SHORT).show();
                return;
            }

            //Now we need to add the admin User for this Partner
            try {
                db.addAdmin(selectedUsername, partner.getId());
            } catch (UnknownUserException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Invalid User : \""+selectedUsername+"\"", Toast.LENGTH_SHORT).show();
                return;
            }

            //Firebase stuff
            dab.collection("Partner").add(partner);
            Toast.makeText(getActivity(), "Partner created", Toast.LENGTH_SHORT).show();
            //TODO add the first admin to Firebase as well
            Log.d(Global.debug_text, "Firebase instance: " + mAuth);
            /**
             * Useless for Partner creation IMO
             *
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
             **/


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}