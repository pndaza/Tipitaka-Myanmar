package mm.pndaza.tipitakamyanmar.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import mm.pndaza.tipitakamyanmar.utils.FontCache;


public class UniTextView extends AppCompatTextView {
    public UniTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public UniTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public UniTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("NotoSansMyanmarUI-Regular.ttf", context);
        setTypeface(customFont);
    }
}
