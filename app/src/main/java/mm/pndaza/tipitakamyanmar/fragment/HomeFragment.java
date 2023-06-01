package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.BookListAdapter;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

public class HomeFragment extends Fragment {

    public interface OnBookItemClickListener {
        void onBookItemClick(String bookID);
    }

    private OnBookItemClickListener callbackListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getDeviceEncodedText(getString(R.string.app_name_mm)));
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MDetect.init(getContext());
        initListView();
        FloatingActionButton fab = view.findViewById(R.id.fab_sutta);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FragmentManager fm = getFragmentManager();
                SuttaDialogFragment suttaDialog = new SuttaDialogFragment();
                suttaDialog.show(fm, "TOC");
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callbackListener = (OnBookItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implemented OnBookItemClickListener");
        }
    }

    private void initListView() {

        ArrayList<Object> books = new ArrayList<>();
        DBOpenHelper db = DBOpenHelper.getInstance(getContext());
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();

        String[] nikaya_list = getResources().getStringArray(R.array.nikaya);
        int countOfNikaya = nikaya_list.length;
        String bookID;
        String bookName;
        String sql;

        //Add books and header to ArrayList
        for (int i = 0; i < countOfNikaya; i++) {
            // add book_header
            books.add(nikaya_list[i]);
            // add book
            int category = i + 1; // category is starting from 1
            sql = "select id, name from book where category_id = " + category;
            Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    bookID = cursor.getString(0);
                    bookName = cursor.getString((1));
                    books.add(new Book(bookID, bookName));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        BookListAdapter bookListAdapter = new BookListAdapter(getContext(), books);
        final ListView listView = getView().findViewById(R.id.listView_books);
        listView.setAdapter(bookListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view, int position, long arg3) {
                if (listView.getItemAtPosition(position) instanceof Book) {
                    Book book = (Book) listView.getItemAtPosition(position);
                    callbackListener.onBookItemClick(book.getId());
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

/*        new MaterialAlertDialogBuilder(this)
                .setTitle("အကြောင်းအရာ")
                .setMessage("ပါဠိတော်မြန်မာပြန်ကျမ်းစာများကို ဖတ်ရှုနိုင်ပါသည်").show();*/

        WebView webView = new WebView(getContext());
        // populate the WebView with an HTML string
        if(SharePref.getInstance(getContext()).getPrefNightModeState()) {
            webView.loadUrl("file:///android_asset/web/info-night.html");
        } else {
            webView.loadUrl("file:///android_asset/web/info.html");
        }
        new MaterialAlertDialogBuilder(getContext()).setView(webView).show();
    }

}
