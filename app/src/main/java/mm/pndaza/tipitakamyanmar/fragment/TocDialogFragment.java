package mm.pndaza.tipitakamyanmar.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.TocAdapter;


public class TocDialogFragment extends DialogFragment {


    private TocDialogListener listener;

    public interface TocDialogListener {

        void onTocItemClick(int page);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().setCanceledOnTouchOutside(true);

        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
        window.getDecorView().setBackgroundResource(android.R.color.transparent);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 90;
        window.setAttributes(params);



        return inflater.inflate(R.layout.dlg_toc, container, false);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof TocDialogListener) {
            listener = (TocDialogListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement GotoDialogFragment.GotoDialogListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float xdpi = metrics.xdpi;
        int deviceWidth = metrics.widthPixels;
        double minScaleFactor = 2.0;
        double maxScaleFactor = 3.0;
        double clearSpace = 0.2;
        double scaleFactor = minScaleFactor;
        while ( minScaleFactor <= maxScaleFactor){
            if((scaleFactor + clearSpace) * xdpi < deviceWidth) {
                scaleFactor = minScaleFactor;
            }
            minScaleFactor += 0.2;
        }

        int dialogWidth = (int)(xdpi * scaleFactor);
        int dialogHeight = (int) (metrics.heightPixels * 0.8 );
        // set width and height for dialog
        params.width = dialogWidth;
        params.height = dialogHeight;
        window.setAttributes(params);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        ArrayList<String> tocList = new ArrayList<>();
        Bundle args = getArguments();
        if ( args != null)
            tocList = args.getStringArrayList("icon_toc");


        Log.v("TOC Dialog", "toclist size is "+ String.valueOf(tocList.size()));
        TocAdapter tocAdapter = new TocAdapter(getContext(), tocList);

        final ListView listView = view.findViewById(R.id.lv_toc);
        listView.setAdapter(tocAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String toc = (String)adapterView.getItemAtPosition(i);
                // split icon_toc string to get page number
                // toc have three parts.
                // type, data and pagenumber
                // they are separated by ->
                // tocstring is like that "subhead->အနုလောမဉာဏကထာ->308"
                int pagenum = Integer.valueOf(toc.split("->")[2]);
                listener.onTocItemClick(pagenum);
                dismiss();


            }
        });

    }

}
