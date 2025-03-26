package mm.pndaza.tipitakamyanmar.fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class BookmarkDialog extends DialogFragment {
    private EditText input;
    private Button btnSave, btnCancel;
    private OnBookmarkSaveListener saveListener;

    public interface OnBookmarkSaveListener {
        void onBookmarkSaved(String bookmark);
    }

    // Empty constructor required for DialogFragment
    public BookmarkDialog() {
    }

    // Method to create new instance with listener
    public static BookmarkDialog newInstance(OnBookmarkSaveListener listener) {
        BookmarkDialog fragment = new BookmarkDialog();
        fragment.saveListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dlg_addbookmark, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Optional: Customize dialog appearance
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set width to 400dp and height to wrap_content
            // Convert 400dp to pixels
            int width = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    350,
                    requireContext().getResources().getDisplayMetrics()
            );
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundResource(R.drawable.dialog_background); // Apply rounded corners

        // Initialize MDetect
        MDetect.init(requireContext());

        // Initialize views
        input = view.findViewById(R.id.input);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);

        setupText();
        setupListeners();
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            String bookmark = input.getText().toString().trim();

            if (bookmark.isEmpty()) {
                Toast.makeText(requireContext(),
                        "မှတ်စရာ တခုခု ထည့်ပါ။",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (saveListener != null) {
                    saveListener.onBookmarkSaved(bookmark);
                }
                dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }


    private void setupText() {
        String hint = "မှတ်လိုသောစာသား ထည့်ရန်";
        String confirm = "သိမ်းမယ်";
        String cancel = "မသိမ်းတော့ဘူး";
        if (!MDetect.getInstance().isUnicode()) {
            hint = Rabbit.uni2zg(hint);
            confirm = Rabbit.uni2zg(confirm);
            cancel = Rabbit.uni2zg(cancel);
        }
        input.setHint(hint);
        btnSave.setText(confirm);
        btnCancel.setText(cancel);
    }
}