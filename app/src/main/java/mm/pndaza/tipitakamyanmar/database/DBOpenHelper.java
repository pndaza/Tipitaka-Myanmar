package mm.pndaza.tipitakamyanmar.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static mm.pndaza.tipitakamyanmar.data.Constants.DATABASE_FILE_NAME;
import static mm.pndaza.tipitakamyanmar.data.Constants.DATABASE_VERSION;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static DBOpenHelper sInstance;
    public static synchronized DBOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (sInstance == null) {
            sInstance = new DBOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBOpenHelper(Context context) {
        super(context, context.getFilesDir() + "/databases/" + DATABASE_FILE_NAME
                , null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}
