package mm.pndaza.tipitakamyanmar.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.adapter.PageAdapter;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.fragment.GotoPaliAppDialog;
import mm.pndaza.tipitakamyanmar.fragment.GotoDialogFragment;
import mm.pndaza.tipitakamyanmar.fragment.TocBottomSheetDialogFragment;
import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.model.Page;
import mm.pndaza.tipitakamyanmar.model.Paragraph;
import mm.pndaza.tipitakamyanmar.model.Toc;
import mm.pndaza.tipitakamyanmar.repository.BookRepository;
import mm.pndaza.tipitakamyanmar.repository.BookmarkRepository;
import mm.pndaza.tipitakamyanmar.repository.PageMapRepository;
import mm.pndaza.tipitakamyanmar.repository.ParagraphRepository;
import mm.pndaza.tipitakamyanmar.repository.RecentRepository;
import mm.pndaza.tipitakamyanmar.repository.SourceBookRepository;
import mm.pndaza.tipitakamyanmar.repository.TocRepository;
import mm.pndaza.tipitakamyanmar.utils.BookUtil;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;
import mm.pndaza.tipitakamyanmar.utils.SharePref;


public class ReadBookActivity extends AppCompatActivity
        implements GotoDialogFragment.GotoDialogListener,
        TocBottomSheetDialogFragment.OnTocItemClickListener,
        GotoPaliAppDialog.Listener {

    //    private Context context;
    private List<Page> listOfPage = new ArrayList<>();
    private PageAdapter pageAdapter;

    private ViewPager viewPager;
    private ImageButton btn_goto;
    private ImageButton btn_toc;
    private DiscreteSeekBar seekBar;

    private String bookID;
    private String bookName;
    private int firstPage;
    private int lastPage;
    private int currentPage = 0;
    private String queryWord = "";
    private int firstParagraph;
    private int lastParagraph;
    private static int paragraph;
    private int paragraphIndex;

    private boolean isOpenedByDeepLink = false;
    private static final int PARAGRAPH = 1;
    private static final int LAUNCH_SETTING_ACTIVITY = 2;

    private static final String TAG = "ReadBook";

    private BookRepository bookRepository;
    private RecentRepository recentRepository;
    private BookmarkRepository bookmarkRepository;
    private TocRepository tocRepository;
    private ParagraphRepository paragraphRepository;
    private SourceBookRepository sourceBookRepository;
    private PageMapRepository pageMapRepository;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (SharePref.getInstance(this).getPrefNightModeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readbook);

        DBOpenHelper dbOpenHelper = DBOpenHelper.getInstance(this);
        initializeRepositories(dbOpenHelper);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MDetect.init(this);

        handleIntent(getIntent());
        loadBookInfo(savedInstanceState);
        initializeViews();
        loadBook(this);
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
        if (isOpenedByDeepLink && isTaskRoot()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentPage", viewPager.getCurrentItem() + firstPage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SETTING_ACTIVITY) {
            recreate();
        }
    }

    private void initializeRepositories(DBOpenHelper dbOpenHelper) {
        bookRepository = new BookRepository(dbOpenHelper);
        bookmarkRepository = new BookmarkRepository(dbOpenHelper);
        recentRepository = new RecentRepository(dbOpenHelper);
        tocRepository = new TocRepository(dbOpenHelper);
        paragraphRepository = new ParagraphRepository(dbOpenHelper);
        sourceBookRepository = new SourceBookRepository(dbOpenHelper);
        pageMapRepository = new PageMapRepository(dbOpenHelper);
    }

    private void handleIntent(Intent intent) {
        if (intent.getData() != null) {
            handleDeepLink(intent);
        } else if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            bookID = bundle.getString("bookID");
            currentPage = bundle.getInt("currentPage", 0);
            queryWord = bundle.getString("queryWord");
            paragraph = bundle.getInt("paragraph", 0);
            paragraphIndex = bundle.getInt("paragraphIndex", 0);
            Log.d(TAG, "onCreate: Paragraph from tipi app: " + paragraph);
            Log.d(TAG, "onCreate: Paragraph index from tipi app: " + paragraphIndex);
            isOpenedByDeepLink = bundle.getBoolean("deeplink", false);
        }
    }

    private void loadBookInfo(Bundle savedInstanceState) {
        Book book = bookRepository.getBookInfo(bookID);
        bookName = book.getName();
        firstPage = book.getFirstPage();
        lastPage = book.getLastPage();

        if (currentPage == 0) {
            currentPage = firstPage;
        }

        if (paragraph != 0) {
            currentPage = getPageNumber(paragraph, paragraphIndex);
            queryWord = NumberUtil.toMyanmar(paragraph);
        }

        firstParagraph = paragraphRepository.getFirstParagraph(bookID);
        lastParagraph = paragraphRepository.getLastParagraph(bookID);

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt("currentPage");
        }

        setTitle(MDetect.getDeviceEncodedText(bookName));
    }

    private void initializeViews() {
//        context = this;
        viewPager = findViewById(R.id.vpPager);
        LinearLayout control_bar = findViewById(R.id.control_bar);
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
            seekBar.setProgress(currentIndex());
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
                currentPage = viewPager.getCurrentItem() + firstPage;

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
                recentRepository.addToRecent(bookID, currentPage);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setupToc() {

        final ArrayList<Toc> tocList = tocRepository.getToc(bookID);
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

        if (item.getItemId() == R.id.menu_pali_book) {
            openPaliBook();
        } else if (item.getItemId() == R.id.menu_addBookmark) {
            addToBookmark(viewPager.getCurrentItem() + firstPage);
        } else if (item.getItemId() == R.id.menu_copy) {
            copyToClipboard();
        } else if (item.getItemId() == R.id.menu_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivityForResult(intent, LAUNCH_SETTING_ACTIVITY);
//                showSettingDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onNavigateToPage(int pageNumber) {
        currentPage = pageNumber;
        viewPager.setCurrentItem(pageNumber - firstPage);
    }

    @Override
    public void onNavigateToParagraph(int paragraphNumber) {
        currentPage = paragraphRepository.getPageNumber(bookID, paragraphNumber);
        String queryWord = NumberUtil.toMyanmar(paragraphNumber);
        pageAdapter.updateHighlightedText(queryWord);
        pageAdapter.updatePageToHighlight(currentPage);
        viewPager.setCurrentItem(currentPage - firstPage);
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
    public void onParagraphSelected(Paragraph paragraph) {
        String paliBookId = sourceBookRepository.getPaliBookID(bookID, currentPage);

        // launch using custom url scheme
        String scheme = "tipitakapali";
        String host = "mm.pndaza.tipitakapali";
        String path = "open";
        Uri deepLinkUri = new Uri.Builder()
                .scheme(scheme)
                .authority(host)
                .path(path)
                .appendQueryParameter("id", paliBookId)
                .appendQueryParameter("paragraph", String.valueOf(paragraph.number))
                .appendQueryParameter("paragraph_index", String.valueOf(paragraph.index))
                .build();
        Log.d(TAG, "uri: " + deepLinkUri.toString());
        Intent deepLinktIntent = new Intent(Intent.ACTION_VIEW, deepLinkUri);
        deepLinktIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (deepLinktIntent.resolveActivity(getPackageManager()) != null) {
            this.startActivity(deepLinktIntent);
        } else {
            Log.d("Exception", "No app found to handle this deep link");
            showNoPaliBook();
        }
        /*
        Bundle bundle = new Bundle();
        bundle.putString("book_id", paliBookId);
        bundle.putInt("paragraph_number", paragraph);
        Intent intent = new Intent("mm.pndaza.tipitakapali.BookReaderActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showNoPaliBook();
        }
        */

    }

    private void loadBook(Context context) {
        executorService.execute(() -> {
            try {
                listOfPage = BookUtil.readBook(context, bookID, firstPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
//highlight page
                if (queryWord != null) {
                    Log.d(TAG, "query word: " + queryWord);
                    Log.d(TAG, "current page: " + currentPage);
                    Log.d(TAG, "total pages: " + listOfPage.size());
                    if (!queryWord.isEmpty() && currentPage != 0) {
                        String pageContent = listOfPage.get(currentIndex()).getPageContent();
                        pageContent = pageContent.replaceAll(
                                queryWord, "<span class=\"highlight\">" + queryWord + "</span>");
                        pageContent = pageContent.replace(
                                "<span class=\"highlight\">", "<span id=\"goto_001\" class=\"highlight\">");
                        listOfPage.set(currentIndex(), new Page(currentPage, pageContent));
                    }
                }
                pageAdapter = new PageAdapter(context, listOfPage, queryWord, currentIndex());
                viewPager.setAdapter(pageAdapter);
                if (currentPage != 0) {
                    viewPager.setCurrentItem(currentIndex());
                } else {
                    viewPager.setCurrentItem(0);
                }
            });

        });
    }


    private void openPaliBook() {
        if (bookID.equals("08_visuddhimagga_00")) {
            showAlertDialog("မူရင်းပါဠိရှိသော စာအုပ်မဟုတ်ပါ။");
            return;
        }
        if (bookID.equals("08_jataka_06") || bookID.equals("08_jataka_07")) {
            showAlertDialog("အချက်အလက်များ မလုပ်ရသေး၍\nပါဠိဆော့ဝဲနှင့် ချိတ်ဆက်မှု မပြုနိုင်ပါ။");
            return;
        }
        String linkType = sourceBookRepository.getLinkType(bookID);
        if (linkType.equals("page")) {
            int paliPageNumber = pageMapRepository.getPaliPageNumber(bookID, currentPage);
            if(paliPageNumber != 0){
                String paliBookId = sourceBookRepository.getPaliBookID(bookID, currentPage);
                // launch using custom url scheme
                String scheme = "tipitakapali";
                String host = "mm.pndaza.tipitakapali";
                String path = "open";
                Uri deepLinkUri = new Uri.Builder()
                        .scheme(scheme)
                        .authority(host)
                        .path(path)
                        .appendQueryParameter("id", paliBookId)
                        .appendQueryParameter("page", String.valueOf(paliPageNumber))
                        .build();
                Log.d(TAG, "uri: " + deepLinkUri.toString());
                Intent deepLinktIntent = new Intent(Intent.ACTION_VIEW, deepLinkUri);
                deepLinktIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (deepLinktIntent.resolveActivity(getPackageManager()) != null) {
                    this.startActivity(deepLinktIntent);
                } else {
                    Log.d("Exception", "No app found to handle this deep link");
                    showNoPaliBook();
                }
            }
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("book_id", bookID);
        bundle.putInt("page_number", currentPage);

        FragmentManager fm = getSupportFragmentManager();
        GotoPaliAppDialog dialog = new GotoPaliAppDialog();
        dialog.setArguments(bundle);
        dialog.show(fm, "chooseParagraph");

    }

    private void addToBookmark(int pageNumber) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);

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
                            bookmarkRepository.
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

/*
    private void loadParagraphs(String bookID) {
//        Log.d(TAG, "loadParagraphs: " + bookid);
        paragraphs = paragraphRepository.getParagraphs(bookID);
//        Log.d(TAG, "loadParagraphs: " + paragraph_map.size());
        if (paragraphs.isEmpty()) {
            firstParagraph = 0;
            lastParagraph = 0;
        } else {
            firstParagraph = (int) Collections.min(paragraphs.keySet());
            lastParagraph = (int) Collections.max(paragraphs.keySet());
        }
    }
    */

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(MDetect.getDeviceEncodedText(message))
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton("Ok", null)
                .show();
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

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();

        if (data != null) {
//            String scheme = data.getScheme();
//            String host = data.getHost();
//            String path = data.getPath();

            // Parse the query parameters
            bookID = data.getQueryParameter("id");
            String paragraphStr = data.getQueryParameter("paragraph");
            String paragraphIndexStr = data.getQueryParameter("paragraph_index");
            String pageNumber = data.getQueryParameter("page");
            if (paragraphStr != null && paragraphIndexStr != null) {
                try {

                    paragraph = Integer.parseInt(paragraphStr);
                    paragraphIndex = Integer.parseInt(paragraphIndexStr);
                    Log.d("onCreate:", "from deeplink- " + "bookId: " + bookID + " paragraph: " + paragraph + " paragraphIndex: " + paragraphIndex);
                } catch (NumberFormatException e) {
                    Log.e("DeepLink", "Invalid paragraph number: " + paragraphStr, e);
                    // Handle the error (e.g., show an error message to the user)
                }
            }
            else if( pageNumber != null){
                try {
                    currentPage = Integer.parseInt(pageNumber);
                } catch (NumberFormatException e){
                    Log.e("DeepLink", "Invalid page number: " + paragraphStr, e);
                }
            }
            else {
                Log.e("DeepLink", "Invalid deep link parameters");
                // Handle the error (e.g., show an error message to the user)
            }

        }
    }

    int getPageNumber(int paragraph, int paragraphIndex) {
        return paragraphRepository.getPageNumber(bookID, paragraph, paragraphIndex);
    }

    private int currentIndex() {
        return currentPage - firstPage;
    }
}
