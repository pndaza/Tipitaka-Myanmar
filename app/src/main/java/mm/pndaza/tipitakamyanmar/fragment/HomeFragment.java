package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.BookListAdapter;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.dialog.InfoDialog;
import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.model.Category;
import mm.pndaza.tipitakamyanmar.repository.BookRepository;
import mm.pndaza.tipitakamyanmar.repository.CategoryRepository;
import mm.pndaza.tipitakamyanmar.utils.ActivityUtils;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

public class HomeFragment extends Fragment {
    private BookRepository bookRepository;
    private CategoryRepository categoryRepository;
    private final ArrayList<Object> books = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setTitle(MDetect.getInstance().getDeviceEncodedText(getString(R.string.app_name_mm)));

        }
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MDetect.init(getContext());
        bookRepository = new BookRepository(DBOpenHelper.getInstance(getContext()));
        categoryRepository = new CategoryRepository(DBOpenHelper.getInstance(getContext()));

        initListView();
        FloatingActionButton fab = view.findViewById(R.id.fab_sutta);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FragmentActivity activity = getActivity();
                if (activity != null) {
                    FragmentManager fm = activity.getSupportFragmentManager();
                    SuttaDialogFragment suttaDialog = new SuttaDialogFragment();
                    suttaDialog.show(fm, "TOC");

                }
            }
        });
    }


    private void initListView() {

//        ArrayList<Object> books = new ArrayList<>();
        ArrayList<Category> categories = categoryRepository.getAllCategories();
        for (Category category : categories) {
            books.add(category);
            ArrayList<Book> booksPerCategory = bookRepository.getBooksByCategory(category.id);
            books.addAll(booksPerCategory);
        }

        BookListAdapter bookListAdapter = new BookListAdapter(getContext(), books);
        final ListView listView = getView().findViewById(R.id.listView_books);
        listView.setAdapter(bookListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view, int position, long arg3) {
                if (listView.getItemAtPosition(position) instanceof Book book) {
                    ActivityUtils.startReadBookActivity(getContext(), book.getId());
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_info) {
            showInfoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {

        InfoDialog infoDialog = new InfoDialog();
        infoDialog.show(getParentFragmentManager(), "InfoDialog");
    }

}
