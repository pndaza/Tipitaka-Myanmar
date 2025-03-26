package mm.pndaza.tipitakamyanmar.model

class Bookmark(
    @JvmField val rowId: Int,
    @JvmField val note: String,
    @JvmField val bookID: String,
    @JvmField val bookName: String,
    @JvmField val pageNumber: Int
)
