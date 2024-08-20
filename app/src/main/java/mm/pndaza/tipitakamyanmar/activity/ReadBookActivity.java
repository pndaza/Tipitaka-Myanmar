package mm.pndaza.tipitakamyanmar.activity;

import android.content.ActivityNotFoundException;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.PageAdapter;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.fragment.ChooseParagraphDialog;
import mm.pndaza.tipitakamyanmar.fragment.GotoDialogFragment;
import mm.pndaza.tipitakamyanmar.fragment.TocBottomSheetDialogFragment;
import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.model.Page;
import mm.pndaza.tipitakamyanmar.model.Toc;
import mm.pndaza.tipitakamyanmar.utils.BookUtil;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;


public class ReadBookActivity extends AppCompatActivity
        implements GotoDialogFragment.GotoDialogListener,
        TocBottomSheetDialogFragment.OnTocItemClickListener,
        ChooseParagraphDialog.OnChooseParagraphListener {

    private Context context;
    private ArrayList<Page> listOfPage = new ArrayList<>();
    private PageAdapter pageAdapter;

    private static ViewPager viewPager;
    private static LinearLayout control_bar;
    private static ImageButton btn_goto;
    private static ImageButton btn_toc;
    private static DiscreteSeekBar seekBar;

    private static String bookID;
    private static String bookName;
    private static int firstPage;
    private static int lastPage;
    private static int currentPage;
    private static String queryWord;
    private static int firstParagraph;
    private static int lastParagraph;
    private Map<Integer, Integer> paragraph_map;
    private static int paragraph;

    private boolean isOpenedByDeeklink = false;
    private static final int PARAGRAPH = 1;
    private static final int LAUNCH_SETTING_ACTIVITY = 2;

    private static final String TAG = "ReadBook";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readbook);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MDetect.init(this);

        Intent intent = getIntent();
        bookID = intent.getStringExtra("bookID");
        currentPage = intent.getIntExtra("currentPage", 0);
        queryWord = intent.getStringExtra("queryWord");
        paragraph = intent.getIntExtra("paragraph", 0);
        isOpenedByDeeklink = intent.getBooleanExtra("deeplink", false);
        // load other book info from database
        Book book = DBOpenHelper.getInstance(this).getBookInfo(bookID);
        bookName = book.getName();
        firstPage = book.getFirstPage();
        lastPage = book.getLastPage();
        loadParagraphs(bookID);

        if (paragraph != 0) {
            currentPage = (int) paragraph_map.get(paragraph);
            // will be use to highlight paragraph
            queryWord = NumberUtil.toMyanmar(paragraph);
        }

        // restore page
        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt("currentPage");
        }

        //set title
        setTitle(MDetect.getDeviceEncodedText(bookName));
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
    public void onBackPressed() {
        if (isOpenedByDeeklink && isTaskRoot()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentPage", viewPager.getCurrentItem() + 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SETTING_ACTIVITY) {
            recreate();
        }
    }

    private void initView() {
        context = this;
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
            args.putInt("firstParagraph", firstParagraph);
            args.putInt("lastParagraph", lastParagraph);

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
        if (currentPage != 0) {
            seekBar.setProgress(currentPage - firstPage);
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
                viewPager.setCurrentItem(seekBar.getProgress() - firstPage);
                currentPage = viewPager.getCurrentItem() + 1;

            }
        });
    }

    private void setupSeekSync() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                seekBar.setProgress(firstPage + i);
                currentPage = i + firstPage;
                DBOpenHelper.getInstance(context).addToRecent(bookID, i + 1);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setupToc() {

        final ArrayList<Toc> tocList = DBOpenHelper.getInstance(this).getToc(bookID);
        btn_toc.setOnClickListener(view -> {

            Bundle args = new Bundle();
            args.putParcelableArrayList("toc_list", tocList);

            FragmentManager fm = getSupportFragmentManager();
            TocBottomSheetDialogFragment tocDialog = new TocBottomSheetDialogFragment();
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

        switch (item.getItemId()) {
            case R.id.menu_pali_book:
                openPaliBook();
                break;
            case R.id.menu_addBookmark:
                addToBookmark(viewPager.getCurrentItem() + 1);
                break;
            case R.id.menu_copy:
                copyToClipboard();
                break;
            case R.id.menu_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivityForResult(intent, LAUNCH_SETTING_ACTIVITY);
//                showSettingDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onSubmitGotoDialog(int input, int type) {

        int page = input;
        if (type == PARAGRAPH) {
            page = paragraph_map.get(input);
        }
        viewPager.setCurrentItem(page - firstPage);
        currentPage = page;
    }

    @Override
    public void onTocItemClick(int page, String tocName) {
        String textToHighlight = tocName;
        textToHighlight = textToHighlight.replaceAll("[၀-၉]+။ ", "");
        pageAdapter.updatePageToHighlight(page);
        pageAdapter.updateHighlightedText(textToHighlight);
        Log.d(TAG, "onTocItemClick: " + textToHighlight);
        viewPager.setCurrentItem(page - firstPage);

    }

    @Override
    public void onChooseParagraph(int paragraph) {
        Bundle bundle = new Bundle();
        bundle.putString("book_id", DBOpenHelper.getInstance(this).getPaliBookID(bookID));
        bundle.putInt("paragraph_number", paragraph);
        Intent intent = new Intent("mm.pndaza.tipitakapali.BookReaderActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showNoPaliBook();
        }

    }

    private class LoadBook extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                listOfPage = BookUtil.read(context, bookID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //highlight page
            if (queryWord != null) {
                if (queryWord.length() > 0) {
                    String pageContent = listOfPage.get(currentPage - 1).getPageContent();
                    pageContent = pageContent.replaceAll(
                            queryWord, "<span class=\"highlight\">" + queryWord + "</span>");
                    pageContent = pageContent.replace(
                            "<span class=\"highlight\">", "<span id=\"goto_001\" class=\"highlight\">");
                    listOfPage.set(currentPage - 1, new Page(currentPage, pageContent));
                }
            }
            pageAdapter = new PageAdapter(context, listOfPage, queryWord, currentPage - 1);
            viewPager.setAdapter(pageAdapter);
            if (currentPage != 0) {
                viewPager.setCurrentItem(currentPage - firstPage);
            } else {
                viewPager.setCurrentItem(0);
            }

        }
    }

    private void openPaliBook() {
        Log.d(TAG, "openPaliBook: current page: " + currentPage);
        ArrayList<Integer> paragraphs = getParagraphs(currentPage);
        boolean isResultFromPreviousPage = false;
        if (paragraphs.isEmpty()) {
            // finding nearest paragraphs
            int previousPage = currentPage - 1;
            while (previousPage >= firstPage) {
                paragraphs = DBOpenHelper.getInstance(this).getParagraphs(bookID, previousPage);
                if (!paragraphs.isEmpty()) {
                    isResultFromPreviousPage = true;
                    break;
                }
                previousPage--;
            }
        }
        // first paragraph
        if (paragraphs.isEmpty()) {
            int firstParagraph = DBOpenHelper.getInstance(this).getFirstParagraph(bookID);
            if (firstParagraph > 0) {
                paragraphs = getParagraphs(firstParagraph);
            }
        }

        String paliBookID = DBOpenHelper.getInstance(this).getPaliBookID(bookID);
        Bundle bundle = new Bundle();
        bundle.putString("book_id", paliBookID);
        bundle.putIntegerArrayList("paragraphs", paragraphs);

        if (isResultFromPreviousPage) {
            bundle.putBoolean("is_from_previous_page", true);
        } else {
            bundle.putBoolean("is_from_previous_page", false);

        }

        FragmentManager fm = getSupportFragmentManager();
        ChooseParagraphDialog dialog = new ChooseParagraphDialog();
        dialog.setArguments(bundle);
        dialog.show(fm, "chooseParagraph");

    }

    private ArrayList<Integer> getParagraphs(int pageNumber) {
        /*
        String pageContent = listOfPage.get(viewPager.getCurrentItem()).getPageContent();
        final String regexParagraph = "<span class=\"paragraph\">([၀-၉]+)</span>";
        final Matcher m = Pattern.compile(regexParagraph).matcher(pageContent);
        while (m.find()) {
            paragraphs.add(NumberUtil.toEnglish(m.group(1)));
        }
        */
        return DBOpenHelper.getInstance(this).getParagraphs(bookID, pageNumber);
    }


    private void addToBookmark(int pageNumber) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);

        String message = "မှတ်လိုသောစာသား ရိုက်ထည့်ပါ။";
        String confirm = "သိမ်းမယ်";
        String cancel = "မသိမ်းတော့ဘူး";
        if (!MDetect.isUnicode()) {
            message = Rabbit.uni2zg(message);
            confirm = Rabbit.uni2zg(confirm);
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
                .setPositiveButton(confirm,
                        (dialog, id) -> {
                            String note = input.getText().toString();
                            DBOpenHelper.getInstance(context).
                                    addToBookmark(note, bookID, pageNumber);

                            showSnackbar(MDetect.getDeviceEncodedText("သိမ်းမှတ်ပြီးပါပြီ။"));

                        })
                .setNegativeButton(cancel, (dialog, id) -> {
                });
        dialogBuilder.show();

        input.setOnFocusChangeListener((v, hasFocus) -> input.post(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        }));
        input.requestFocus();

    }

    private void copyToClipboard() {

        String pageContent = listOfPage.get(viewPager.getCurrentItem()).getPageContent();
        if (!MDetect.isUnicode()) {
            pageContent = Rabbit.uni2zg(pageContent);
        }
        String simpleText = pageContent.replaceAll("<[^>]*>", "");
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ပိဋက", simpleText);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }

        showSnackbar(MDetect.getDeviceEncodedText("ကော်ပီကူးယူပြီးပါပြီ။"));
    }

    private void loadParagraphs(String bookID) {
//        Log.d(TAG, "loadParagraphs: " + bookid);
        paragraph_map = DBOpenHelper.getInstance(this).getParagraphs(bookID);
//        Log.d(TAG, "loadParagraphs: " + paragraph_map.size());
        if (paragraph_map.isEmpty()) {
            firstParagraph = 0;
            lastParagraph = 0;
        } else {
            firstParagraph = (int) Collections.min(paragraph_map.keySet());
            lastParagraph = (int) Collections.max(paragraph_map.keySet());
        }
    }

    private void showNoPaliBook() {
        new AlertDialog.Builder(this)
                .setMessage(MDetect.getDeviceEncodedText("တိပိဋကပါဠိ ဆော့ဝဲလ် ထည့်သွင်းရန် လိုအပ်ပါသည်။"))
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("OK", null)
                .show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(viewPager, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
