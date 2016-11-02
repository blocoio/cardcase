package io.bloco.cardcase.presentation;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static String APP_KEY = "myAppPrefs";
    public static String THEME_KEY = "CURRENT_THEME_KEY";

    public static void setStringForKeyInContext(String str, String key, Context ctx) {
        SharedPreferences mPrefs = ctx.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, str);
        editor.commit();
    }

    public static String getStringForKeyInContext(String key, Context ctx) {
        SharedPreferences mPrefs = ctx.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        return mPrefs.getString(key, null);
    }
}
