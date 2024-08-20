package mm.pndaza.tipitakamyanmar.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.constraintlayout.widget.Constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.model.Bookmark;
import mm.pndaza.tipitakamyanmar.model.Recent;
import mm.pndaza.tipitakamyanmar.model.Sutta;
import mm.pndaza.tipitakamyanmar.model.Toc;

import static android.content.ContentValues.TAG;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static DBOpenHelper sInstance;
    private static final String DATABASE_NAME = "tipi_mm.db";
    private static final int DATABASE_VERSION = 13;


    public static synchronized DBOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (sInstance == null) {
            sInstance = new DBOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBOpenHelper(Context context) {
        super(context, context.getFilesDir() + "/databases/" + DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Book getBookInfo(String bookid) {
        String bookName = "";
        int firstPage = 1;
        int lastPage = 1;
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name,first_page,last_page FROM book where id = '" + bookid + "'", null)) {
            if (cursor != null && cursor.moveToFirst()) {
                bookName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                firstPage = cursor.getInt(cursor.getColumnIndexOrThrow("first_page"));
                lastPage = cursor.getInt(cursor.getColumnIndexOrThrow("last_page"));
            }
        }

        return new Book(bookid, bookName, firstPage, lastPage);
    }

    public String getBoookName(String bookid) {
        String bookName = "";
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name FROM book where id = '" + bookid + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            bookName = cursor.getString(0);
        }
        return bookName;
    }

    public ArrayList<Toc> getToc(String bookid) {

        ArrayList<Toc> tocArrayList = new ArrayList<>();
        String rawToc = "";
        SQLiteDatabase database = getReadableDatabase();
        final String tocTable = "toc";
        final String columnName = "name";
        final String columnType = "type";
        final String columnPageNumber = "page_number";
        final String columnBookId = "book_id";
        final String sql = String.format("SELECT %s, %s, %s FROM %s WHERE %s = '%s'",
                columnName, columnType, columnPageNumber, tocTable, columnBookId, bookid);
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final String name = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
                final int type = cursor.getInt(cursor.getColumnIndexOrThrow(columnType));
                final int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow(columnPageNumber));
                final Toc toc = new Toc(String.valueOf(type), name, pageNumber);
                tocArrayList.add(toc);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return tocArrayList;
    }

    public Map<Integer, Integer> getParagraphs(String bookid) {
        HashMap<Integer, Integer> para_map = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                String.format("SELECT paragraph_number, page_number FROM para_page_map WHERE book_id = '%s'", bookid), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                final int paragraphNumber = cursor.getInt(cursor.getColumnIndexOrThrow("paragraph_number"));
                final int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow("page_number"));
                para_map.put(paragraphNumber, pageNumber);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return para_map;
    }


    public ArrayList<String> getAllBook() {
        ArrayList<String> bookList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT id FROM book", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                bookList.add(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return bookList;
    }

    public String getPaliBookID(String bookid) {
//        Log.d(Constraints.TAG, "getTranslationBookID: pali book id  " + bookid);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT pali_bookid FROM pali_books WHERE bookid = ?", new String[]{bookid});
        Log.d(Constraints.TAG, "getTranslationBookID: cursor count " + cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String pali_bookid = cursor.getString(0);
            Log.d(Constraints.TAG, "getTranslationBookID: " + pali_bookid);
            cursor.close();
            return pali_bookid;
        }
        return null;
    }

    public ArrayList<Bookmark> getBookmarks() {
        ArrayList<Bookmark> bookmarkList = new ArrayList<>();
        Cursor cursor = getReadableDatabase()
                .rawQuery(" SELECT note, bookid, pagenumber FROM bookmark", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                    String bookid = cursor.getString(cursor.getColumnIndexOrThrow("bookid"));
                    String bookName = getBoookName(bookid);
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow("pagenumber"));
                    bookmarkList.add(new Bookmark(note, bookid, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return bookmarkList;
    }

    public void addToBookmark(String note, String bookid, int pageNumber) {
        getWritableDatabase()
                .execSQL("INSERT INTO bookmark (note, bookid, pagenumber) VALUES (?,?,?)",
                        new Object[]{note, bookid, pageNumber});
    }

    public void removeFromBookmark(int rowid) {
        getWritableDatabase().execSQL("DELETE FROM bookmark WHERE rowid = " + rowid);
    }

    public void removeAllBookmarks() {
        getWritableDatabase().execSQL("DELETE FROM bookmark");
    }

    public ArrayList<Recent> getAllRecent() {
        ArrayList<Recent> recentList = new ArrayList<>();
        Cursor cursor = getReadableDatabase()
                .rawQuery(" SELECT rowid, bookid, pagenumber FROM recent ORDER BY rowid DESC", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String bookid = cursor.getString(cursor.getColumnIndexOrThrow("bookid"));
                    String bookName = getBoookName(bookid);
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow("pagenumber"));
                    recentList.add(new Recent(bookid, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return recentList;
    }


    private boolean isBookExistInRecent(String bookid) {
        Cursor cursor = this.getReadableDatabase()
                .rawQuery("SELECT bookid FROM recent Where bookid = '" + bookid + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void addToRecent(String bookid, int pageNumber) {
        if (isBookExistInRecent(bookid)) {
            getWritableDatabase()
                    .execSQL("UPDATE recent SET bookid = '" + bookid + "', pagenumber = '" + pageNumber
                            + "' WHERE bookid = '" + bookid + "'");
        } else {
            getWritableDatabase()
                    .execSQL("INSERT INTO recent (bookid, pagenumber) VALUES (?,?)", new Object[]{bookid, pageNumber});
        }

    }

    public void removeAllRecent() {
        getWritableDatabase().execSQL("DELETE FROM recent");
    }

    public ArrayList<Integer> getParagraphs(String bookId, int pageNumber) {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT paragraph_number FROM para_page_map WHERE book_id = ? AND page_number = ?";
        Log.d(TAG, "book id:" + bookId + " page number:" + pageNumber);
        Log.d(TAG, "getParagraphs: " + query);
        Cursor cursor = database.rawQuery(query, new String[]{bookId, String.valueOf(pageNumber)});
        ArrayList<Integer> paraNumbers = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                paraNumbers.add(cursor.getInt(0));
            } while (cursor.moveToNext());
            cursor.close();
            Log.d(TAG, "getParagraphs: " + paraNumbers);
        }
        return paraNumbers;
    }

    public int getFirstParagraph(String bookId) {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT paragraph_number FROM para_page_map WHERE book_id = ? ORDER BY paragraph_number ASC LIMIT 1";
        Cursor cursor = database.rawQuery(query, new String[]{bookId});
        if (cursor != null && cursor.moveToFirst()) {
            int paraNumber = cursor.getInt(0);
            cursor.close();
            Log.d(TAG, "getFirstParagraphs: " + paraNumber);
            return paraNumber;
        }
        return 0;
    }

    public ArrayList<Sutta> getSuttas(String filterWord) {
        ArrayList<Sutta> suttas = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String sql = "SELECT suttas.name, book_id, book.name as book_name, page_number from suttas " +
                "INNER JOIN book on book.id = suttas.book_id WHERE suttas.name LIKE ?";
        filterWord = "%" + filterWord + "%";
        Cursor cursor = database.rawQuery(sql, new String[]{filterWord});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String bookid = cursor.getString(cursor.getColumnIndexOrThrow("book_id"));
                    String bookName = cursor.getString(cursor.getColumnIndexOrThrow("book_name"));
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow("page_number"));
                    suttas.add(new Sutta(name, bookid, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
//        Log.d(TAG, "getSuttas: " + suttas.size());
        return suttas;
    }

    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}
