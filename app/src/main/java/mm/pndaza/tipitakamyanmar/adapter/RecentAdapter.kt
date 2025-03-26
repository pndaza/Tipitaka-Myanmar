package mm.pndaza.tipitakamyanmar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mm.pndaza.tipitakamyanmar.databinding.RecentlistRowItemBinding
import mm.pndaza.tipitakamyanmar.model.Recent
import mm.pndaza.tipitakamyanmar.utils.MDetect
import mm.pndaza.tipitakamyanmar.utils.NumberUtil

class RecentAdapter(
    private var recents: MutableList<Recent>,
    private val onItemClick: (Recent) -> Unit,
) :
    RecyclerView.Adapter<RecentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecentlistRowItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecentlistRowItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recent = recents[position]
        with(holder.binding) {
            tvBookName.text = MDetect.getInstance().getDeviceEncodedText(recent.bookName)
            val pageNumber = MDetect.getInstance().getDeviceEncodedText("နှာ - ") +
                    NumberUtil.toMyanmar(recent.pageNumber)
            tvPageNumber.text = pageNumber
            root.setOnClickListener { onItemClick(recent) }
        }
    }

    override fun getItemCount() = recents.size

    fun getRecentAt(position: Int): Recent? {
        return recents.getOrNull(position)
    }

    fun removeItem(position: Int): Recent? {
        if (position !in recents.indices) return null
        val recent = recents.removeAt(position)
        notifyItemRemoved(position)
        return recent
    }

    /*
    fun restoreItem(position: Int, recent: Recent) {
        if (position >= 0) {
        recents.add(position, recent)
        notifyItemInserted(position)
        }
    }
    */

    fun getData(): MutableList<Recent> {
        return recents
    }

}
