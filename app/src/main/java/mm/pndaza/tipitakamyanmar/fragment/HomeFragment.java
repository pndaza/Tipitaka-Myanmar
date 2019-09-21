package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.activity.ReadBookActivity;
import mm.pndaza.tipitakamyanmar.adapter.BookListAdapter;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class HomeFragment  extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getDeviceEncodedText(getString(R.string.app_name_mm)));
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListView();


    }

    private void initListView() {

        ArrayList<Object> books = new ArrayList<>();
        DBOpenHelper db = DBOpenHelper.getInstance(getContext());
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = null;


        String[] nikaya_list = {"ဝိနည်း","ဒီဃနိကာယ်","မဇ္ဈိမနိကာယ်","သံယုတ္တနိကာယ်","အင်္ဂုတ္တရနိကာယ်","ခုဒ္ဒကနိကာယ်","အဘိဓမ္မာ"};
        int countOfNikaya = nikaya_list.length ;
        String bookid;
        String bookName;
        int firstPage;
        int lastPage;
        String sql;

        //Add books and header to ArrayList
        for ( int i = 0; i < countOfNikaya; i++){
            if(MDetect.isUnicode()) {
                books.add(nikaya_list[i]);
            } else {
                books.add(Rabbit.uni2zg(nikaya_list[i]));
            }
            // category is starting from 1
            int category = i +1 ;
            sql = "select id, name, first_page, last_page from book_list where category = " + category;
            cursor = sqLiteDatabase.rawQuery(sql , null);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    bookid = cursor.getString(0);
                    bookName = cursor.getString((1));
                    if(!MDetect.isUnicode()){
                        bookName = Rabbit.uni2zg(bookName);
                    }
                    firstPage = cursor.getInt(2);
                    lastPage = cursor.getInt(3);
                    books.add(new Book(bookid, bookName, firstPage, lastPage));
                } while (cursor.moveToNext());

            }

        }


        BookListAdapter bookListAdapter = new BookListAdapter(getContext(), books);
        final ListView listView = getView().findViewById(R.id.listView_books);
        listView.setAdapter(bookListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listview, View view, int position, long arg3) {
                if( listView.getItemAtPosition(position) instanceof Book) {
                    Book book = (Book) listview.getItemAtPosition(position);
                    String bookid = book.getId();
                    // null bookid is listview header
                    if (bookid != null) {
                        Intent intent = new Intent(getContext(), ReadBookActivity.class);
                        intent.putExtra("bookID", bookid);
                        intent.putExtra("currentPage", 0);

                        getContext().startActivity(intent);
                    }
                }
            }
        });

    }
}
