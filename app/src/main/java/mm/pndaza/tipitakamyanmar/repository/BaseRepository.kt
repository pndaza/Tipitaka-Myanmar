package mm.pndaza.tipitakamyanmar.repository

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class BaseRepository(private val dbHelper: SQLiteOpenHelper) {
    protected val readableDatabase: SQLiteDatabase
        get() = dbHelper.readableDatabase

    protected val writableDatabase: SQLiteDatabase
        get() = dbHelper.writableDatabase
}