package mm.pndaza.tipitakamyanmar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.model.Sutta;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;

public class SuttaListAdapter extends RecyclerView.Adapter<SuttaListAdapter.ViewHolder> {
    private final OnItemClickListener onItemClickListener;
    private ArrayList<Sutta> suttas;
    private String filterText = "";
    private Context context;

    public SuttaListAdapter(ArrayList<Sutta> suttas, OnItemClickListener onItemClickListener) {
        this.suttas = suttas;
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public int getItemViewType(int position) {
        //default
        return position;
    }


    @Override
    public int getItemCount() {
        return suttas.size();
    }

    public void setFilteredWordList(ArrayList<Sutta> filteredSutta){
        suttas = filteredSutta;
        notifyDataSetChanged();
    }

    public void setFilterText(String filterText){
        this.filterText = filterText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sutta_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

Sutta sutta = suttas.get(position);
String book_and_page = sutta.getBookName() + " - " + NumberUtil.toMyanmar(sutta.getPageNumber());
if(filterText.isEmpty()){
        holder.tv_sutta_naame.setText(sutta.getName());

} else{
    holder.tv_sutta_naame.setText(setHighlight(sutta.getName()));
}

        holder.tv_book_and_page.setText(book_and_page);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tv_sutta_naame;
        public TextView tv_book_and_page;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_sutta_naame = itemView.findViewById(R.id.tv_sutta_name);
            tv_book_and_page = itemView.findViewById(R.id.tv_book_and_page);

            itemView.setTag(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(suttas.get(getAdapterPosition()));
        }
    }

    private SpannableString setHighlight(String word){

        SpannableString highlightedText = new SpannableString(word);
        int start_index = word.indexOf(filterText);
        int end_index = start_index + filterText.length();

        highlightedText.setSpan(
                new ForegroundColorSpan(Color.RED), start_index, end_index,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // set background color for query words

//        highlightedText.setSpan(
//                new BackgroundColorSpan(Color.YELLOW), start_index, end_index,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        );
        return highlightedText;
    }

    public interface OnItemClickListener {
        void onItemClick(Sutta sutta);
    }
}
