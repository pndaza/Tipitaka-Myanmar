package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
    private TextView tv_empty;
    private ParagraphListAdapter adapter;
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
        if (args != null) {
            paragraphs = args.getIntegerArrayList("paragraphs");
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
        tv_empty = view.findViewById(R.id.tv_empty);
        tv_empty.setText(MDetect.getDeviceEncodedText(getString(R.string.no_paragraph)));
//        tv_empty.setVisibility(View.GONE);
        listView.setEmptyView(tv_empty);
        adapter = new ParagraphListAdapter(context, paragraphs);
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
