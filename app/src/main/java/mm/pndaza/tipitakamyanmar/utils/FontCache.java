package mm.pndaza.tipitakamyanmar.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;

public class FontCache {

    private static final HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "web/" + fontname);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontname, typeface);
        }

        return typeface;
    }

    public static Typeface getZawgyiTypeface(Context context) {
        String fontName = "NotoSansZawgyi-Regular.ttf";
        Typeface typeface = fontCache.get(fontName);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "web/" + fontName);

            } catch (Exception e) {
                Log.e("FontCache", "Error loading font: " + fontName, e);
                return null;
            }

            fontCache.put(fontName, typeface);
        }
        return typeface;
    }

    public static Typeface getUnicodeTypeface(Context context) {
        String fontName = "Pyidaungsu-2.5.3_Regular.ttf";
        Typeface typeface = fontCache.get(fontName);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "web/" + fontName);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontName, typeface);
        }
        return typeface;
    }

}
