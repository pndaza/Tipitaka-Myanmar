package mm.pndaza.tipitakamyanmar.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;

public class TocAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> tocList;
    private static final int TOC_ITEM = 0;
    private static final int HEADER = 1;

    public TocAdapter(Context context, ArrayList<String> tocList) {
        this.context = context;
        this.tocList = tocList;
    }

    @Override
    public int getItemViewType(int position) {
        String toc = tocList.get(position);
        // we want to bold heaidng1
        // 1(heading1) will be used as header
        if (toc.startsWith("1")){
            return HEADER;
        } else {
            return TOC_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return tocList.size();
    }

    @Override
    public Object getItem(int i) {
        return tocList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            switch (getItemViewType(position)){
                case TOC_ITEM:
                    convertView = LayoutInflater.from(context).
                            inflate(R.layout.toc_list_item, parent, false);
                    break;
                case HEADER:
                    convertView = LayoutInflater.from(context).
                            inflate(R.layout.toc_list_header, parent, false);
                    break;
            }
        }


        String toc = tocList.get(position);

        // icon_toc have three parts.
        // type, data and pageNumber
        // separated by ->
        // icon_toc string is like that "1->အနုလောမဉာဏကထာ->308"
        String[] tocdata = toc.split("->");
        String toctype = tocdata[0];
        String tocname = tocdata[1];
        if( toctype.equals("2"))
            tocname = "\t\t" + tocname;
        else if ( toctype.equals("3"))
            tocname = "\t\t\t" + tocname;
        //int pagenum = Integer.valueOf(tocdata[2]);


        switch (getItemViewType(position)){
            case TOC_ITEM:
                // get the TextView for item name and item description
                TextView textViewToc = convertView.findViewById(R.id.tv_toc_listitem);
                //set Toc
                textViewToc.setText(tocname);
                break;
            case HEADER:
                // get the TextView for item name and item description
                TextView textViewHeader = convertView.findViewById(R.id.tv_toc_list_header);
                textViewHeader.setTypeface(textViewHeader.getTypeface(), Typeface.BOLD);
                //set toc type 1 as header
                textViewHeader.setText(tocname);
                break;
        }


        // returns the view for the current row
        return convertView;
    }
}
