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

import application_projet4_groupe12.activities.browse_points.UsePromotionsRowAdapter;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;

public class FirebaseUtils {
    private static boolean resultRemoval;
    private static boolean resultInsertion;
    private static boolean resultUsePromotion;

    public static void transferFromFirebase(Context c){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);

            //TODO empty local db to take remote changes into account -> unnecessary if conditional import is implemented

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

    public static boolean firestoreRemoveAdmin(long partnerID, long userID){
        resultRemoval = false; // Reset

        FirebaseFirestore dab = FirebaseFirestore.getInstance();
        DocumentReference doc = dab.collection("Admin_User").
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
                        resultRemoval = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        resultRemoval = false;
                    }
                });
        return resultRemoval;
    }

    public static boolean firestoreAddAdmin(long partnerID, long userID){
        resultInsertion = false; //Reset

        FirebaseFirestore dab = FirebaseFirestore.getInstance();

        Map<String, Long> data = new HashMap<>();
        data.put("id_user", userID);
        data.put("id_partner", partnerID);
        dab.collection("Admin_User").document((userID)+String.valueOf(partnerID))
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        resultInsertion = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        resultInsertion = false;
                    }
                });
        return resultInsertion;
    }

    public static boolean firestoreUsePromotion(User user, UsePromotionsRowAdapter.ViewHolder vh, String date){
        FirebaseFirestore dab = FirebaseFirestore.getInstance();
        if(! vh.reusable) {
            /*Delete the promotion*/
            resultUsePromotion = false; //Reset
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
            doc.update(data);
            /*Deleting the document*/
            doc.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            resultUsePromotion = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            resultUsePromotion = false;
                        }
                    });
            if (!resultUsePromotion) {
                return false;
            }
        }

        /*Update the User's points*/
        int pointsCost = Integer.parseInt(vh.pointsRequired.getText().toString());
        DocumentReference docUserPoints = dab.collection("User_points").document((user.getId())+String.valueOf(vh.shopID));
        docUserPoints.update("points", FieldValue.increment((-1)*pointsCost))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        resultUsePromotion = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                resultUsePromotion = false; // TODO Problem : what if the first succeeded and not the second ?
            }
        });
        if(!resultUsePromotion){
            return false;
        }

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
                        resultUsePromotion = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        resultUsePromotion = false; // TODO Problem : what if the first succeeded and not the second ?
                    }
                });

        return resultUsePromotion;
    }
}
