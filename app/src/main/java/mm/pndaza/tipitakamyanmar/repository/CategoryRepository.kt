package mm.pndaza.tipitakamyanmar.repository

import android.database.sqlite.SQLiteOpenHelper
import mm.pndaza.tipitakamyanmar.database.DatabaseSchema.CategoryTable
import mm.pndaza.tipitakamyanmar.model.Category

class CategoryRepository(dbHelper: SQLiteOpenHelper) : BaseRepository(dbHelper) {
    val allCategories: ArrayList<Category>
        get() {
            val categories =
                ArrayList<Category>()
            val sql = """
                SELECT ${CategoryTable.id}, ${CategoryTable.name}
                 FROM ${CategoryTable.table} ORDER BY ${CategoryTable.id} ASC
            """.trimIndent()
            readableDatabase.rawQuery(sql, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val id =
                            cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.id))
                        val name =
                            cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.name))
                        categories.add(Category(id, name))
                    } while (cursor.moveToNext())
                }
            }
            return categories
        }

}
