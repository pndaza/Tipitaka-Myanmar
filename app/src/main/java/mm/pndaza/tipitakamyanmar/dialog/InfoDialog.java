package mm.pndaza.tipitakamyanmar.dialog;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

public class InfoDialog extends DialogFragment {

    public static InfoDialog newInstance() {
        return new InfoDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setBackgroundResource(R.drawable.dialog_background);

        WebView webView = view.findViewById(R.id.info);
        if (SharePref.getInstance(getContext()).getPrefNightModeState()) {
            webView.loadUrl("file:///android_asset/web/info-night.html");
        } else {
            webView.loadUrl("file:///android_asset/web/info.html");
        }

        ImageButton closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Optional: Customize dialog appearance
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set to 90% of screen width
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int width = (int) (displayMetrics.widthPixels * 0.95);
            int height = (int) (displayMetrics.heightPixels * 0.8);
            getDialog().getWindow().setLayout(
                    width,
                    height
            );
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}