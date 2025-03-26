package mm.pndaza.tipitakamyanmar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import mm.pndaza.tipitakamyanmar.model.SearchResult;
import mm.pndaza.tipitakamyanmar.utils.FontCache;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;
import mm.pndaza.tipitakamyanmar.utils.SharePref;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<SearchResult> searchResultResults;
    private String queryWord;
    private View.OnClickListener onClickListener;
    private Typeface typeface;

    private Context context;

    public SearchAdapter(ArrayList<SearchResult> searchResultResults, String queryWord) {
        this.searchResultResults = searchResultResults;
        this.queryWord = queryWord;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if(SharePref.getInstance(context).getPrefFontStyle().equals("unicode")) {
            typeface = FontCache.getUnicodeTypeface(context);
        } else {
            typeface = FontCache.getZawgyiTypeface(context);
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        // reuse layout because layout are same
        View wordListItemView = inflater.inflate(R.layout.searchlist_row_item, parent, false);
        return new ViewHolder(wordListItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SearchResult searchResult = searchResultResults.get(position);
        String bookAndPage = searchResult.bookName() + " - နှာ " + NumberUtil.toMyanmar(searchResult.pageNumber());
        holder.tvBookAndPage.setText(MDetect.getInstance().getDeviceEncodedText(bookAndPage));
        holder.tvBrief.setText(getHighLightedString(searchResult.brief(),queryWord));
    }

    @Override
    public int getItemCount() {
        return searchResultResults.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBookAndPage;
        TextView tvBrief;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookAndPage = itemView.findViewById(R.id.tv_bookandpage);
            tvBrief = itemView.findViewById(R.id.tv_brief);
            if(typeface != null){
            tvBrief.setTypeface(typeface);
            tvBrief.setLineSpacing(0.0f, 0.9f);
            }

            itemView.setTag(this);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        onClickListener = clickListener;
    }

    private SpannableString getHighLightedString(String brief, String query) {

        if(!SharePref.getInstance(context).getPrefFontStyle().equals("unicode")) {
        brief = Rabbit.uni2zg(brief);
        }
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
                    new BackgroundColorSpan(0x99FF00FF), start_index, end_index,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return highlightedText;
    }

}
