package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatDelegate;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class GotoDialogFragment extends DialogFragment {

    private static int firstPage;
    private static int lastPage;

    private static final int DAYPOSITIVECOLOR = Color.rgb(233, 30, 99);
    private static final int NIGHTPOSITIVECOLOR = Color.rgb(255, 88, 35);
    private static final int NEGATIVECOLOR = Color.rgb(128, 128, 128);

    private GotoDialogListener listener;

    public interface GotoDialogListener {

        void onSubmitGotoDialog(int input);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dlg_goto, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof GotoDialogListener) {
            listener = (GotoDialogListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement GotoDialogFragment.GotoDialogListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();

        if (args != null) {
            firstPage = args.getInt("firstPage");
            lastPage = args.getInt("lastPage");
        }

        final TextView tvTitle = view.findViewById(R.id.tv_title);
        final EditText editText = view.findViewById(R.id.goto_num);
        final Button btn_go = view.findViewById(R.id.btn_go);
        final Button btn_cancel = view.findViewById(R.id.btn_cancel);

        if (!MDetect.isUnicode()) {
            tvTitle.setText(Rabbit.uni2zg(tvTitle.getText().toString()));
            btn_go.setText(Rabbit.uni2zg(tvTitle.getText().toString()));
            btn_cancel.setText(Rabbit.uni2zg(tvTitle.getText().toString()));
        }
        // set hint for editText
        editText.setHint(String.format("(%d-%d) page", firstPage, lastPage));
        // show soft keyboard
        editText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        // check input is valid and enable Go button
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                boolean isValidInput = false;
                int buttonTextColor = NEGATIVECOLOR;

                String inputStr = charSequence.toString();
//                Log.d("onTextChanged", "input is - '"+ inputStr + "' and count is " + inputStr.length());

                if (inputStr.length() == 0 || inputStr.length() > 4) {
                    isValidInput = false;
                    buttonTextColor = NEGATIVECOLOR;
                } else {
                    int input = Integer.parseInt(inputStr);

                    if (input >= firstPage && input <= lastPage) {
                        isValidInput = true;
                        int nightStatus = AppCompatDelegate.getDefaultNightMode();
                        if (nightStatus == AppCompatDelegate.MODE_NIGHT_YES)
                            buttonTextColor = NIGHTPOSITIVECOLOR;
                        else
                            buttonTextColor = DAYPOSITIVECOLOR;
                    }
                }
                btn_go.setEnabled(isValidInput);
                btn_go.setTextColor(buttonTextColor);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_cancel.setOnClickListener(view1 -> dismiss());

        btn_go.setOnClickListener(view12 -> {
            int input = Integer.valueOf(editText.getText().toString().trim());
            listener.onSubmitGotoDialog(input);
            dismiss();
        });
    }
}
