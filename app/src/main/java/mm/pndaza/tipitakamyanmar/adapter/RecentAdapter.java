package mm.pndaza.tipitakamyanmar.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.model.Recent;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.MyanNumber;


public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private ArrayList<Recent> recentList;
    private View.OnClickListener onClickListener;

    public RecentAdapter(ArrayList<Recent> recentList) {
        this.recentList = recentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View wordListItemView = inflater.inflate(R.layout.recentlist_row_item, parent, false);
        return new ViewHolder(wordListItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Recent recent = recentList.get(position);
        String bookName = MDetect.getDeviceEncodedText(recent.getBookName());
        String pageNumber = MDetect.getDeviceEncodedText("နှာ - ")
                + MyanNumber.toMyanmar(recent.getPageNumber());
        holder.tvBookName.setText(bookName);
        holder.tvPageNumber.setText(pageNumber);
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBookName;
        TextView tvPageNumber;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookName = itemView.findViewById(R.id.tv_bookName);
            tvPageNumber = itemView.findViewById(R.id.tv_pageNumber);

            itemView.setTag(this);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        onClickListener = clickListener;
    }

}
