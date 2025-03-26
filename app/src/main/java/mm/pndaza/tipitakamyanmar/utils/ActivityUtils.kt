package mm.pndaza.tipitakamyanmar.utils

import android.content.Context
import android.content.Intent
import mm.pndaza.tipitakamyanmar.activity.ReadBookActivity

//@JvmStatic  // Makes the object's members static in Java
object ActivityUtils {
    @JvmStatic  // Makes this method static in Java
    private fun createReadBookIntent(
        context: Context,
        bookID: String,
        pageNumber: Int,
        queryWord: String?
    ): Intent {
        val intent = Intent(context, ReadBookActivity::class.java)
        intent.putExtra("bookID", bookID)
        intent.putExtra("currentPage", pageNumber)
        intent.putExtra("queryWord", queryWord)
        return intent
    }

    @JvmStatic  // Makes this method static in Java
    @JvmOverloads // Generates overloaded methods for optional parameters if needed
    fun startReadBookActivity(
        context: Context,
        bookID: String,
        pageNumber: Int = 0,
        queryWord: String = ""  // Optional parameter with default value
    ) {
        context.startActivity(createReadBookIntent(context, bookID, pageNumber, queryWord))
    }
}