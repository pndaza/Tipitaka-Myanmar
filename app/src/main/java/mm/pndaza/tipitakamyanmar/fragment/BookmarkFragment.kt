package mm.pndaza.tipitakamyanmar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import mm.pndaza.tipitakamyanmar.R
import mm.pndaza.tipitakamyanmar.adapter.BookmarkAdapter
import mm.pndaza.tipitakamyanmar.callback.SwipeToDeleteCallback
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper
import mm.pndaza.tipitakamyanmar.databinding.FragmentBookmarkBinding
import mm.pndaza.tipitakamyanmar.model.Bookmark
import mm.pndaza.tipitakamyanmar.repository.BookmarkRepository
import mm.pndaza.tipitakamyanmar.utils.ActivityUtils
import mm.pndaza.tipitakamyanmar.utils.MDetect
import mm.pndaza.tipitakamyanmar.utils.Rabbit

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookmarkRepository: BookmarkRepository
    private lateinit var adapter: BookmarkAdapter
    private var recentlyDeletedBookmark: Pair<Bookmark, Int>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents()
        setupRecyclerView()
        loadBookmarks()
        setupMenuProvider()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            recentlyDeletedBookmark
            refreshBookmarks()
        }
    }

    private fun initComponents() {
        bookmarkRepository = BookmarkRepository(DBOpenHelper.getInstance(requireContext()))
    }

    private fun setupRecyclerView() {
        adapter = BookmarkAdapter(
            mutableListOf(),
            onItemClick = { bookmark ->
                ActivityUtils.startReadBookActivity(
                    requireContext(),
                    bookmark.bookID,
                    bookmark.pageNumber
                )
            },
        )

        with(binding.listViewBookmark) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BookmarkFragment.adapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        ItemTouchHelper(
            SwipeToDeleteCallback(requireContext()) { position ->
                adapter.getBookmarkAt(position)?.let { bookmark ->
                    deleteBookmark(bookmark, position)
//                    showDeleteConfirmationDialog(bookmark)
                }
            }
        ).attachToRecyclerView(binding.listViewBookmark)
    }

    private fun loadBookmarks() {
        val bookmarks = bookmarkRepository.getBookmarks()
        adapter.submitList(bookmarks)
//        adapter.updateBookmarks(bookmarks)
        toggleEmptyView(bookmarks.isEmpty())
    }

    private fun refreshBookmarks() {
        loadBookmarks()
    }

    private fun toggleEmptyView(show: Boolean) {
        binding.emptyInfo.text =
            MDetect.getInstance().getDeviceEncodedText(getString(R.string.bookmark_empty))
        binding.emptyInfo.visibility = if (show) View.VISIBLE else View.GONE
        binding.listViewBookmark.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun setupMenuProvider() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_recent, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_clearAll) {
                    showClearAllConfirmationDialog()
                    return true
                }
                return false
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun deleteBookmark(bookmark: Bookmark, position: Int) {
        recentlyDeletedBookmark = Pair(bookmark, position)
        bookmarkRepository.removeFromBookmark(bookmark.rowId)
        adapter.removeItemAt(position)
        toggleEmptyView(adapter.itemCount == 0)
        showUndoSnackbar()
    }

    private fun showUndoSnackbar() {
        val message = getString(R.string.bookmark_deleted)
        val undo = getString(R.string.undo)
        Snackbar.make(
            binding.root,
            MDetect.getInstance().getDeviceEncodedText(message),
            Snackbar.LENGTH_LONG
        )
            .setAction(MDetect.getInstance().getDeviceEncodedText(undo)) {
                recentlyDeletedBookmark?.let { (bookmark, position) ->
                    bookmarkRepository.addToBookmark(
                        bookmark.note,
                        bookmark.bookID,
                        bookmark.pageNumber,
                    )
                    adapter.restoreItemAt(bookmark, position)
                    toggleEmptyView(false)
                }
            }
            .show()
    }

    private fun showClearAllConfirmationDialog() {
        val (message, confirm, cancel) = getDialogTexts("သိမ်းမှတ်ထားသည်များကို ဖျက်မှာလား")

        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(confirm) { _, _ ->
                bookmarkRepository.removeAllBookmarks()
                loadBookmarks()
            }
            .setNegativeButton(cancel) { _, _ -> }
            .show()
    }

    private fun getDialogTexts(message: String): Triple<String, String, String> {
        return if (MDetect.getInstance().isUnicode) {
            Triple(message, "ဖျက်မယ်", "မလုပ်တော့ဘူး")
        } else {
            Triple(
                Rabbit.uni2zg(message),
                Rabbit.uni2zg("ဖျက်မယ်"),
                Rabbit.uni2zg("မလုပ်တော့ဘူး")
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}