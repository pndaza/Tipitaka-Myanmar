package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Sutta;
import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class SuttaRepository extends BaseRepository {
    private static final String SUTTA_TABLE = "sutta";
    private static final String BOOK_TABLE = "book";
    private static final String SUTTA_NAME_COLUMN = "name";
    private static final String BOOK_ID_COLUMN = "book_id";
    private static final String BOOK_NAME_COLUMN = "name";
    private static final String BOOK_NAME_AS_COLUMN = "book_name";
    private static final String PAGE_NUMBER_COLUMN = "page_number";

    public SuttaRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    public ArrayList<Sutta> getSuttas(String filterWord) {
        ArrayList<Sutta> suttaList = new ArrayList<>();
        if (filterWord == null || filterWord.isEmpty()) {
            return suttaList;
        }
        SQLBuilder sqlBuilder = new SQLBuilder().
                select(SUTTA_TABLE + "." + SUTTA_NAME_COLUMN,
                        BOOK_ID_COLUMN,
                        BOOK_TABLE + "." + BOOK_NAME_COLUMN + " as " + BOOK_NAME_AS_COLUMN,
                        PAGE_NUMBER_COLUMN).
                from(SUTTA_TABLE).
                innerJoin(BOOK_TABLE, SUTTA_TABLE + "." + BOOK_ID_COLUMN, BOOK_TABLE + "." + "id").
                where(SUTTA_TABLE + "." + SUTTA_NAME_COLUMN, " LIKE ", "%" + filterWord + "%");
        String sql = sqlBuilder.build();
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(SUTTA_NAME_COLUMN));
                    String bookId = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_ID_COLUMN));
                    String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_NAME_AS_COLUMN));
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow(PAGE_NUMBER_COLUMN));
                    suttaList.add(new Sutta(name, bookId, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
//        Log.d(TAG, "getSuttas: " + suttas.size());
        return suttaList;
    }
}
