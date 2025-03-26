package mm.pndaza.tipitakamyanmar.fragment

import android.os.Bundle
import android.util.Log
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
import mm.pndaza.tipitakamyanmar.R
import mm.pndaza.tipitakamyanmar.adapter.RecentAdapter
import mm.pndaza.tipitakamyanmar.callback.SwipeToDeleteCallback
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper
import mm.pndaza.tipitakamyanmar.databinding.FragmentRecentBinding
import mm.pndaza.tipitakamyanmar.model.Recent
import mm.pndaza.tipitakamyanmar.repository.RecentRepository
import mm.pndaza.tipitakamyanmar.utils.ActivityUtils
import mm.pndaza.tipitakamyanmar.utils.MDetect
import mm.pndaza.tipitakamyanmar.utils.Rabbit

class RecentFragment : Fragment() {

    private var _binding: FragmentRecentBinding? = null
    private val binding get() = _binding!!
    private lateinit var recentRepository: RecentRepository
    private lateinit var adapter: RecentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recentRepository = RecentRepository(DBOpenHelper.getInstance(requireContext()))
        setupRecyclerView()
        loadRecents()
        setupMenuProvider()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            refreshRecents()
        }
    }
    private fun setupRecyclerView() {
        adapter = RecentAdapter(
            mutableListOf(),
            onItemClick = { recent ->
                ActivityUtils.startReadBookActivity(
                    requireContext(),
                    recent.bookId,
                    recent.pageNumber,
                    ""
                )
            },
        )
        with(binding.listViewRecent) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecentFragment.adapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        ItemTouchHelper(
            SwipeToDeleteCallback(requireContext()) { position ->
                adapter.getRecentAt(position)?.let { recent ->
                    deleteRecent(recent, position)
                }
            }
        ).attachToRecyclerView(binding.listViewRecent)
    }

    private fun loadRecents() {
        val recents = recentRepository.getAll()
        adapter.getData().clear()
        adapter.getData().addAll(recents)
        adapter.notifyDataSetChanged()
        toggleEmptyView(recents.isEmpty())
    }
    private fun refreshRecents() {
        Log.d("recent", "refreshing")
        loadRecents()
    }

    private fun toggleEmptyView(show: Boolean) {
        binding.emptyInfo.text = MDetect.getInstance().getDeviceEncodedText(getString(R.string.recent_empty))
        binding.emptyInfo.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun  deleteRecent(recent: Recent, position: Int){
        recentRepository.remove(recent.bookId)
        adapter.removeItem(position)
    }


    private fun setupMenuProvider() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_recent, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_clearAll -> {
                        clearRecent()
                        true
                    }

                    else -> false
                }
            }
        }
        requireActivity().addMenuProvider(
            menuProvider,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }


    private fun clearRecent() {
        val (message, confirm, cancel) =
            getLocalizedStrings("ကြည့်ဆဲစာရင်းအားလုံး ဖျက်မှာလား", "ဖျက်မယ်", "မဖျက်တော့ဘူး")


        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(confirm) { _, _ ->
                recentRepository.removeAll()
                adapter.getData().clear()
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton(cancel) { _, _ ->
                // do nothing
            }
            .show()
    }


    private fun getLocalizedStrings(
        message: String,
        confirm: String,
        cancel: String
    ): Triple<String, String, String> {

        return if (MDetect.getInstance().isUnicode()) {
            Triple(message, confirm, cancel)
        } else {
            Triple(
                Rabbit.uni2zg(message),
                Rabbit.uni2zg(confirm),
                Rabbit.uni2zg(cancel)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}