package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.SearchAdapter;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.model.Page;
import mm.pndaza.tipitakamyanmar.model.SearchQuery;
import mm.pndaza.tipitakamyanmar.model.SearchResult;
import mm.pndaza.tipitakamyanmar.repository.BookRepository;
import mm.pndaza.tipitakamyanmar.utils.ActivityUtils;
import mm.pndaza.tipitakamyanmar.utils.BookUtil;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;
import mm.pndaza.tipitakamyanmar.utils.SearchUtil;
import mm.pndaza.tipitakamyanmar.view.IOSProgressDialog;
import android.view.inputmethod.InputMethodManager;

public class SearchFragment extends Fragment {

    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    private SearchAdapter adapter;
    private Context context;
    private String queryWord;

    private IOSProgressDialog progressDialog;
    private TextView emptyInfoView;
    private BookRepository bookRepository;

    private RecyclerView recyclerView;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getInstance().getDeviceEncodedText("ရှာဖွေရေး"));
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        bookRepository = new BookRepository(DBOpenHelper.getInstance(context));
        setupViews(view);
        setupSearchInput(view);
    }


    @Override
    public void onResume() {
        super.onResume();
        SearchView searchInput = getView().findViewById(R.id.search_input);
        if (searchInput != null) {
            searchInput.clearFocus();
            // Optional: Hide the keyboard as well
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void setupViews(View view) {
        emptyInfoView = view.findViewById(R.id.empty_info);
        recyclerView = view.findViewById(R.id.search_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    private void setupSearchInput(View view) {
        SearchView searchInput = view.findViewById(R.id.search_input);
        searchInput.setQueryHint(MDetect.getInstance().getDeviceEncodedText("ရှာလိုသောစကားလုံးကို ရိုက်ထည့်ပါ"));
        searchInput.setIconified(false);
        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    clearSearchResults();
                    handleSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clearSearchResults();
                return false;
            }
        });
    }

    private void handleSearch(String query) {
        if (!MDetect.getInstance().isUnicode()) {
            query = Rabbit.zg2uni(query);
        }
        queryWord = query;
        adapter = new SearchAdapter(searchResults, queryWord);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(view -> {
            int position = recyclerView.getChildAdapterPosition(view);
            if (position != RecyclerView.NO_POSITION) {
                SearchResult result = searchResults.get(position);
                ActivityUtils.startReadBookActivity(context, result.bookID(), result.pageNumber(), queryWord);
            }
        });

        showProgressDialog();
        executeSearch(query);
    }

    private void clearSearchResults() {
        if (!searchResults.isEmpty()) {
            searchResults.clear();
            adapter.notifyDataSetChanged();
        }
        emptyInfoView.setText("");
    }

    private void showProgressDialog() {
        progressDialog = new IOSProgressDialog();
        progressDialog.showProgressDialog(context, MDetect.getInstance().getDeviceEncodedText("ရှာနေဆဲ..."));
    }

    private void executeSearch(String query) {
        executorService.execute(() -> {
            long startTime = System.currentTimeMillis();
            ArrayList<Book> bookList = bookRepository.getAllBooks();
            for (Book book : bookList) {
                List<Page> pages;
                try {
                    pages = BookUtil.readBook(context, book.getId(), book.getFirstPage());
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                for (Page page : pages) {
                    if (page.pageContent.contains(query)) {
                        List<SearchResult> matches = SearchUtil.findMatches(
                                new SearchQuery(book.getId(), book.getName(), page.pageNumber, page.pageContent, query));
                        if (!matches.isEmpty()) {
                            searchResults.addAll(matches);
                            updateProgress();
                        }
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime; // Duration in milliseconds
            Log.d("time", "Execution time: " + duration + " ms");
            Log.d("time", "Execution time: " + duration / 1000 + " second");
            onSearchCompleted();
        });
    }

    private void updateProgress() {
        handler.post(() -> {
            int found = searchResults.size();
            progressDialog.setMessage(MDetect.getInstance().getDeviceEncodedText("ရှာနေဆဲ (" + NumberUtil.toMyanmar(found) + ")"));
//            progressDialog.setLabel(MDetect.getInstance().getDeviceEncodedText("ရှာနေဆဲ (" + NumberUtil.toMyanmar(found) + ")"));
            adapter.notifyDataSetChanged();
        });
    }

    private void onSearchCompleted() {
        handler.post(() -> {
            progressDialog.dismissProgressDialog();
            int found = searchResults.size();
            if (found > 0) {
                getActivity().setTitle(MDetect.getInstance().getDeviceEncodedText("တွေ့ရှိမှု - " + NumberUtil.toMyanmar(found) + " ကြိမ်"));
            } else {
                emptyInfoView.setText(MDetect.getInstance().getDeviceEncodedText("\"" + queryWord + "\" " + getString(R.string.search_empty)));
            }
        });
    }
}
