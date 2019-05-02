package application_projet4_groupe12.fragment;

import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.UnknownUserException;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.utils.AppUtils;
import application_projet4_groupe12.utils.Global;

import static android.content.Context.MODE_PRIVATE;

public class Fragment3 extends Fragment implements AdapterView.OnItemSelectedListener {

    private SQLHelper db;
    private Partner partner;
    private FirebaseFirestore dab = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private String document_id;

    private EditText name;
    private EditText address;
    private EditText tva;
    private Spinner dropDownUsers;
    private Button fragment3_sign_up;

    String selectedUsername;

    static boolean successPush1;
    static boolean successPush2;

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
            allUsernamesList.add(0, getString(R.string.settings_partner_spinner_default));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_adapter_plain_text, allUsernamesList);
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
                tva = getView().findViewById(R.id.sign_up_partner_input_tva);

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

    private boolean signUp() {
        String mName = name.getText().toString();
        String mAddress = address.getText().toString();
        String mTVA = tva.getText().toString();
        if( mName.equals("") ||
                mAddress.equals("") ||
                mTVA.equals("") ||
                selectedUsername.equals(getString(R.string.settings_partner_spinner_default)) )
        {
            Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            db = new SQLHelper(getContext());

            if (db.doesPartnerExist(mTVA))  {
                Toast.makeText(getActivity(),  "This TVA number is already used", Toast.LENGTH_SHORT).show();
                return false;
            }

            long partnerID = db.getFreeIDPartner();
            User user = db.getUser(selectedUsername);
            User.connectUser(getContext(), user);

            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);
            partner = new Partner(partnerID, mTVA, mName, mAddress, today, ""); //ImagePath will be edited later by partner in Settings Activity

            /*Adding the partner to the DB (local)*/
            try {
                boolean success = db.addPartner(partner);
                System.out.println("Partner inséré : " + success);
            } catch (WrongDateFormatException e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "Invalid date format : please use DD/MM/YYYY", Toast.LENGTH_SHORT).show();
                return false;
            }
            /*Adding the Admin_User entry to the DB (local)*/
            try {
                boolean success = db.addAdmin(user.getId(), partner.getId());
                System.out.println("Admin_User inséré : " + success);
            } catch (UnknownUserException e) {
                Log.v(Global.debug_text,"addAdmin "+e);
                e.printStackTrace();
                Toast.makeText(getActivity(), "Invalid User : \""+selectedUsername+"\"", Toast.LENGTH_SHORT).show();
                return false;
            }

            /*Firebase login*/
            boolean isLoggedIn = (mAuth.getCurrentUser() != null);
            if (!isLoggedIn) {
                mAuth.signInWithEmailAndPassword(User.connectedUser.getUsername(), User.connectedUser.getPasswordHashed())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getContext(), "Firebase login : success ? " + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                if(pushDataToFirebase(user, partnerID)) {
                                    System.out.println("Successful push to Firestore.");
                                    /*Everything succeeded : Move to main menu*/

                                    // Logging in
                                    SharedPreferences session = getActivity().getSharedPreferences("session", MODE_PRIVATE);
                                    session.edit().putBoolean("choice made", true).apply();
                                    session.edit().putBoolean("loggin_chosed", true).apply();
                                    session.edit().putBoolean("is admin", true).apply();

                                    AppUtils.end_home_admin(getActivity());
                                } else {
                                    System.out.println("Unsuccessful push to Firestore.");
                                }
                            }
                        });
            }


        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "error fragment 3"+e);
        } finally {
            db.close();
        }
        return true;
    }

    private boolean pushDataToFirebase(User user, long partnerID){
        //successPush1 = false; //Reset
        //successPush2 = false; //Reset

        /*Adding the partner to the DB (Firestore)*/
        dab.collection("Partner")
                .document(String.valueOf(partner.getId()))
                .set(partner, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Succesfully added a Partner entry to Firestore");
                        successPush1 = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while adding a Partner entry to Firestore. Here is the stacktrace :");
                        e.printStackTrace();
                        successPush1 = false;
                    }
                });

        /*Adding the Admin_User entry to the DB (Firestore)*/
        Map<String, Long> data = new HashMap<>();
        data.put("id_user", user.getId());
        data.put("id_partner", partnerID);
        dab.collection("Admin_user").
                document((user.getId())+String.valueOf(partnerID)). //Document ID = concat of both ids (assures its unique + very easy to retrieve later on)
                set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Successfully added a Admin_user entry to Firestore");
                        successPush2 = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while adding a Admin_user entry to Firestore. Here is the stacktrace :");
                        e.printStackTrace();
                        successPush2 = false;
                    }
                });
        return successPush1 && successPush2;
    }
}