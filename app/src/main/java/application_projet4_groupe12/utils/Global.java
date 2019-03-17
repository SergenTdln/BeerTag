package application_projet4_groupe12.utils;

public class Global {
    public static int ivar1, ivar2;
    public static String svar1, svar2;
    public static int[] myarray1 = new int[10];

    public static boolean fb_logged;

    public static String debug_text = "beer tag";

    public String session_username;
    public String session_email;
    public String session_facebook_id;

    public void setSession_username(String username){
        this.session_username = username;
    }

    public void setSession_facebook_id(String facebook_user_id){
        this.session_facebook_id = facebook_user_id;
    }

    public void setSession_emai(String email){
        this.session_email = email;
    }
}