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
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Paragraph;
import mm.pndaza.tipitakamyanmar.repository.ParagraphRepository;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class GotoPaliAppDialog extends DialogFragment {

    private Context context;
    private String bookId;
    private int pageNumber;
//    private ArrayList<Integer> paragraphs;

    private static final String TAG = "GotoExplanationDialog";
    private Listener listener;

    public interface Listener {
        void onParagraphSelected(Paragraph paragraph);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = +200;
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
        if (context instanceof Listener) {
            listener = (Listener) context;
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
            bookId = args.getString("book_id");
            pageNumber = args.getInt("page_number");
//            paragraphs = args.getIntegerArrayList("paragraphs");
//            isFromPreviousPage = args.getBoolean("is_from_previous_page");
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

//        tv_empty.setText(MDetect.getDeviceEncodedText(getString(R.string.no_paragraph)));
//        tv_empty.setVisibility(View.GONE);
        listView.setEmptyView(tv_empty);
        int currentPage = pageNumber;
        boolean isResultFromPreviusPage = false;
        ParagraphRepository paragraphRepository = new ParagraphRepository(DBOpenHelper.getInstance(context));
        final ArrayList<Paragraph> paragraphs = new ArrayList<>(paragraphRepository.getParagraphs(bookId, pageNumber));
        if (paragraphs.isEmpty()) {
            while (paragraphs.isEmpty() && currentPage-- > 1) {
                paragraphs.addAll(paragraphRepository.getParagraphs(bookId, currentPage));
            }
            if(!paragraphs.isEmpty()){
            isResultFromPreviusPage = true;
            }
        }
        if (isResultFromPreviusPage) {
            String info = "ယခုစာမျက်နှာ၌ စာပိုဒ်နံပါတ် မပါသည့်အတွက်\\n ရှေ့စာမျက်နှာမှ စာပိုဒ်များကို ပြထားပါသည်။";
            tv_additional_info.setText(MDetect.getDeviceEncodedText(info));
        } else {
            tv_additional_info.setVisibility(View.GONE);
        }

        ParagraphListAdapter adapter = new ParagraphListAdapter(context, paragraphs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                listener.onParagraphSelected(paragraphs.get(position));
            }
        });

    }
}
