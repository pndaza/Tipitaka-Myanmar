package mm.pndaza.tipitakamyanmar.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.model.Toc;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class TocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Toc> tocList;
    private static final int TOC_LEVEL_1 = 1;
    private static final int TOC_LEVEL_2 = 2;
    private static final int TOC_LEVEL_3 = 3;
    private static final int TOC_LEVEL_4 = 0;

    private OnItemClickListener onItemClickListener;

    public TocAdapter(ArrayList<Toc> tocList, OnItemClickListener onItemClickListener) {
        this.tocList = tocList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        Toc toc = tocList.get(position);
        // we want to bold heading 1
        // 1(heading1) will be used as header
        switch (toc.getType()){
            case "1": return TOC_LEVEL_1;
            case "2": return TOC_LEVEL_2;
            case "3": return TOC_LEVEL_3;
            default: return TOC_LEVEL_4;
        }
    }

    @Override
    public int getItemCount() {
        return tocList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TOC_LEVEL_1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.toc_level_1, parent, false);
                return new ViewHolderTocLevel1(view);
            case TOC_LEVEL_2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.toc_level_2, parent, false);
                return new ViewHolderTocLevel2(view);
            case TOC_LEVEL_3:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.toc_level_3, parent, false);
                return new ViewHolderTocLevel3(view);
            case TOC_LEVEL_4:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.toc_level_4, parent, false);
                return new ViewHolderTocLevel4(view);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderTocLevel1) {
            ((ViewHolderTocLevel1) holder).tv_name.setText(MDetect.getDeviceEncodedText(tocList.get(position).getName()));
        } else if (holder instanceof ViewHolderTocLevel2) {
            ((ViewHolderTocLevel2) holder).tv_name.setText(MDetect.getDeviceEncodedText(tocList.get(position).getName()));
        } else if (holder instanceof ViewHolderTocLevel3) {
            ((ViewHolderTocLevel3) holder).tv_name.setText(MDetect.getDeviceEncodedText(tocList.get(position).getName()));
        } else {
            ((ViewHolderTocLevel4) holder).tv_name.setText(MDetect.getDeviceEncodedText(tocList.get(position).getName()));
        }
    }

    class ViewHolderTocLevel1 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;
        public ViewHolderTocLevel1(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_name.setTypeface( Typeface.create(tv_name.getTypeface(), Typeface.BOLD));
            tv_name.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(tocList.get(getAdapterPosition()));
        }
    }

    class ViewHolderTocLevel2 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;
        public ViewHolderTocLevel2(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_name.setTypeface( tv_name.getTypeface(), Typeface.BOLD);
            tv_name.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(tocList.get(getAdapterPosition()));
        }
    }

    class ViewHolderTocLevel3 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;
        public ViewHolderTocLevel3(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
//            tv_name.setTypeface( null, Typeface.BOLD);
            tv_name.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(tocList.get(getAdapterPosition()));
        }
    }

    class ViewHolderTocLevel4 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;
        public ViewHolderTocLevel4(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
//            tv_name.setTypeface( null, Typeface.BOLD);
            tv_name.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(tocList.get(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Toc toc);
    }
}
