package mm.pndaza.tipitakamyanmar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import mm.pndaza.tipitakamyanmar.databinding.BookmarklistRowItemBinding
import mm.pndaza.tipitakamyanmar.model.Bookmark
import mm.pndaza.tipitakamyanmar.utils.MDetect
import mm.pndaza.tipitakamyanmar.utils.NumberUtil

class BookmarkAdapter(
    private var bookmarks: MutableList<Bookmark>,
    private val onItemClick: (Bookmark) -> Unit,
) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BookmarklistRowItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookmarklistRowItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookmark = bookmarks[position]
        with(holder.binding) {
            tvNote.text = bookmark.note
            tvBookName.text = MDetect.getInstance().getDeviceEncodedText(bookmark.bookName)
            val pageNumber = MDetect.getInstance().getDeviceEncodedText("နှာ - ") +
                    NumberUtil.toMyanmar(bookmark.pageNumber)
            tvPageNumber.text = pageNumber

            root.setOnClickListener { onItemClick(bookmark) }
        }
    }

    override fun getItemCount() = bookmarks.size

    fun getBookmarkAt(position: Int): Bookmark? {
        return bookmarks.getOrNull(position)
    }

    fun removeItemAt(position: Int): Bookmark? {
        if (position in bookmarks.indices) {
            val removeItem = bookmarks.removeAt(position)
            notifyItemRemoved(position)
            return removeItem
        }
        return null
    }

    fun restoreItemAt(bookmark: Bookmark, position: Int) {
        if (position in 0..bookmarks.size) {
            bookmarks.add(position, bookmark)
            notifyItemInserted(position)
        }
    }

    /*
    fun updateBookmarks(newBookmarks: List<Bookmark>) {
        val oldSize = bookmarks.size
        bookmarks.clear()
        bookmarks.addAll(newBookmarks)
        notifyItemRangeChanged(0, maxOf(oldSize, bookmarks.size))
    }
    */

    // Update data using DiffUtil for better performance and animations
    fun submitList(newBookmarks: List<Bookmark>) {
        val diffCallback = BookmarkDiffCallback(this.bookmarks, newBookmarks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        // Important: Update the internal list *after* calculating diff
        this.bookmarks.clear()
        this.bookmarks.addAll(newBookmarks)
        diffResult.dispatchUpdatesTo(this)
    }

    // DiffUtil Callback implementation
    private class BookmarkDiffCallback(
        private val oldList: List<Bookmark>,
        private val newList: List<Bookmark>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        // Check if items represent the same object (unique ID)
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].rowId == newList[newItemPosition].rowId
        }

        // Check if item contents are the same (if IDs match)
        @SuppressLint("DiffUtilEquals") // Using data class equals is usually sufficient
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}