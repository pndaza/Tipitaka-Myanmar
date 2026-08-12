package mm.pndaza.tipitakamyanmar.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public class Theme {
    public static void setTheme(Context context) {
        SharePref sharePref = SharePref.getInstance(context);
        if (sharePref.getPrefNightModeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
