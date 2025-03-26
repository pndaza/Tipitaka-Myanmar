package mm.pndaza.tipitakamyanmar.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mm.pndaza.tipitakamyanmar.data.Constants

class DBOpenHelper private constructor(context: Context) :
    SQLiteOpenHelper(
        context, context.filesDir.toString() + "/databases/" + Constants.DATABASE_FILE_NAME,
        null, Constants.DATABASE_VERSION
    ) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
    }

    companion object {
        private var sInstance: DBOpenHelper? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): DBOpenHelper {
            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.

            if (sInstance == null) {
                sInstance = DBOpenHelper(context.applicationContext)
            }
            return sInstance!!
        }
    }
}
