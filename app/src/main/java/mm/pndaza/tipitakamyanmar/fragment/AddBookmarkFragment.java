package mm.pndaza.tipitakamyanmar.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

public class AddBookmarkFragment extends DialogFragment {
    View dlgView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getDialog().setCancelable(false);
        dlgView = inflater.inflate(R.layout.dlg_addbookmark, container, false);

        return dlgView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    private void initUI() {

/*        SharePref sharePref = SharePref.getInstance(this.getContext());
        String fontSize = sharePref.getPrefFontSize();
        boolean nightModeState = sharePref.getPrefNightModeState();*/
    }

}
