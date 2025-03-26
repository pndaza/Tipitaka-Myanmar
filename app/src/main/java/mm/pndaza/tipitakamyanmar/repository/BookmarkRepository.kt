package mm.pndaza.tipitakamyanmar.repository

import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import mm.pndaza.tipitakamyanmar.database.DatabaseSchema.BookTable
import mm.pndaza.tipitakamyanmar.database.DatabaseSchema.BookmarkTable

import mm.pndaza.tipitakamyanmar.model.Bookmark

class BookmarkRepository(dbHelper: SQLiteOpenHelper) : BaseRepository(dbHelper) {

    fun getBookmarks(): ArrayList<Bookmark> {
        val bookmarkList = ArrayList<Bookmark>()
        val sql = """
            SELECT ${BookmarkTable.table}.rowid,
            ${BookmarkTable.note},
            ${BookmarkTable.table}.${BookmarkTable.bookId},
            ${BookTable.name},
            ${BookmarkTable.pageNumber}
            FROM ${BookmarkTable.table} INNER JOIN ${BookTable.table} 
            ON ${BookmarkTable.table}.${BookmarkTable.bookId} = ${BookTable.table}.${BookTable.id}
        """.trimIndent()

        readableDatabase.rawQuery(sql, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val rowid = cursor.getInt(cursor.getColumnIndexOrThrow("rowid"))
                    val note = cursor.getString(cursor.getColumnIndexOrThrow(BookmarkTable.note))
                    val bookId =
                        cursor.getString(cursor.getColumnIndexOrThrow(BookmarkTable.bookId))
                    val bookName = cursor.getString(cursor.getColumnIndexOrThrow(BookTable.name))
                    val pageNumber =
                        cursor.getInt(cursor.getColumnIndexOrThrow(BookmarkTable.pageNumber))
                    bookmarkList.add(Bookmark(rowid, note, bookId, bookName, pageNumber))
                } while (cursor.moveToNext())
            }
        }
        return bookmarkList
    }

    fun addToBookmark(note: String, bookId: String, pageNumber: Int) {
        val sql = """
            INSERT INTO ${BookmarkTable.table} (${BookmarkTable.note}, ${BookmarkTable.bookId}, ${BookmarkTable.pageNumber})
            VALUES (?, ?, ?)
        """.trimIndent()
        val bindArgs = arrayOf<Any>(note, bookId, pageNumber)

        Log.d("addToBookmark: ", sql)
        writableDatabase.execSQL(sql, bindArgs)
    }

    fun removeFromBookmark(rowid: Int) {
        val sql = """
            DELETE FROM ${BookmarkTable.table}
            WHERE rowid = ?
        """.trimIndent()
        val bindArgs = arrayOf<Any>(rowid)
        writableDatabase.execSQL(sql, bindArgs)
    }

    fun removeAllBookmarks() {
        val sql = "DELETE FROM ${BookmarkTable.table}"
        writableDatabase.execSQL(sql)
    }
}