package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class PageMapRepository extends BaseRepository {
    public PageMapRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    public int getPaliPageNumber(String bookId, int pageNumber) {
        int paliPageNumber = 0;

        String sql = "SELECT pali_page_number from mm_pali_page_map " +
                "where mm_book_id = ? AND mm_page_number = ?";
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{bookId, String.valueOf(pageNumber)})) {
            if (cursor != null && cursor.moveToFirst()) {
                paliPageNumber = cursor.getInt(0);
            }
        }
        return paliPageNumber;
    }

}
