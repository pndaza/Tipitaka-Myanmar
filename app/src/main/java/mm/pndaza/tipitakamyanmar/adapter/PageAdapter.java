package mm.pndaza.tipitakamyanmar.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.activity.ReadBookActivity;
import mm.pndaza.tipitakamyanmar.model.Page;
import mm.pndaza.tipitakamyanmar.utils.NumberUtil;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

public class PageAdapter extends PagerAdapter {

    private static final String TAG = "PageAdapter";
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Page> pages;
    boolean bar_status;
    LinearLayout book_toolbar;

    private static String style;


    public PageAdapter(Context context, ArrayList<Page> pages) {
        this.context = context;
        this.pages = pages;
        bar_status = true;
        layoutInflater = LayoutInflater.from(this.context);
        style = getStyle();
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

        book_toolbar = ((ReadBookActivity) context).findViewById(R.id.control_bar);

        String content = pages.get(position).getPageContent();

        int pagenum = pages.get(position).getPageNumber();
        String formatedContent = formatContent(content, style, pagenum);

        //find and populate data into webview
        WebView webView = itemView.findViewById(R.id.wv_page);
        webView.loadDataWithBaseURL("file:///android_asset/web/",
                formatedContent,"text/html", "UTF-8", null);
        webView.setScrollbarFadingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebViewInterface(), "ReadBookInterface");

        // Add the page to the container
        container.addView(itemView);
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
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></meta>\n" )
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
                .append("<script type = \"text/javascript\" src=\"click.js\"></script>")
                .append("\n</body>\n</html>");

        return sb.toString();
    }

    private String getStyle(){

        SharePref sharePref = SharePref.getInstance(context);
        String fontSize = sharePref.getPrefFontSize();
        boolean nightModeState = sharePref.getPrefNightModeState();

        String cssStyle = "style_" + fontSize + ".css";
        if (nightModeState)
            cssStyle = "style_" + fontSize + "_night.css";


        return cssStyle;
    }

    class WebViewInterface {


        @JavascriptInterface
        public void showHideBars() {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (bar_status) {
                        bar_status = false;
                        ((ReadBookActivity) context).getSupportActionBar().hide();
                        book_toolbar.setVisibility(View.GONE);
                    } else {
                        bar_status = true;
                        ((ReadBookActivity) context).getSupportActionBar().show();
                        book_toolbar.setVisibility(View.VISIBLE);
                    }
                }
            });


        }
    }
}
