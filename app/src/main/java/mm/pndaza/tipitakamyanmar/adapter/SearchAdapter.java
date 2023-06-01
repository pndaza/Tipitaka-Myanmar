package mm.pndaza.tipitakamyanmar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.model.Search;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<Search> searchResults;
    private String queryWord;
    private View.OnClickListener onClickListener;

    private Context context;

    public SearchAdapter(ArrayList<Search> searchResults, String queryWord) {
        this.searchResults = searchResults;
        this.queryWord = queryWord;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // reuse layout because layout are same
        View wordListItemView = inflater.inflate(R.layout.searchlist_row_item, parent, false);
        return new ViewHolder(wordListItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Search search = searchResults.get(position);
        String bookAndPage = search.getBookName() + " - နှာ " + NumberUtil.toMyanmar(search.getPageNumber());
        holder.tvBookAndPage.setText(MDetect.getDeviceEncodedText(bookAndPage));
        holder.tvBrief.setText(getHighLightedString(search.getBrief(),queryWord));
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBookAndPage;
        TextView tvBrief;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookAndPage = itemView.findViewById(R.id.tv_bookandpage);
            tvBrief = itemView.findViewById(R.id.tv_brief);

            itemView.setTag(this);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        onClickListener = clickListener;
    }

    private SpannableString getHighLightedString(String brief, String query) {
        brief = MDetect.getDeviceEncodedText(brief);
        query = MDetect.getDeviceEncodedText(query);
        int start_index = brief.indexOf(query);
        int end_index = start_index + query.length();
        SpannableString highlightedText = new SpannableString(brief);
        // highlight query words
        // set foreground color for query words
        if (start_index != -1) {
            highlightedText.setSpan(
                    new ForegroundColorSpan(Color.WHITE), start_index, end_index,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            // set background color for query words
            highlightedText.setSpan(
                    new BackgroundColorSpan(Color.MAGENTA), start_index, end_index,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return highlightedText;
    }

}
