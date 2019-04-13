package application_projet4_groupe12.activities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TransferFromFirestore {
    private static SQLHelper db;
    private static FirebaseFirestore dab = FirebaseFirestore.getInstance();
    private static User user;
    public static void Transfer2() {
        try {
            db = new SQLHelper(getApplicationContext());
            Log.e("TAG", "Hello");
            dab.collection("Users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        public void onComplete(Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
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
                                    boolean admin = document.getBoolean("admin");
                                    Log.e("TAG", "admin: " + admin);
                                    long id = (new Double(document.getDouble("id"))).longValue();
                                    Log.e("TAG", "id : " + id);
                                    user = new User(id, username, password, created_on, firstName, lastName, birthday, image_path, admin);
                                    try {
                                        System.out.println("Fonctionne :" + db.addUser(user));
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
