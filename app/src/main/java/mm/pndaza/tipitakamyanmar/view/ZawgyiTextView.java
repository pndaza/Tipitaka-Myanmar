package mm.pndaza.tipitakamyanmar.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import mm.pndaza.tipitakamyanmar.utils.FontCache;


public class ZawgyiTextView extends AppCompatTextView {
    public ZawgyiTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public ZawgyiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public ZawgyiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("NotoSansZawgyi-Regular.ttf", context);
        setTypeface(customFont);
    }
}
