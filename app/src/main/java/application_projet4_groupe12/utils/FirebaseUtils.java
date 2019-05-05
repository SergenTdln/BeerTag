package application_projet4_groupe12.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import application_projet4_groupe12.activities.browse_points.UsePromotionsRowAdapter;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;

public class FirebaseUtils {

    private static boolean resultRemoval;
    private static boolean resultInsertion;
    private static boolean resultUsePromotion;

    private static volatile boolean doneAddAdmin;
    private static volatile boolean doneRemoveAdmin;
    private static volatile boolean doneUsePromotion;

    public static void transferFromFirebase(Context c){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);

            //Empty local db to take remote changes into account
            db.emptyAll();

            if(db.TransferUser()) {
                System.out.println("Transferred Users successfully");
            }
            if(db.TransferAddress()) {
                System.out.println("Transferred Address successfully");
            }
            if(db.TransferAdmin_user()) {
                System.out.println("Transferred Admin successfully");
            }
            /*
            if(db.TransferFavorite_shops()) {
                System.out.println("Transferred Favorite_shops successfully");
            }
            Removed feature */
            if(db.TransferPromotion()) {
                System.out.println("Transferred Promotion successfully");
            }
            /*
            if(db.TransferShop_frames()) {
                System.out.println("Transferred Shop_frames successfully");
            }
            Removed feature */
            if(db.TransferShop_location()) {
                System.out.println("Transferred Shop_locations successfully");
            }
            if(db.TransferUser_points()) {
                System.out.println("Transferred User_points successfully");
            }
            if(db.TransferUser_promotion()) {
                System.out.println("Transferred User_promotions successfully");
            }
            if(db.TransferPartner()){
                System.out.println("Transferred Partners successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(Global.debug_text, "printStackTrace" + e);
        }
        finally {
            if(db!=null) {
                db.close();
            }
        }
    }

    public static void firestoreRemoveAdmin(long partnerID, long userID){

        FirebaseFirestore dab = FirebaseFirestore.getInstance();
        DocumentReference doc = dab.collection("Admin_user").
                document((userID)+String.valueOf(partnerID));

        /*Deleting fields*/
        Map<String, Object> newData = new HashMap<>();
        newData.put("id_user", FieldValue.delete());
        newData.put("id_partner", FieldValue.delete());
        doc.update(newData);

        /*Deleting the document*/
        doc.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Admin Removal : Successful push to Firestore.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Admin Removal : Unsuccessful push to Firestore. Stacktrace :");
                        e.printStackTrace();
                    }
                });
    }

    public static void firestoreAddAdmin(long partnerID, long userID){

        final CountDownLatch latch = new CountDownLatch(1); //used for threads syncing

        FirebaseFirestore dab = FirebaseFirestore.getInstance();

        Map<String, Long> data = new HashMap<>();
        data.put("id_user", userID);
        data.put("id_partner", partnerID);
        dab.collection("Admin_user").document((userID)+String.valueOf(partnerID))
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Admin Addition : Successful push to Firestore.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Admin Addition : Unsuccessful push to Firestore. Stacktrace :");
                        e.printStackTrace();
                    }
                });
    }

    public static void firestoreUsePromotion(User user, UsePromotionsRowAdapter.ViewHolder vh, String date){

        FirebaseFirestore dab = FirebaseFirestore.getInstance();
        if(! vh.reusable) {
            /*Delete the promotion*/

            //resultUsePromotion = false; //Reset
            DocumentReference doc = dab.collection("Promotion").document(String.valueOf(vh.promoID));
            /*Deleting fields*/
            Map<String, Object> data = new HashMap<>();
            data.put("active", FieldValue.delete());
            data.put("description", FieldValue.delete());
            data.put("endDate", FieldValue.delete());
            data.put("id", FieldValue.delete());
            data.put("idPartner", FieldValue.delete());
            data.put("idShop", FieldValue.delete());
            data.put("imagePath", FieldValue.delete());
            data.put("pointsRequired", FieldValue.delete());
            data.put("reusable", FieldValue.delete());
            doc.update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Promotion Usage : Successful push to Firestore : (1/4 - Deleting the fields)");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Promotion Usage : Unsuccessful push to Firestore : (1/4 - Deleting the fields). Stacktrace :");
                            e.printStackTrace();
                        }
                    });
            /*Deleting the document*/
            doc.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Promotion Usage : Successful push to Firestore : (2/4 - Deleting the document)");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Promotion Usage : Unsuccessful push to Firestore : (2/4 - Deleting the document). Stacktrace :");
                            e.printStackTrace();
                        }
                    });
        }

        /*Update the User's points*/
        int pointsCost = Integer.parseInt(vh.pointsRequired.getText().toString());
        DocumentReference docUserPoints = dab.collection("User_points").document((user.getId())+String.valueOf(vh.shopID));
        docUserPoints
                .update("points", FieldValue.increment((-1)*pointsCost))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Promotion Usage : Successful push to Firestore : (3/4 - Updating the User's points)");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Promotion Usage : Unsuccessful push to Firestore : (3/4 - Updating the user's points). Stacktrace :");
                        e.printStackTrace();
                    }
                });

        /*Update the User_promotion table*/
        Map<String, Object> newData = new HashMap<>();
        newData.put("id_user", user.getId());
        newData.put("id_promotion", vh.promoID);
        newData.put("used_on", date);
        dab.collection("User_promotion").document((user.getId())+String.valueOf(vh.promoID))
                .set(newData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Promotion Usage : Successful push to Firestore : (4/4 - Updating the User_promotion table)");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Promotion Usage : Unsuccessful push to Firestore : (4/4 - Updating the User_promotion table). Stacktrace :");
                        e.printStackTrace();
                    }
                });
    }
}
