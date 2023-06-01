package mm.pndaza.tipitakamyanmar.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.SearchAdapter;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Page;
import mm.pndaza.tipitakamyanmar.model.Search;
import mm.pndaza.tipitakamyanmar.utils.BookUtil;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;
import mm.pndaza.tipitakamyanmar.utils.SearchUtil;

public class SearchFragment extends Fragment {

    public interface OnSearchItemClickListener {
        void onSearchItemClick(String bookid, int pageNumber, String queryWord);
    }

    private OnSearchItemClickListener callbackListener;
    private ArrayList<Search> searchResult = new ArrayList<>();
    private SearchAdapter adapter;
    private static Context context;
    private String queryWord;

    private KProgressHUD progressDialog;
    private TextView emptyInfoView;


//    private static final String TAG = "SearchFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getDeviceEncodedText("ရှာဖွေရေး"));
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        emptyInfoView = view.findViewById(R.id.empty_info);
        RecyclerView recyclerView = view.findViewById(R.id.search_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        SearchView searchInput = view.findViewById(R.id.search_input);
        searchInput.setQueryHint(MDetect.getDeviceEncodedText("ရှာလိုသောစကားလုံးကို ရိုက်ထည့်ပါ"));
        searchInput.setFocusable(true);
        searchInput.setIconified(false);
//        searchInput.requestFocusFromTouch();
        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // todo search

                if (query.length() > 0) {
                    if (!MDetect.isUnicode()) {
                        query = Rabbit.zg2uni(query);
                    }
                    queryWord = query;
                    adapter = new SearchAdapter(searchResult, queryWord);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnClickListener(view1 -> {
                        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view1.getTag();
                        int position = viewHolder.getAdapterPosition();
                        String bookid = searchResult.get(position).getBookID();
                        int pageNumber = searchResult.get(position).getPageNumber();
//                Log.d("pageNumber" , ""+pageNumber);
                        callbackListener.onSearchItemClick(bookid, pageNumber, queryWord);
                    });

                    progressDialog = KProgressHUD.create(context)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel(MDetect.getDeviceEncodedText("ရှာနေဆဲ"))
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(0.5f);

                    new searchIt().execute(query);
                    searchInput.clearFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (searchResult.size() > 0) {
                    searchResult.clear();
                    adapter.notifyDataSetChanged();
                }
                emptyInfoView.setText("");
                return false;
            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callbackListener = (OnSearchItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implemented OnSearchItemClickListener");

        }
    }

    public class searchIt extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... queries) {

            String query = queries[0];
            ArrayList<String> bookList = DBOpenHelper.getInstance(context).getAllBook();
            for (String book : bookList) {
                String bookName = DBOpenHelper.getInstance(context).getBoookName(book);
                ArrayList<Page> pages = new ArrayList<>();
                pages.clear();

                try {
                    pages = BookUtil.read(context, book);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (Page page : pages) {
                    if (page.getPageContent().contains(query)) {
                        searchResult.addAll(SearchUtil.searchWord(
                                book, bookName, page.getPageNumber(), page.getPageContent(), query));
                    }
                }
                publishProgress(searchResult.size());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            progressDialog.setLabel(MDetect.getDeviceEncodedText("ရှာနေဆဲ"));
            int found = progress[0];
            if (found > 0) {
                progressDialog.setDetailsLabel(
                        MDetect.getDeviceEncodedText("တွေ့ရှိမှု(" + NumberUtil.toMyanmar(found) + ")ကြိမ်"));
            }
            progressDialog.show();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();
            int found = searchResult.size();
            if (found > 0) {
                getActivity().setTitle(MDetect.getDeviceEncodedText(
                        "တွေ့ရှိမှု - " + NumberUtil.toMyanmar(found) + " ကြိမ်"));
            } else {
                emptyInfoView.setText(MDetect.getDeviceEncodedText(
                        "\"" + queryWord + "\" " + getString(R.string.search_empty)));
            }
        }
    }

}
