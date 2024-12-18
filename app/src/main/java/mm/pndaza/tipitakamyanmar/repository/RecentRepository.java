package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Recent;
import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class RecentRepository extends BaseRepository {

    private static final String RECENT_TABLE = "recent";
    private static final String BOOK_ID_COLUMN = "book_id";
    private static final String PAGE_NUMBER_COLUMN = "page_number";
    private static final String BOOK_TABLE = "book";
    private static final String BOOK_NAME_COLUMN = "name";

    public RecentRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    public ArrayList<Recent> getAllRecent() {
        ArrayList<Recent> recentList = new ArrayList<>();
        final String sql = """
                    SELECT recent.book_id, name, recent.page_number
                    FROM recent INNER JOIN book ON recent.book_id = book.id
                """;
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String bookId = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_ID_COLUMN));
                String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_NAME_COLUMN));
                int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow(PAGE_NUMBER_COLUMN));
                recentList.add(new Recent(bookId, bookName, pageNumber));
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return recentList;
    }

    public void addToRecent(String bookId, int pageNumber) {
        if (isBookExistInRecent(bookId)) {
            updateRecentPageNumber(bookId, pageNumber);
        } else {
            insertRecent(bookId, pageNumber);
        }
    }

    private void updateRecentPageNumber(String bookId, int pageNumber) {

        final String sql = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
                RECENT_TABLE, PAGE_NUMBER_COLUMN, BOOK_ID_COLUMN);
        String[] selectionArgs = {String.valueOf(pageNumber), bookId};
        getWritableDatabase().execSQL(sql, selectionArgs);
    }

    private void insertRecent(String bookId, int pageNumber) {
        final String sql = new SQLBuilder().
                insert( BOOK_ID_COLUMN, PAGE_NUMBER_COLUMN).
                into(RECENT_TABLE).
                values(bookId, pageNumber).
                build();
        Log.d("insertRecent: ", sql);
        getWritableDatabase().execSQL(sql);
    }

    private boolean isBookExistInRecent(String bookId) {
        final String sql = String.format("SELECT 1 FROM %s WHERE %s = ? LIMIT 1",
                RECENT_TABLE, BOOK_ID_COLUMN);
        String[] selectionArgs = {bookId};
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, selectionArgs)) {
            return cursor != null && cursor.getCount() > 0;
        }
    }

    public void removeAllRecent() {
        final String sql = String.format("DELETE FROM %s", RECENT_TABLE);
        getWritableDatabase().execSQL(sql);
    }
}
