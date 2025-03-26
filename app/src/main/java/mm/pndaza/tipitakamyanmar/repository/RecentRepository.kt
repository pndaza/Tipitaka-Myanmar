package mm.pndaza.tipitakamyanmar.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.database.sqlite.transaction
import mm.pndaza.tipitakamyanmar.database.DatabaseSchema.BookTable
import mm.pndaza.tipitakamyanmar.database.DatabaseSchema.RecentTable
import mm.pndaza.tipitakamyanmar.model.Recent

class RecentRepository(dbHelper: SQLiteOpenHelper) : BaseRepository(dbHelper) {

    fun getAll(): ArrayList<Recent> {
        val sql = """
            SELECT ${RecentTable.table}.${RecentTable.bookId},
            ${BookTable.name},
            ${RecentTable.table}.${RecentTable.pageNumber}
            FROM ${RecentTable.table} INNER JOIN ${BookTable.table} 
            ON ${RecentTable.table}.${RecentTable.bookId} = ${BookTable.table}.${BookTable.id}
        """.trimIndent()

        return readableDatabase.rawQuery(sql, null).use { cursor ->
            val recentList = ArrayList<Recent>()

            if (cursor.moveToFirst()) {
                val bookIdIndex = cursor.getColumnIndexOrThrow(RecentTable.bookId)
                val bookNameIndex = cursor.getColumnIndexOrThrow(BookTable.name)
                val pageNumberIndex = cursor.getColumnIndexOrThrow(RecentTable.pageNumber)

                do {
                    val bookId = cursor.getStringOrNull(bookIdIndex) ?: continue
                    val bookName = cursor.getStringOrNull(bookNameIndex) ?: continue
                    val pageNumber = cursor.getIntOrNull(pageNumberIndex) ?: 0

                    recentList.add(Recent(bookId, bookName, pageNumber))
                } while (cursor.moveToNext())
            }

            recentList
        }
    }

    fun add(bookId: String, pageNumber: Int): Long {
        var row = -1L
        writableDatabase.transaction {
            try {
                remove(bookId)
                row = insert(bookId, pageNumber)
            } finally {
            }
        }
        return row
    }

    private fun insert(bookId: String, pageNumber: Int): Long {
        val contentValues = ContentValues().apply {
            put(RecentTable.bookId, bookId)
            put(RecentTable.pageNumber, pageNumber)
        }
        return writableDatabase.insert(
            RecentTable.table,
            null,
            contentValues
        )
    }

    fun removeAll() {
        val sql = "DELETE FROM ${RecentTable.table}"
        writableDatabase.execSQL(sql)
    }

    fun remove(bookId: String): Int {
        val whereClause = "${RecentTable.bookId} = ?"
        val whereArgs = arrayOf(bookId)
        return writableDatabase.delete(
            RecentTable.table,
            whereClause,
            whereArgs
        )
    }
}