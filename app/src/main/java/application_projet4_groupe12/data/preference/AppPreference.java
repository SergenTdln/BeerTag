package application_projet4_groupe12.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Arrays;


public class AppPreference {

    // singleton
    private static AppPreference appPreference = null;

    // common
    private SharedPreferences sharedPreferences;
    //private SharedPreferences settingsPreferences; // never accessed ?
    private SharedPreferences.Editor editor;

    public static AppPreference getInstance(Context context) {
        if(appPreference == null) {
            appPreference = new AppPreference(context);
        }
        return appPreference;
    }
    private AppPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PrefKey.APP_PREF_NAME, Context.MODE_PRIVATE);
        //settingsPreferences = PreferenceManager.getDefaultSharedPreferences(context); // never accessed ?
        editor = sharedPreferences.edit();
        editor.apply(); // Just to make the compiler happy
    }

    private void setString(String key, String value) {
        editor.putString(key , value);
        editor.commit();
    }
    private String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }
    public Boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setInteger(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInteger(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void setStringArray(String key, ArrayList<String> values) {
        if (values != null && !values.isEmpty()) {
            StringBuilder value = new StringBuilder();
            for (String str : values) {
                value.append(",").append(str);
            }
            setString(key, value.toString());
        }
    }

    public ArrayList<String> getStringArray(String key) {
        ArrayList<String> arrayList = new ArrayList<>();
        String value = getString(key);
        if (value != null) {
            arrayList = new ArrayList<>(Arrays.asList(value.split(",")));
        }
        return arrayList;
    }


}
