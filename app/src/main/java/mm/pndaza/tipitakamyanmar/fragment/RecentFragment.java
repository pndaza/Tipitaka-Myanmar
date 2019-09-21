package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.RecentAdapter;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Recent;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class RecentFragment extends Fragment {

    public interface OnRecentItemClickListener {
        void onRecentItemClick(String bookid, int pageNumber);
    }


    private Context context;
    private RecyclerView recentListView;
    TextView emptyInfoView;
    private ArrayList<Recent> recents;
    private  OnRecentItemClickListener callbackListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getDeviceEncodedText(getString(R.string.recent_mm)));
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        //bind view
        recentListView = view.findViewById(R.id.listView_recent);
        recentListView.setLayoutManager(new LinearLayoutManager(context));
        recentListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        applyRecentList();

        emptyInfoView = view.findViewById(R.id.empty_info);
        applyEmptyInfoView(emptyInfoView);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recent, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_clearAll) {
            clearRecent();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            callbackListener = (OnRecentItemClickListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implemented OnRecentItemCliclListener");

        }
    }

    private void applyRecentList() {
        recents = DBOpenHelper.getInstance(getContext()).getAllRecent();
        final RecentAdapter adapter = new RecentAdapter(recents);
        recentListView.setAdapter(adapter);
        adapter.setOnClickListener(view -> {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            String bookid = recents.get(position).getBookid();
            int pageNumber = recents.get(position).getPageNumber();

            Log.d("pageNumber" , ""+pageNumber);

                callbackListener.onRecentItemClick(bookid, pageNumber);
        });
    }

    private void clearRecent() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogTheme);

        String message = "လက်တလော ကြည့်ရှုထားသည်များကို ဖယ်ရှားမှာလား";
        String comfirm = "ဖယ်ရှားမယ်";
        String cancel = "မလုပ်တော့ဘူး";
        if (!MDetect.isUnicode()) {
            message = Rabbit.uni2zg(message);
            comfirm = Rabbit.uni2zg(comfirm);
            cancel = Rabbit.uni2zg(cancel);
        }

        alertDialog.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(comfirm,
                        (dialog, id) -> {
                            DBOpenHelper.getInstance(context).removeAllRecent();
                            applyRecentList();
                                applyEmptyInfoView(emptyInfoView);
                        })
                .setNegativeButton(cancel, (dialog, id) -> {
                });
        alertDialog.show();
    }

    private void applyEmptyInfoView(TextView emptyInfoView){

        String info = getString(R.string.recent_empty);
        if (!MDetect.isUnicode()) {
            info = Rabbit.uni2zg(info);
        }
        emptyInfoView.setText(info);
        emptyInfoView.setVisibility(recents.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }


}
