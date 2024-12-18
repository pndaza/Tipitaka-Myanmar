package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class SourceBookRepository extends BaseRepository {
    public SourceBookRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    private static final String SOURCE_BOOK_TABLE = "source_book";
    private static final String PALI_BOOK_ID_COLUMN = "pali_book_id";
    private static final String MM_BOOK_ID_COLUMN = "mm_book_id";
    private static final String LINK_TYPE_COLUMN = "link_type";

    public String getPaliBookID(String bookId, int pageNumber) {
        String paliBookId = null;

        String sql = "SELECT pali_book_id from source_book where mm_book_id = ? AND ? BETWEEN mm_first_page AND mm_last_page";
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{bookId, String.valueOf(pageNumber)})) {
            if (cursor != null && cursor.moveToFirst()) {
                paliBookId = cursor.getString(0);
            }
        }
        return paliBookId;
    }

    public String getLinkType(String bookId) {
        String linkType = null;
        String sql = new SQLBuilder().
                select(LINK_TYPE_COLUMN).
                from(SOURCE_BOOK_TABLE).
                where(MM_BOOK_ID_COLUMN, "=", bookId).
                build();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor.moveToFirst()) {
                linkType = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e("error", "Error querying data: " + e.getMessage());
        }
        return linkType;
    }
}
