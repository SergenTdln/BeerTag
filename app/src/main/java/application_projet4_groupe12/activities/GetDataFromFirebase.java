package application_projet4_groupe12.activities;

import com.firebase.client.Query;
import com.google.firebase.FirebaseError;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.protobuf.Any;
import java.util.Map;
import java.util.HashMap;
import com.google.firebase.firestore.FirebaseFirestore;

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;

import android.app.Fragment;
import android.support.annotation.NonNull;
import application_projet4_groupe12.R;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;
import static android.content.ContentValues.TAG;
import static java.lang.Integer.getInteger;
import static java.lang.System.err;
import android.util.Log;
import application_projet4_groupe12.exceptions.WrongDateFormatException;

public class GetDataFromFirebase extends Fragment {
    private static HashMap<String, User> users = new HashMap<>();
    private static FirebaseFirestore dab = FirebaseFirestore.getInstance();
    private static SQLHelper db;

            public static void Transfer2() {
                Log.e("TAG", "Hello");
                dab.collection("Users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            public void onComplete( Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        double id = document.getDouble("id");
                                        String username = document.getString("username");
                                        Log.e("TAG", "username: " + username);
                                        String password = document.getString("passwordHashed");
                                        Log.e("TAG", "password: " + password);
                                        String created_on = document.getString("creationDate");
                                        Log.e("TAG", "created_on: " + created_on);
                                        String firstName = document.getString("firstName");
                                        Log.e("TAG", "firstName: " + firstName);
                                        String lastName = document.getString("lastName");
                                        Log.e("TAG", "lastName: " + lastName);
                                        String birthday = document.getString("birthday");
                                        Log.e("TAG", "birthday: " + birthday);
                                        String image_path = document.getString("imagePath");
                                        Log.e("TAG", "image_path: " + image_path);
                                        Boolean admin = document.getBoolean("admin");
                                        Log.e("TAG", "admin: " + admin);
                                        User user = new User((int)id, username, password, created_on, firstName, lastName, birthday, image_path, admin);
                                        try {
                                            db.createUser(user);
                                        } catch (WrongEmailFormatException e) {
                                            e.printStackTrace();
                                        } catch (WrongDateFormatException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Log.e("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }

            public void onCancelled(FirebaseError firebaseError) {

            }
}
