package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.TocAdapter;
import mm.pndaza.tipitakamyanmar.model.Toc;

import static android.content.ContentValues.TAG;


public class TocBottomSheetDialogFragment extends BottomSheetDialogFragment implements TocAdapter.OnItemClickListener{

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onTocItemClick(int page);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dlg_toc, container, false);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            listener = (OnItemClickListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement TocBottomSheetDialogFragment.TocDialogItemClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        Bundle args = getArguments();
        ArrayList<Toc> tocList = args.getParcelableArrayList("toc_list");
        TocAdapter tocAdapter = new TocAdapter( tocList, this);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(tocAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


/*        final ListView listView = view.findViewById(R.id.lv_toc);
        listView.setAdapter(tocAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toc toc = (Toc)adapterView.getItemAtPosition(i);
                int pagenum = Integer.valueOf(toc.getPage());
                listener.onTocItemClick(pagenum);
                dismiss();
            }
        });*/
    }

    @Override
    public void onItemClick(Toc toc) {
        int pageNumber = toc.getPage();
        listener.onTocItemClick(pageNumber);
        dismiss();
    }
}
