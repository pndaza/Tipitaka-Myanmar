package mm.pndaza.tipitakamyanmar.repository

import android.database.sqlite.SQLiteOpenHelper
import mm.pndaza.tipitakamyanmar.database.DatabaseSchema.BookTable
import mm.pndaza.tipitakamyanmar.database.DatabaseSchema.SuttaTable
import mm.pndaza.tipitakamyanmar.model.Sutta

class SuttaRepository(dbHelper: SQLiteOpenHelper) : BaseRepository(dbHelper) {

    val bookName = "book_name"

    fun getSuttas(filterWord: String): List<Sutta> {
        if (filterWord.isEmpty()) {
            return emptyList()
        }

        val suttaList = mutableListOf<Sutta>()
        val sql = """
            SELECT 
                ${SuttaTable.table}.${SuttaTable.name},
                ${SuttaTable.bookId},
                ${BookTable.table}.${BookTable.name} AS $bookName,
                ${SuttaTable.pageNumber}
            FROM ${SuttaTable.table}
            INNER JOIN ${BookTable.table} 
                ON ${SuttaTable.table}.${SuttaTable.bookId} = ${BookTable.table}.${BookTable.id}
            WHERE ${SuttaTable.table}.${SuttaTable.name} LIKE ?
        """.trimIndent()

        val selectionArgs = arrayOf("%$filterWord%")

        readableDatabase.rawQuery(sql, selectionArgs).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(SuttaTable.name))
                    val bookId = cursor.getString(cursor.getColumnIndexOrThrow(SuttaTable.bookId))
                    val bookName = cursor.getString(cursor.getColumnIndexOrThrow(bookName))
                    val pageNumber =
                        cursor.getInt(cursor.getColumnIndexOrThrow(SuttaTable.pageNumber))
                    suttaList.add(Sutta(name, bookId, bookName, pageNumber))
                } while (cursor.moveToNext())
            }
        }
        return suttaList
    }
}