package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Bookmark;
import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class BookmarkRepository extends BaseRepository {

    private static final String BOOKMARK_TABLE = "bookmark";
    private static final String ROW_ID_COLUMN = "rowid";
    private static final String BOOK_ID_COLUMN = "book_id";
    private static final String BOOK_NAME_COLUMN = "name";
    private static final String PAGE_NUMBER_COLUMN = "page_number";
    private static final String NOTE_COLUMN = "note";

    private static final String BOOK_TABLE = "book";

    public BookmarkRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    public ArrayList<Bookmark> getBookmarks() {
        ArrayList<Bookmark> bookmarkList = new ArrayList<>();
        SQLBuilder sqlBuilder = new SQLBuilder().
                select(NOTE_COLUMN, BOOK_ID_COLUMN, BOOK_NAME_COLUMN, PAGE_NUMBER_COLUMN).
                from(BOOKMARK_TABLE).
                innerJoin(BOOK_TABLE, BOOKMARK_TABLE + "." + BOOK_ID_COLUMN, BOOK_TABLE + "." + "id");
        String sql = sqlBuilder.build();
//        String sql = """
//                         SELECT note, bookmark.book_id, name, page_number
//                         FROM bookmark
//                         INNER JOIN book ON bookmark.book_id = book.id
//                """;
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String note = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_COLUMN));
                    String bookId = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_ID_COLUMN));
                    String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_NAME_COLUMN));
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow(PAGE_NUMBER_COLUMN));
                    bookmarkList.add(new Bookmark(note, bookId, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
        }
        return bookmarkList;
    }

    public void addToBookmark(String note, String bookId, int pageNumber) {
        String sql = new SQLBuilder().
                insert(NOTE_COLUMN, BOOK_ID_COLUMN, PAGE_NUMBER_COLUMN).
                into(BOOKMARK_TABLE).
                values(note, bookId, pageNumber).
                build();
        Log.d( "addToBookmark: ", sql);
        getWritableDatabase().execSQL(sql);
    }

    public void removeFromBookmark(int rowid) {
        SQLBuilder sqlBuilder = new SQLBuilder().delete().from(BOOKMARK_TABLE).
                where(ROW_ID_COLUMN, " = ?", rowid);
        String sql = sqlBuilder.build();
        getWritableDatabase().execSQL(sql, null);
    }

    public void removeAllBookmarks() {
        final String sql = new SQLBuilder().delete().from(BOOKMARK_TABLE).build();
        getWritableDatabase().execSQL(sql);
    }
}
