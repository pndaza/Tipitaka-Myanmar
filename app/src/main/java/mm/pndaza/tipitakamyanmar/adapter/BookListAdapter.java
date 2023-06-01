package mm.pndaza.tipitakamyanmar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.utils.MDetect;

public class BookListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Object> list;

    private static final int BOOK_ITEM = 0;
    private static final int HEADER = 1;

    public BookListAdapter(Context context, ArrayList<Object> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Book) {
            return BOOK_ITEM;
        } else {
            return HEADER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return list.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            switch (getItemViewType(position)) {
                case BOOK_ITEM:
                    convertView = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
                    break;

                case HEADER:
                    convertView = LayoutInflater.from(context).inflate(R.layout.book_list_header, parent, false);
                    break;
            }
        }

        switch (getItemViewType(position)) {
            case BOOK_ITEM:
                // Lookup view for data population
                TextView tvName = convertView.findViewById(R.id.tv_list_item);
                // Populate the data into the template view using the data object
                tvName.setText(MDetect.getDeviceEncodedText(((Book) list.get(position)).getName()));
                break;

            case HEADER:
                // Lookup view for data population
                TextView tvHeader = convertView.findViewById(R.id.tv_list_header);
                // Populate the data into the template view using the data object
                tvHeader.setText(MDetect.getDeviceEncodedText((String) list.get(position)));
                break;
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
