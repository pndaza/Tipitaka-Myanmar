package mm.pndaza.tipitakamyanmar.activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SettingActivity extends AppCompatActivity {

    private SharePref sharePref;

    private RadioGroup radioGroupTheme;
    private RadioGroup radioGroupFontStyle;
    private CheckBox checkBoxFontFix;
    private DiscreteSeekBar seekBar;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        MDetect.init(this);
        if (!MDetect.isUnicode()) {
            changeDisplayText();
        }
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(MDetect.getDeviceEncodedText(getString(R.string.settingTitle)));
        }


        radioGroupTheme = findViewById(R.id.rg_theme);
        radioGroupFontStyle = findViewById(R.id.rg_font_style);
        webView = findViewById(R.id.webView);
        seekBar = findViewById(R.id.fontsize_seekbar);
        seekBar.setMax(35);
        seekBar.setMin(5);

        loadSavedSettings();

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                final WebSettings webSettings = webView.getSettings();
                webSettings.setDefaultFontSize(value);
                sharePref.setPrefFontSize(value);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        radioGroupTheme.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            saveAndApplyTheme(checkedId);
        });

        radioGroupFontStyle.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            saveFontStyle(checkedId);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loadSavedSettings() {

        sharePref = SharePref.getInstance(this);
        int fontSize = sharePref.getPrefFontSize();
        seekBar.setProgress(fontSize);

        boolean nightModeState = sharePref.getPrefNightModeState();
        if (nightModeState) {
            radioGroupTheme.check(R.id.radio_theme_night);
        } else {
            radioGroupTheme.check((R.id.radio_theme_day));
        }

        String fontStyle = sharePref.getPrefFontStyle();
        if(fontStyle.equals("unicode")){
            radioGroupFontStyle.check(R.id.radio_font_unicode);
        } else {
            radioGroupFontStyle.check(R.id.radio_font_zawgyi);
        }

        StringBuilder sample = new StringBuilder();
        // add css style
        String cssFile = nightModeState ? "style_sample_night.css" : "style_sample.css";
        sample.append("<link rel=\"stylesheet\" href=\"");
        sample.append(cssFile);
        sample.append("\"s>");
        sample.append("<p class=\"sample\">နမူနာစာသား</p>");
        webView.loadDataWithBaseURL("file:///android_asset/web/",
                Rabbit.uni2zg(sample.toString()), "text/html", "UTF-8", null);
        final WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(fontSize);

    }

    private void saveAndApplyTheme(int checkedId) {
        boolean nightModeState = sharePref.getPrefNightModeState();
        switch (checkedId) {
            case R.id.radio_theme_day:
                sharePref.setPrefNightModeState(false);
                nightModeState = false;
                break;
            case R.id.radio_theme_night:
                sharePref.setPrefNightModeState(true);
                nightModeState = true;
                break;
            case R.id.radio_font_zawgyi:
                sharePref.setPrefFontStyle("zawgyi");
                break;
            case R.id.radio_font_unicode:
                sharePref.setPrefFontStyle("unicode");
                break;
        }
        setTheme(nightModeState);
    }

    private void saveFontStyle(int checkedId){
        switch (checkedId) {
            case R.id.radio_font_zawgyi:
                sharePref.setPrefFontStyle("zawgyi");
                break;
            case R.id.radio_font_unicode:
                sharePref.setPrefFontStyle("unicode");
                Log.d(TAG, "saveAndApplyTheme: unicode");
                break;
        }
    }

    private void setTheme(boolean nightState) {
        if (nightState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void changeDisplayText() {
        // change display text for zawgyi device
        TextView tvThemeTitle = findViewById(R.id.tv_themeTitle);
        RadioButton rbThemeDay = findViewById(R.id.radio_theme_day);
        RadioButton rbThemeNight = findViewById(R.id.radio_theme_night);
        TextView tvFontStyleTitle = findViewById(R.id.tv_font_style_title);
        RadioButton rbZawgyi = findViewById(R.id.radio_font_zawgyi);
        RadioButton rbUnicode = findViewById(R.id.radio_font_unicode);
        TextView tvFontSizeTitle = findViewById(R.id.tv_fontSizeTitle);
        TextView tvFontSizeSmall = findViewById(R.id.tv_fontSizeSmall);
        TextView tvFontSizeNormal = findViewById(R.id.tv_fontSizeNormal);
        TextView tvFontSizeLarge= findViewById(R.id.tv_fontSizeLarge);

        tvThemeTitle.setText(Rabbit.uni2zg(getString(R.string.themeTitle)));
        rbThemeDay.setText(Rabbit.uni2zg(getString(R.string.themeDay)));
        rbThemeNight.setText(Rabbit.uni2zg(getString(R.string.themeNight)));
        tvFontStyleTitle.setText(Rabbit.uni2zg(getString(R.string.fontStyleTitle)));
        rbZawgyi.setText(Rabbit.uni2zg(getString(R.string.zawgyi)));
        rbUnicode.setText(Rabbit.uni2zg(getString(R.string.unicode)));
        tvFontSizeTitle.setText(Rabbit.uni2zg(getString(R.string.fontSize)));
        tvFontSizeSmall.setText(Rabbit.uni2zg(getString(R.string.fontSizeSmall)));
        tvFontSizeNormal.setText(Rabbit.uni2zg(getString(R.string.fontSizeNormal)));
        tvFontSizeLarge.setText(Rabbit.uni2zg(getString(R.string.fontSizeLarge)));
    }
}
