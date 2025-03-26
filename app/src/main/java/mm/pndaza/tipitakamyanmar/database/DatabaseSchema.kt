package mm.pndaza.tipitakamyanmar.database

/**
 * Database schema definition for the Tipitaka Myanmar app.
 * Contains table and column names to be used across repositories.
 */
object DatabaseSchema {
    object BookTable {
        const val table = "book"
        const val id = "id"
        const val name = "name"
        // Add other book columns as needed
    }

    object RecentTable {
        const val table = "recent"
        const val bookId = "book_id"
        const val pageNumber = "page_number"
    }


    object BookmarkTable {
        const val table = "bookmark"
        const val bookId = "book_id"
        const val pageNumber = "page_number"
        const val note = "note"
    }

    object CategoryTable {
        const val table = "category"
        const val id = "id"
        const val name = "name"
    }

    object SuttaTable {
        const val table = "sutta"
        const val name = "name"
        const val bookId = "book_id"
        const val pageNumber = "page_number"
    }

    object TocTable {
        const val table = "toc"
        const val name = "name"
        const val type = "type"
        const val bookId = "book_id"
        const val pageNumber = "page_number"
    }
}