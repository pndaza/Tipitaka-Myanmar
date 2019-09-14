package mm.pndaza.tipitakamyanmar.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

public class SettingDialogFragment extends DialogFragment {

    private static final String FONTSIZESMALL = "small";
    private static final String FONTSIZENORMAL = "normal";
    private static final String FONTSIZELARGE = "large";

    View dlgView;
    SharePref sharePref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getDialog().setCancelable(true);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dlgView = inflater.inflate(R.layout.dlg_setting, container, false);

        return dlgView;
    }

    @Override
    public void onResume() {
        super.onResume();

/*        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float xdpi = metrics.xdpi;
        int dialogWidth = (int)(xdpi * 2.2);
        params.width = dialogWidth;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);*/

    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup radioGroupFontSize = dlgView.findViewById(R.id.rg_fontsize);
        RadioGroup radioGroupTheme = dlgView.findViewById(R.id.rg_theme);

        setViewTextEncoding();
        initCheckState(radioGroupFontSize, radioGroupTheme);

        radioGroupFontSize.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            saveAndRestart(checkedId);
        });

        radioGroupTheme.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            saveAndRestart(checkedId);
        });

        ImageButton btnDone = dlgView.findViewById(R.id.btn_close);
        btnDone.setOnClickListener(view1 -> dismiss());

    }

    private void initCheckState(RadioGroup radioGroupFontSize, RadioGroup radioGroupTheme){
        sharePref = SharePref.getInstance(this.getContext());
        String fontSize = sharePref.getPrefFontSize();
        boolean nightModeState = sharePref.getPrefNightModeState();
        //
        if (fontSize.equals(FONTSIZESMALL))
            radioGroupFontSize.check(R.id.radio_font_small);
        else if (fontSize.equals(FONTSIZENORMAL))
            radioGroupFontSize.check(R.id.radio_font_normal);
        else if (fontSize.equals(FONTSIZELARGE))
            radioGroupFontSize.check(R.id.radio_font_large);
        //
        if(nightModeState){
            radioGroupTheme.check(R.id.radio_theme_night);
        } else {
            radioGroupTheme.check((R.id.radio_theme_day));
        }

    }

    private void saveAndRestart(int checkedId){
        boolean nightModeState = sharePref.getPrefNightModeState();
        switch (checkedId){
            case R.id.radio_font_small:
                sharePref.setPrefFontSize(FONTSIZESMALL);
                break;
            case R.id.radio_font_normal:
                sharePref.setPrefFontSize(FONTSIZENORMAL);
                break;
            case R.id.radio_font_large:
                sharePref.setPrefFontSize(FONTSIZELARGE);
                break;
            case R.id.radio_theme_day:
                sharePref.setPrefNightModeState(false);
                nightModeState = false;
                break;
            case R.id.radio_theme_night:
                sharePref.setPrefNightModeState(true);
                nightModeState = true;
                break;
        }
        setTheme(nightModeState);
        getActivity().recreate();
    }

    private void setTheme(boolean nightState){
        if (nightState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setViewTextEncoding(){
        TextView tvှSettingTitle = dlgView.findViewById(R.id.tv_settingTitle);
        TextView tvFontSizeTitle = dlgView.findViewById(R.id.tv_fontSizeTitle);
        RadioButton rbFontSizeSmall = dlgView.findViewById(R.id.radio_font_small);
        RadioButton rbFontSizeNormal = dlgView.findViewById(R.id.radio_font_normal);
        RadioButton rbFontSizeLarge = dlgView.findViewById(R.id.radio_font_large);
        TextView tvThemeTitle = dlgView.findViewById(R.id.tv_themeTitle);
        RadioButton rbThemeDay = dlgView.findViewById(R.id.radio_theme_day);
        RadioButton rbThemeNight = dlgView.findViewById(R.id.radio_theme_night);

        String settingTitle = getString(R.string.settingTitle);
        String fontSizeTitle = getString(R.string.fontSize);
        String fontSizeSmall = getString(R.string.fontSizeSmall);
        String fontSizeNormal = getString(R.string.fontSizeNormal);
        String fontSizeLarge = getString(R.string.fontSizeLarge);
        String themeTitle = getString(R.string.theme);
        String themeDay = getString(R.string.themeDay);
        String themeNight = getString(R.string.themeNight);

        if(!MDetect.isUnicode()){
            tvှSettingTitle.setText(Rabbit.uni2zg(settingTitle));
            tvFontSizeTitle.setText(Rabbit.uni2zg(fontSizeTitle));
            rbFontSizeSmall.setText(Rabbit.uni2zg(fontSizeSmall));
            rbFontSizeNormal.setText(Rabbit.uni2zg(fontSizeNormal));
            rbFontSizeLarge.setText(Rabbit.uni2zg(fontSizeLarge));
            tvThemeTitle.setText(Rabbit.uni2zg(themeTitle));
            rbThemeDay.setText(Rabbit.uni2zg(themeDay));
            rbThemeNight.setText(Rabbit.uni2zg(themeNight));
        }
    }
}
