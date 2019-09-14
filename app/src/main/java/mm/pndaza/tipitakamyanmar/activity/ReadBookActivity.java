package mm.pndaza.tipitakamyanmar.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.sdsmdg.tastytoast.TastyToast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.PageAdapter;
import mm.pndaza.tipitakamyanmar.db.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.fragment.GotoDialogFragment;
import mm.pndaza.tipitakamyanmar.fragment.SettingDialogFragment;
import mm.pndaza.tipitakamyanmar.fragment.TocDialogFragment;
import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.model.Page;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;


public class ReadBookActivity extends AppCompatActivity
        implements GotoDialogFragment.GotoDialogListener, TocDialogFragment.TocDialogListener {

    private DBOpenHelper database = null;
    private ArrayList<Page> listOfPage;
    private PageAdapter pageAdapter;
    private Context context = null;

    private static ViewPager viewPager;
    private static LinearLayout control_bar;
    private static ImageButton btn_goto;
    private static ImageButton btn_toc;
    private static DiscreteSeekBar seekBar;

    private static String bookid;
    private static String bookname;
    private static int firstPage;
    private static int lastPage;
    private static int currentPage;

//    private static final String TAG = "ReadBook";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readbook);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bookid = intent.getStringExtra("bookID");
        currentPage = intent.getIntExtra("currentPage", 0);
        Book book = DBOpenHelper.getInstance(this).getBookInfo(bookid);
        bookname = book.getName();
        firstPage = book.getFirstPage();
        lastPage = book.getLastPage();

        // restore page
        if( savedInstanceState != null){
            currentPage = savedInstanceState.getInt("currentPage");
        }

        //set title
        setTitle(MDetect.getDeviceEncodedText(bookname));

        initView();
        new LoadBook().execute();

        setupGoto();
        setupSeek();
        setupSeekSync();
        setupToc();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentPage", viewPager.getCurrentItem() + 1 );

    }

    private void initView() {

        context = this;
        MDetect.init(context);
        database = DBOpenHelper.getInstance(context);


        viewPager = findViewById(R.id.vpPager);
        control_bar = findViewById(R.id.control_bar);
        btn_goto = findViewById(R.id.btn_goto);
        seekBar = findViewById(R.id.seedbar);
        btn_toc = findViewById(R.id.btn_toc);


    }

    void setupGoto() {

        btn_goto.setOnClickListener(view -> {

            Bundle args = new Bundle();
            args.putInt("firstPage", firstPage);
            args.putInt("lastPage", lastPage);

            Log.d("BookList", "first page is " + firstPage);
            Log.d("BookList", "last page is " + lastPage);

            FragmentManager fm = getSupportFragmentManager();
            GotoDialogFragment gotoDialog = new GotoDialogFragment();
            gotoDialog.setArguments(args);
            gotoDialog.show(fm, "Goto");


        });

    }


    private void setupSeek() {

        seekBar.setMin(firstPage);
        seekBar.setMax(lastPage);
        //if min value is not 1, something wrong with seekbar_progess_indicator
        if(currentPage != 0){
            seekBar.setProgress(currentPage -firstPage);
        } else {
            seekBar.setProgress(firstPage + 1);
            seekBar.setProgress(firstPage);
        }

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {

            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                viewPager.setCurrentItem(seekBar.getProgress()-firstPage);

            }
        });
    }


    private void setupSeekSync(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                seekBar.setProgress( firstPage + i);
                DBOpenHelper.getInstance(context).addToRecent(bookid, i + 1);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private void setupToc(){

        String tocText = DBOpenHelper.getInstance(this).getToc(bookid);
        final ArrayList<String> tocList = new ArrayList<>(Arrays.asList(tocText.split("\n")));

        btn_toc.setOnClickListener(view -> {

            Bundle args = new Bundle();
            args.putStringArrayList("icon_toc", tocList);

            FragmentManager fm = getSupportFragmentManager();
            TocDialogFragment tocDialog = new TocDialogFragment();
            tocDialog.setArguments(args);
            tocDialog.show(fm, "TOC");

        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reading, menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.menu_addBookmark:
                addToBookmark(viewPager.getCurrentItem() + 1);
                break;
            case R.id.menu_copy:
                copyToClipboard();
                break;
            case R.id.menu_setting:
                showSettingDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onSubmitGotoDialog(int input) {

        int page = input;
        viewPager.setCurrentItem(page - firstPage);
    }

    @Override
    public void onTocItemClick(int page) {
        viewPager.setCurrentItem(page-firstPage);

    }


    public class LoadBook extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                loadPages(bookid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            pageAdapter = new PageAdapter(context,listOfPage);
            viewPager.setAdapter(pageAdapter);

            if( currentPage != 0) {
                viewPager.setCurrentItem(currentPage - firstPage);
            } else {
                viewPager.setCurrentItem(0 );
            }
        }
    }

    private void loadPages(String bookId) throws IOException {

        listOfPage = new ArrayList<>();

        String fileName = bookId + ".html";
        String dir = "Books";
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getAssets().open(dir+"/"+fileName), "UTF-8"));

        try {

            int pageNumber = 1;
            String pageContent = "";
            String pageBrakeMark = "--";
            while (true) {
                String strLine = bufferedReader.readLine();
                if (strLine == null) {
                    listOfPage.add(new Page(pageNumber,pageContent));
                    break;
                }
                if(strLine.equals(pageBrakeMark)){
                    listOfPage.add(new Page(pageNumber,pageContent));
                    pageNumber++;
                    pageContent="";
                    strLine = "";
                }
                pageContent+= strLine;
            }

        } finally {
            bufferedReader.close();
        }
    }


    private void addToBookmark(int pageNumber){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);

        String message = "မှတ်လိုသောစာသား ရိုက်ထည့်ပါ။";
        String comfirm = "သိမ်းမယ်";
        String cancel = "မလုပ်တော့ဘူး";
        if (!MDetect.isUnicode()) {
            message = Rabbit.uni2zg(message);
            comfirm = Rabbit.uni2zg(comfirm);
            cancel = Rabbit.uni2zg(cancel);
        }

        dialogBuilder.setMessage(message);
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogBuilder.setMessage(message)
                .setView(input)
                .setCancelable(true)
                .setPositiveButton(comfirm,
                        (dialog, id) -> {
                            String note = input.getText().toString();
                            DBOpenHelper.getInstance(context).
                                    addToBookmark(note, bookid, pageNumber);
                            TastyToast.makeText(context, MDetect.getDeviceEncodedText("သိမ်းမှတ်ပြီးပါပြီ။"),
                                    TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                        })
                .setNegativeButton(cancel, (dialog, id) -> {
                });
        dialogBuilder.show();
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                input.post(() -> {
                    InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                });
            }
        });
        input.requestFocus();

    }
    private void copyToClipboard(){

        String pageContent = listOfPage.get(viewPager.getCurrentItem()).getPageContent();
        if(!MDetect.isUnicode()){
            pageContent = Rabbit.uni2zg(pageContent);
        }
        String simpleText = pageContent.replaceAll("<[^>]*>", "");
        ClipboardManager clipboard= (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newHtmlText("ပိဋက",simpleText,pageContent);
        clipboard.setPrimaryClip(clip);

        TastyToast.makeText(context, MDetect.getDeviceEncodedText("ကော်ပီကူးယူပြီးပါပြီ။"),
                TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();

    }

    private void showSettingDialog(){
        FragmentManager fm = getSupportFragmentManager();
        SettingDialogFragment settingDialog = new SettingDialogFragment();
        settingDialog.show(fm, "Setting");
    }


}
