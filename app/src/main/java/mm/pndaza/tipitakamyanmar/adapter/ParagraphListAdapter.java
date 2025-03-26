package mm.pndaza.tipitakamyanmar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Paragraph;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;

public class ParagraphListAdapter extends BaseAdapter {

    final private Context context;
    final private ArrayList<Paragraph> paragraphs;

    public ParagraphListAdapter(Context context, ArrayList<Paragraph> list) {

        this.context = context;
        this.paragraphs = list;
    }


    @Override
    public int getCount() {
        return paragraphs.size(); //returns total of items in the booksList
    }

    @Override
    public Paragraph getItem(int position) {
        return paragraphs.get(position); //returns booksList item at the specified position
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
        Paragraph paragraph = paragraphs.get(position);
        textView.setText(MDetect.getInstance().getDeviceEncodedText( "စာပိုဒ် အမှတ် - " + NumberUtil.toMyanmar(paragraph.number)));

        return convertView;
    }

}
