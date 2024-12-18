package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Toc;
import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class TocRepository extends BaseRepository {

    public TocRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    private static final String TOC_TABLE = "toc";
    private static final String TOC_NAME_COLUMN = "name";
    private static final String TOC_TYPE_COLUMN = "type";
    private static final String BOOK_ID_COLUMN = "book_id";
    private static final String PAGE_NUMBER_COLUMN = "page_number";

    public ArrayList<Toc> getToc(String bookId) {
        ArrayList<Toc> tocArrayList = new ArrayList<>();
        final SQLBuilder sqlBuilder = new SQLBuilder().
                select(TOC_NAME_COLUMN, TOC_TYPE_COLUMN, PAGE_NUMBER_COLUMN).
                from(TOC_TABLE).
                where(BOOK_ID_COLUMN , " = ", bookId);
        final String sql = sqlBuilder.build();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(TOC_NAME_COLUMN));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(TOC_TYPE_COLUMN));
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow(PAGE_NUMBER_COLUMN));
                    tocArrayList.add(new Toc(String.valueOf(type), name, pageNumber));
                } while (cursor.moveToNext());
            }
        }
        return tocArrayList;
    }
}