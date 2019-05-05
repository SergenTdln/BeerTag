package application_projet4_groupe12.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Various methods for Hashing
 */
public abstract class Hash {

    public static String hash(String input){
        try {
            // Create a MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(("MD5"));
            digest.update((input.getBytes()));
            byte[] messageDigest = digest.digest();

            // The MD5 Hash is in array form so  it need to be convert into a String
            StringBuilder output = new StringBuilder();
            for(byte b : messageDigest) {
                output.append(Integer.toHexString(0xFF & b));
            }

            return output.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
