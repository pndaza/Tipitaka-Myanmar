package mm.pndaza.tipitakamyanmar.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePref {

    private static final String PREF_FILENAME = "setting";
    private static final String PREF_FIRST_TIME = "FirstTime";
    private static final String PREF_DB_COPY = "DBCopy";
    private static final String PREF_DB_VERSION = "DBVersion";
    private static final String PREF_FONT_SIZE = "FontSize";
    private static final String PREF_FONT_STYLE = "FontStyle";
    private static final String PREF_NIGHT_MODE = "NightMode";

    private Context context;
    private static SharePref prefInstance;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;

    public SharePref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharePref getInstance(Context Context) {
        if (prefInstance == null) {
            prefInstance = new SharePref(Context);
        }
        return prefInstance;
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(PREF_FIRST_TIME, true);
    }

    public void noLongerFirstTime() {
        editor.putBoolean(PREF_FIRST_TIME, false);
    }

    public void setPrefFontStyle(String fontStyle) {
        editor.putString(PREF_FONT_STYLE, fontStyle);
        editor.apply();
    }

    public String getPrefFontStyle() {
        MDetect.init(context);
        String fontStyle = MDetect.isUnicode() ? "unicode" : "zawgyi";
        return sharedPreferences.getString(PREF_FONT_STYLE, fontStyle);
    }

    public void setPrefFontSize(int fontSize) {
        editor.putInt(PREF_FONT_SIZE, fontSize);
        editor.apply();
    }

    public int getPrefFontSize() {
        return sharedPreferences.getInt(PREF_FONT_SIZE, 17);
    }

    public void setPrefNightModeState(boolean state) {
        editor.putBoolean(PREF_NIGHT_MODE, state);
        editor.apply();
    }

    public boolean getPrefNightModeState() {
        return sharedPreferences.getBoolean(PREF_NIGHT_MODE, false);
    }

    public void setDbCopyState(boolean state) {
        editor.putBoolean(PREF_DB_COPY, state);
        editor.apply();
    }

    public boolean setDatabaseCopied() {
        return sharedPreferences.getBoolean(PREF_DB_COPY, true);
    }

    public void setDatabaseCopied(boolean value) {
        editor.putBoolean(PREF_DB_COPY, value);
        editor.apply();
    }

    public int getDatabaseVersion() {
        return sharedPreferences.getInt(PREF_DB_VERSION, 1);
    }

    public void setDatabaseVersion(int version) {
        editor.putInt(PREF_DB_VERSION, version);
    }

    public void saveDefault() {
        editor.putBoolean(PREF_DB_COPY, false);
        editor.putInt(PREF_FONT_SIZE, 17);
        editor.putBoolean(PREF_NIGHT_MODE, false);
        editor.apply();
    }

}
