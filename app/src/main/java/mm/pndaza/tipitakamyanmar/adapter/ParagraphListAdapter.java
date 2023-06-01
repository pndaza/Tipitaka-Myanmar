package mm.pndaza.tipitakamyanmar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;

public class ParagraphListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Integer> para_list;

    public ParagraphListAdapter(Context context, ArrayList<Integer> list) {

        this.context = context;
        this.para_list = list;
    }


    @Override
    public int getCount() {
        return para_list.size(); //returns total of items in the booksList
    }

    @Override
    public Integer getItem(int position) {
        return para_list.get(position); //returns booksList item at the specified position
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        int baseParagraph = para_list.get(position);
        textView.setText(MDetect.getDeviceEncodedText( "စာပိုဒ် အမှတ် - " + NumberUtil.toMyanmar(baseParagraph)));

        return convertView;
    }

}
