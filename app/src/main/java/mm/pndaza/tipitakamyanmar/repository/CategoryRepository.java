package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Category;
import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class CategoryRepository extends BaseRepository {
    public CategoryRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    private static final String CATEGORY_TABLE = "category";
    private static final String CATEGORY_ID_COLUMN = "id";
    private static final String CATEGORY_NAME_COLUMN = "name";

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        String sql = new SQLBuilder().select(CATEGORY_ID_COLUMN, CATEGORY_NAME_COLUMN).from(CATEGORY_TABLE).build();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(CATEGORY_ID_COLUMN));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY_NAME_COLUMN));
                    categories.add(new Category(id, name));
                } while (cursor.moveToNext());
            }
        }
        return categories;
    }
}
