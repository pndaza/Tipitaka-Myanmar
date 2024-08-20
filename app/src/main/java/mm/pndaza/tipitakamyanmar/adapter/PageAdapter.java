package mm.pndaza.tipitakamyanmar.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.activity.ReadBookActivity;
import mm.pndaza.tipitakamyanmar.model.Page;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

public class PageAdapter extends PagerAdapter {

    private static final String TAG = "PageAdapter";
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Page> pages;
    String textToHighlight;
    int pageToHighlight;

    private boolean control_bar_view_state;
    private LinearLayout control_bar;
    private static int fontSize;
    private static String style;
    private final String GOTO_ID = "goto_001";

    public GestureDetectorCompat mDetector;


    public PageAdapter(Context context, ArrayList<Page> pages, String textToHighlight, int pageToHighlight) {
        this.context = context;
        this.pages = pages;
        this.textToHighlight = textToHighlight;
        this.pageToHighlight = pageToHighlight;
        control_bar_view_state = true;
        layoutInflater = LayoutInflater.from(this.context);
        style = getStyle();
        fontSize = SharePref.getInstance(context).getPrefFontSize();
        mDetector = new GestureDetectorCompat(context, new MyGestureListener());
    }

    // Returns the number of pages to be displayed in the ViewPager.
    @Override
    public int getCount() {
        return pages.size();
    }

    // Returns true if a particular object (page) is from a particular page
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // This method should create the page for the given position passed to it as an argument.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate the layout for the page
        View itemView = layoutInflater.inflate(R.layout.page, container, false);
        control_bar = ((ReadBookActivity) context).findViewById(R.id.control_bar);
        String content = pages.get(position).getPageContent();
        int pageNumber = pages.get(position).getPageNumber();
        if (textToHighlight != null && !textToHighlight.isEmpty() && pageToHighlight == pageNumber) {
            content = setHighlight(content, textToHighlight);
        }

        String formattedContent = formatContent(content, style, pageNumber);
        String fontStyle = SharePref.getInstance(context).getPrefFontStyle();
        if (fontStyle.equals("zawgyi")) {
            formattedContent = Rabbit.uni2zg(formattedContent);
        }

        //find and populate data into webview
        WebView webView = itemView.findViewById(R.id.wv_page);
        webView.loadDataWithBaseURL("file:///android_asset/web/",
                formattedContent, "text/html", "UTF-8", null);
        webView.setScrollbarFadingEnabled(true);
        webView.getSettings().setDefaultFontSize(fontSize);
        webView.getSettings().setJavaScriptEnabled(true);
        // Add the page to the container
        container.addView(itemView);
        if (formattedContent.contains(GOTO_ID)) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
//                webView.loadUrl("javascript:scrollAnchor(" + id + ");");
                    webView.loadUrl("javascript:document.getElementById(\"" + GOTO_ID + "\").scrollIntoView()");
                }
            });
        }

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return mDetector.onTouchEvent(motionEvent);
//                return context.onTouchEvent(motionEvent);
            }
        });
        // Return the page
        return itemView;
    }

    // Removes the page from the container for the given position.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private String formatContent(String content, String cssStyle, int pageNumber) {

        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n")
                .append("<head>\n")
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></meta>\n")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n")
                .append("<link rel=\"stylesheet\" href=\"")
                .append(cssStyle)
                .append("\"s>")
                .append("<body>\n")
                .append("<p class=\"pageheader\">")
                .append(NumberUtil.toMyanmar(pageNumber))
                .append("</p>")
                .append(content)
                .append("\n<p>&nbsp;</p>")
                .append("\n</body>\n</html>");

        return sb.toString();
    }

    private String getStyle() {

        SharePref sharePref = SharePref.getInstance(context);
        String theme = sharePref.getPrefNightModeState() ? "night_" : "";
        String fontStyle = sharePref.getPrefFontStyle();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("style_");
        stringBuilder.append(theme);
        stringBuilder.append(fontStyle);
        stringBuilder.append(".css");

        return stringBuilder.toString();
    }

    private void toggleActionBar() {
        if (control_bar_view_state) {
            control_bar_view_state = false;
            ((ReadBookActivity) context).getSupportActionBar().hide();
            control_bar.setVisibility(View.GONE);
        } else {
            control_bar_view_state = true;
            ((ReadBookActivity) context).getSupportActionBar().show();
            control_bar.setVisibility(View.VISIBLE);
        }
    }

    public void updateHighlightedText(String textToHighlight){
        this.textToHighlight = textToHighlight;
    }

    public void  updatePageToHighlight(int pageToHighlight){
        this.pageToHighlight = pageToHighlight;
    }
    private String setHighlight(String content, String textToHighlight) {

        // TODO optimize highlight for some query text
        String highlightedText = "<span class = \"highlight\">" + textToHighlight + "</span>";
        content = content.replace(textToHighlight, highlightedText);
        content = content.replaceFirst(
                "<span class = \"highlight\">", "<span id=\"goto_001\" class=\"highlight\">");
        Log.d("setHighlight: ", content        );
        return content;

    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            toggleActionBar();
            return true;
        }

    }
}
