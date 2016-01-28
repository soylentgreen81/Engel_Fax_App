package software.oi.engelfax.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import software.oi.engelfax.activity.SettingsActivity;

/**
 * Created by stefa_000 on 28.01.2016.
 */
public abstract class Preferences {
    public static String getNumber(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(SettingsActivity.PHONE_NO, "");
    }
    public static boolean saveNumber(Context context, String number){

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString(SettingsActivity.PHONE_NO, number);
            return edit.commit();
    }
}
