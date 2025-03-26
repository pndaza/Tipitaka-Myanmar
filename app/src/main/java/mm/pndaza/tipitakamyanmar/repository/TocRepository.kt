package mm.pndaza.tipitakamyanmar.repository

import android.database.sqlite.SQLiteOpenHelper
import mm.pndaza.tipitakamyanmar.database.DatabaseSchema.TocTable
import mm.pndaza.tipitakamyanmar.model.Toc

class TocRepository(dbHelper: SQLiteOpenHelper) : BaseRepository(dbHelper) {
    fun getToc(bookId: String?): ArrayList<Toc> {
        val tocArrayList = ArrayList<Toc>()
        val sql = """
            SELECT ${TocTable.name}, ${TocTable.type}, ${TocTable.pageNumber}
             FROM ${TocTable.table}
             WHERE ${TocTable.bookId} = ?
        """.trimIndent()
        val selectionArgs = arrayOf(bookId)
        readableDatabase.rawQuery(sql, selectionArgs).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(TocTable.name))
                    val type = cursor.getInt(cursor.getColumnIndexOrThrow(TocTable.type))
                    val pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow(TocTable.pageNumber))
                    tocArrayList.add(Toc(type.toString(), name, pageNumber))
                } while (cursor.moveToNext())
            }
        }
        return tocArrayList
    }

}