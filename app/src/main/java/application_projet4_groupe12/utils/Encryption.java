package application_projet4_groupe12.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import application_projet4_groupe12.activities.ExpiredActivity;
import application_projet4_groupe12.activities.MainActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class Encryption {

    //todo utiliser le temps systeme pour crypter decrypter et faire la vérif du qr code, gps @Sergen
    private static String cryptoPass = "clé_de_cryptage_qr_code";

    public static String encryptQrCode(String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encryptedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            Log.d(Global.debug_text, "Encrypted: " + value + " -> " + encryptedValue);
            return encryptedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }


    public static String decryptQrCode(String value, Activity activity) {
//        String[] str = new Date[0];
        String decryptedValue = null;
        try {
            DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] encrypedPwdBytes = Base64.decode(value, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            decryptedValue = new String(decryptedValueBytes);

            String[] data = decryptedValue.split("_5%/");
            String createTimeStr = data[2];

            Long createTime = Long.valueOf(createTimeStr);
            Log.v(Global.debug_text, "create time str" + createTimeStr);
            Log.v(Global.debug_text, "create time int" + createTime);
            if (validity_expired(createTime)) {
                Log.v(Global.debug_text, "activity expired");
                ActivityUtils.getInstance().invokeActivity(activity, ExpiredActivity.class, true);


                //TODO SOLUTION TEMPORAIRE POUR PASSER L'INFO VERS L'ACTI
                SharedPreferences shared = getApplicationContext().getSharedPreferences("session", MODE_PRIVATE);
//                SharedPreferences.Editor editor = shared.edit();
//                editor.putBoolean("expired_qr", true); // Storing boolean - true/false
                shared.edit().putBoolean("expired_qr", true).apply(); // Storing boolean - true/false
//                getActivity().finish();

            }

//            Log.d(Global.debug_text, "Decrypted: " + value + " -> " + decryptedValue);
//            str = decryptedValue.split("_5%/");
//            int montant_achat = Integer.parseInt(str[1]);
//            Long partnerId = Long.valueOf(str[2]);
//            String createTime = str[3];

//            Log.v(Global.debug_text, "montant= " + montant_achat + " partnerId= " + partnerId + " createTime= " + createTime);


            //todo: faire le regex pour récupérer l'id , le montant, la date
//            return decryptedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedValue;
    }

    /*
    Checks the validity of a qr code according to its generation datetime
    If the qrcode has not expired (has to be used within 2 minutes) then its considered as valid
     */
    private static Boolean validity_expired(long qrtime) {
        Boolean expired = false;
        Long now_time = GetUnixTime();

        Log.v(Global.debug_text, "qr time" + qrtime);
        Log.v(Global.debug_text, "now  time" + now_time);

        Long diff = now_time - qrtime;
        Log.v(Global.debug_text, "time diff " + diff);
        if (diff >= 180000) {
            expired = true;
        }

        return expired;
    }

    /*
    returns the UTC time in millisecs
     */
    public static Long GetUnixTime() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
//        int utc = (int)(now / 1000);
        return now;

    }

    public static String GetUnixTimeStr() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
//        int utc = (int)(now / 1000);
        String time = String.valueOf(now);
        return time;

    }
}
