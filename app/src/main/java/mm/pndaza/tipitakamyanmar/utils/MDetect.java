package mm.pndaza.tipitakamyanmar.utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

public class MDetect {

    private static MDetect instance;
    private boolean isUnicode;

    // Private constructor to prevent direct instantiation
    private MDetect() {}

    // Initialize with context (call this once in your Application class)
    public static void init(Context context) {
        if (instance == null) {
            synchronized (MDetect.class) { // Thread-safe initialization
            instance = new MDetect();
            instance.initialize(context.getApplicationContext());
            }
        }
    }

    // Get the singleton instance
    public static MDetect getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MDetect not initialized. Call MDetect.init(context) first.");
        }
        return instance;
    }

    private void initialize(Context context) {
        TextView textView = new TextView(context, null);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        textView.setText("က");
        textView.measure(0, 0);
        int length1 = textView.getMeasuredWidth();

        textView.setText("က္က");
        textView.measure(0, 0);
        int length2 = textView.getMeasuredWidth();

        isUnicode = (length1 == length2);
    }

    public boolean isUnicode() {
        return isUnicode;
    }

    public String getDeviceEncodedText(String uniText) {
        return isUnicode() ? uniText : Rabbit.uni2zg(uniText);
    }
}