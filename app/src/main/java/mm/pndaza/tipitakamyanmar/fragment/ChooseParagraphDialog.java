package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.ParagraphListAdapter;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class ChooseParagraphDialog extends DialogFragment {

    private Context context;
    private ArrayList<Integer> paragraphs;

    private static final String TAG = "GotoExplanationDialog";
    private OnChooseParagraphListener listener;

    public interface OnChooseParagraphListener {
        void onChooseParagraph(int paragraph);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.y =  +200;
        params.gravity = Gravity.TOP;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.requestFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.dlg_choose_paragraph, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
        if (context instanceof OnChooseParagraphListener) {
            listener = (OnChooseParagraphListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnChooseParagraphListener.OnChooseParagraph");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        boolean isFromPreviousPage = false;
        if (args != null) {
            paragraphs = args.getIntegerArrayList("paragraphs");
            isFromPreviousPage = args.getBoolean("is_from_previous_page");
        }

        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(Rabbit.uni2zg(tv_title.getText().toString()));
        Button btn_close = view.findViewById(R.id.btn_close);
        btn_close.setText(MDetect.getDeviceEncodedText(btn_close.getText().toString()));
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ListView listView = view.findViewById(R.id.list_view);
        TextView tv_empty = view.findViewById(R.id.tv_empty);

        TextView tv_additional_info = view.findViewById(R.id.tv_additional_info);
        if(!isFromPreviousPage){
            tv_additional_info.setVisibility(View.GONE);
        } else {
            String info = "ယခုစာမျက်နှာ၌ စာပိုဒ်နံပါတ် မပါသည့်အတွက်\\n ရှေ့စာမျက်နှာမှ စာပိုဒ်များကို ပြထားပါသည်။";
            tv_additional_info.setText(MDetect.getDeviceEncodedText(info));
        }

//        tv_empty.setText(MDetect.getDeviceEncodedText(getString(R.string.no_paragraph)));
//        tv_empty.setVisibility(View.GONE);
        listView.setEmptyView(tv_empty);
        ParagraphListAdapter adapter = new ParagraphListAdapter(context, paragraphs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                listener.onChooseParagraph(paragraphs.get(position));
            }
        });

    }
}
