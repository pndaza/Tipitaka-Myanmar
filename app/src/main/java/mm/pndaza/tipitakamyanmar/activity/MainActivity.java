package mm.pndaza.tipitakamyanmar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.fragment.BookmarkFragment;
import mm.pndaza.tipitakamyanmar.fragment.HomeFragment;
import mm.pndaza.tipitakamyanmar.fragment.RecentFragment;
import mm.pndaza.tipitakamyanmar.fragment.SearchFragment;
import mm.pndaza.tipitakamyanmar.fragment.SuttaDialogFragment;
import mm.pndaza.tipitakamyanmar.model.Sutta;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.SharePref;

public class MainActivity extends AppCompatActivity implements
        HomeFragment.OnBookItemClickListener,
        RecentFragment.OnRecentItemClickListener,
        BookmarkFragment.OnBookmarkItemClickListener, SearchFragment.OnSearchItemClickListener,
        SuttaDialogFragment.SuttaDialogListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        MDetect.init(this);
        setTitle(MDetect.getDeviceEncodedText(getString(R.string.app_name_mm)));

        if (savedInstanceState == null) {
            openFragment(new HomeFragment());
        }

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(item -> {
//            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    openFragment(new HomeFragment());
                    break;
                case R.id.navigation_bookmark:
                    openFragment(new BookmarkFragment());
                    break;
                case R.id.navigation_recent:
                    openFragment(new RecentFragment());
                    break;
                case R.id.navigation_search:
                    openFragment(new SearchFragment());
                    break;
                case R.id.navigation_setting:
                    Intent intent = new Intent(this, SettingActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBookItemClick(String bookID) {
        startReadBookActivity(bookID, 1, "");
    }

    @Override
    public void onBookmarkItemClick(String bookID, int pageNumber) {
        startReadBookActivity(bookID, pageNumber, "");
    }

    @Override
    public void onRecentItemClick(String bookID, int pageNumber) {
        startReadBookActivity(bookID, pageNumber, "");
    }

    @Override
    public void onSearchItemClick(String bookId, int pageNumber, String queryWord) {
        startReadBookActivity(bookId, pageNumber, queryWord);
    }

    private void startReadBookActivity(String bookID, int pageNumber, String queryWord) {

        Intent intent = new Intent(this, ReadBookActivity.class);
        intent.putExtra("bookID", bookID);
        intent.putExtra("currentPage", pageNumber);
        intent.putExtra("queryWord", queryWord);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_layout);
        if ( currentFragment instanceof HomeFragment) {
            finish();
        } else {
            BottomNavigationView navView = findViewById(R.id.navigation);
            navView.setSelectedItemId(R.id.navigation_home);
        }
    }


    @Override
    public void onClickedSutta(Sutta sutta) {
        startReadBookActivity(sutta.getBookID(), sutta.getPageNumber(), sutta.getName());

    }
}
