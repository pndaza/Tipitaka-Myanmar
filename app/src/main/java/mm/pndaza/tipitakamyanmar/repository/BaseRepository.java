package mm.pndaza.tipitakamyanmar.repository;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseRepository {

    private final SQLiteOpenHelper dbHelper;

    public BaseRepository(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    protected SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase();
    }

    protected SQLiteDatabase getWritableDatabase() {
        return dbHelper.getWritableDatabase();
    }
}