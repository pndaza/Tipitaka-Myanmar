package mm.pndaza.tipitakamyanmar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.db.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Bookmark;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.MyanNumber;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;


public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private ArrayList<Bookmark> bookmarkList;
    private View.OnClickListener onClickListener;

    private Context context;

    public BookmarkAdapter(ArrayList<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // reuse layout because layout are same
        View wordListItemView = inflater.inflate(R.layout.bookmarklist_row_item, parent, false);
        return new ViewHolder(wordListItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Bookmark bookmark = bookmarkList.get(position);
        holder.tvNote.setText(bookmark.getNote()); // note are saved as device encoding
        holder.tvBookName.setText(MDetect.getDeviceEncodedText(bookmark.getBookName()));
        String pageNumber = MDetect.getDeviceEncodedText("နှာ - ") + MyanNumber.toMyanmar(bookmark.getPageNumber());
        holder.tvPageNumber.setText(pageNumber);
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNote;
        TextView tvBookName;
        TextView tvPageNumber;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNote = itemView.findViewById(R.id.tv_note);
            tvBookName = itemView.findViewById(R.id.tv_bookName);
            tvPageNumber = itemView.findViewById(R.id.tv_pageNumber);

            itemView.setTag(this);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        onClickListener = clickListener;
    }


    public void deleteItem(int position){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        String message = "သိမ်းမှတ်ထားသည်ကို ဖျက်မှာလား";
        String comfirm = "ဖျက်မယ်";
        String cancel = "မလုပ်တော့ဘူး";
        if (!MDetect.isUnicode()) {
            message = Rabbit.uni2zg(message);
            comfirm = Rabbit.uni2zg(comfirm);
            cancel = Rabbit.uni2zg(cancel);
        }

        alertDialog.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(comfirm,
                        (dialog, id) -> {
                            DBOpenHelper.getInstance(context).removeFromBookmark(position);
                            bookmarkList.remove(position);
                            notifyDataSetChanged();
                            TastyToast.makeText(context,
                                    MDetect.getDeviceEncodedText("သိမ်းထားသည်ကို ဖျက်လိုက်ပြီးပါပြီ"),
                                    TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                        })
                .setNegativeButton(cancel, (dialog, id) -> {
                    notifyDataSetChanged();
                });
        alertDialog.show();
    }

}
