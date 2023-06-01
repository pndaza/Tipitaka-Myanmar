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
import android.widget.RadioGroup;
import android.widget.TextView;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class GotoDialogFragment extends DialogFragment {

    private static int firstPage;
    private static int lastPage;
    private static int firstParagraph;
    private static int lastParagraph;
    private static final int PAGE = 0;
    private static final int PARAGRAPH = 1;

    private static final int DAYPOSITIVECOLOR = Color.rgb(233, 30, 99);
    private static final int NIGHTPOSITIVECOLOR = Color.rgb(255, 88, 35);
    private static final int NEGATIVECOLOR = Color.rgb(128, 128, 128);

    private GotoDialogListener listener;

    public interface GotoDialogListener {
        void onSubmitGotoDialog(int input, int type);
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
            firstParagraph = args.getInt("firstParagraph");
            lastParagraph = args.getInt("lastParagraph");
        }

        final TextView tvTitle = view.findViewById(R.id.tv_dlg_title);
        final EditText editText = view.findViewById(R.id.goto_num);
        final RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        final Button btn_go = view.findViewById(R.id.btn_go);
        final Button btn_cancel = view.findViewById(R.id.btn_cancel);

        if (!MDetect.isUnicode()) {
            tvTitle.setText(Rabbit.uni2zg(tvTitle.getText().toString()));
            btn_go.setText(Rabbit.uni2zg(tvTitle.getText().toString()));
            btn_cancel.setText(Rabbit.uni2zg(tvTitle.getText().toString()));
        }

        // set hint for editText
        editText.setHint(MDetect.getDeviceEncodedText(
                String.format("(%d-%d) စာမျက်နှာ", firstPage, lastPage)));
        // show soft keyboard
        editText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        // change hint for editText
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if ( i == R.id.radio_btn_page) {
                    editText.setHint(MDetect.getDeviceEncodedText(
                            String.format("(%d-%d) စာမျက်နှာ", firstPage, lastPage)));
                    editText.getText().clear();
                }
                else {
                    editText.setHint(MDetect.getDeviceEncodedText(
                            String.format("(%d-%d) စာပိုဒ်", firstParagraph, lastParagraph)));
                    editText.getText().clear();
                }
            }
        });

        // check input is valid and enable Go button
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                boolean isValid = false;
                int color = 0xFF969696;

                String inputStr = charSequence.toString();
                Log.v("onTextChanged", "input is - '"+ inputStr + "' and count is " + String.valueOf(inputStr.length()));

                if(inputStr.length() == 0 || inputStr.length() > 4){
                    isValid = false;
                    color = 0xFF969696;
                }
                else {
                    int input = Integer.parseInt(inputStr);
                    int checked = radioGroup.getCheckedRadioButtonId();

                    switch (checked){
                        case R.id.radio_btn_page:
                            if (input >= firstPage && input <= lastPage){
                                isValid = true;
                                int nightStatus = AppCompatDelegate.getDefaultNightMode();
                                if ( nightStatus == AppCompatDelegate.MODE_NIGHT_YES)
                                    color = Color.WHITE;
                                else
                                    color = DAYPOSITIVECOLOR;
                                break;
                            }
                        case R.id.radio_btn_para:
                            if (input >= firstParagraph && input <= lastParagraph){
                                isValid = true;
                                int nightStatus = AppCompatDelegate.getDefaultNightMode();
                                if ( nightStatus == AppCompatDelegate.MODE_NIGHT_YES)
                                    color = Color.WHITE;
                                else
                                    color = NIGHTPOSITIVECOLOR;
                                break;
                            }
                    }

                }

//                Log.v("onTextChanged", "is Enabled - " + String.valueOf(isValid)
//                        + " and color is " + String.valueOf(color));

                btn_go.setEnabled(isValid);
                btn_go.setTextColor(color);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selected = radioGroup.getCheckedRadioButtonId();
                int input = Integer.valueOf(editText.getText().toString().trim());

                int type = PAGE;
                if (selected == R.id.radio_btn_para)
                    type = PARAGRAPH;
                listener.onSubmitGotoDialog(input, type);

                dismiss();
            }
        });
    }
}
