package mm.pndaza.tipitakamyanmar.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.fragment.BookmarkFragment;
import mm.pndaza.tipitakamyanmar.fragment.HomeFragment;
import mm.pndaza.tipitakamyanmar.fragment.InfoFragment;
import mm.pndaza.tipitakamyanmar.fragment.RecentFragment;
import mm.pndaza.tipitakamyanmar.fragment.SearchFragment;
import mm.pndaza.tipitakamyanmar.fragment.SettingDialogFragment;
import mm.pndaza.tipitakamyanmar.utils.MDetect;

public class MainActivity extends AppCompatActivity implements
        RecentFragment.OnRecentItemClickListener,
BookmarkFragment.OnBookmarkItemClickListener, SearchFragment.OnSearchItemClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        MDetect.init(this);

        setTitle(MDetect.getDeviceEncodedText(getString(R.string.app_name_mm)));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if( savedInstanceState == null) {
            fragmentTransaction.replace(R.id.fragment_layout, new HomeFragment());
            fragmentTransaction.commit();
        }

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;
            switch (item.getItemId()) {
                case R.id.navigation_bookmark:
                    selectedFragment = new BookmarkFragment();
                    break;
                case R.id.navigation_recent:
                    selectedFragment = new RecentFragment();
                    break;
                case R.id.navigation_search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.navigation_info:
                    selectedFragment = new InfoFragment();
                    break;
                default:
                    selectedFragment = new HomeFragment();
                    break;
            }

            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.fragment_layout, selectedFragment);
            fragmentTransaction2.commit();

            return true;
        });

    }

    @Override
    public void onBookmarkItemClick(String bookid, int pageNumber) {
        startReadBookActivity(bookid, pageNumber, "");

    }

    @Override
    public void onRecentItemClick(String bookid, int pageNumber) {
        startReadBookActivity(bookid, pageNumber, "");

    }

    @Override
    public void onSearchItemClick(String bookid, int pageNumber, String queryWord) {
        startReadBookActivity(bookid, pageNumber, queryWord);
    }

    private void startReadBookActivity(String bookid, int pageNumber, String queryWord){

        Intent intent = new Intent(this, ReadBookActivity.class);
        intent.putExtra("bookID", bookid);
        intent.putExtra("currentPage", pageNumber);
        intent.putExtra("queryWord", queryWord);
        startActivity(intent);
    }



/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_setting) {

            FragmentManager fm = getSupportFragmentManager();
            SettingDialogFragment settingDialog = new SettingDialogFragment();
            settingDialog.show(fm, "Setting");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

/*    private void showSettingDialog(){
        FragmentManager fm = getSupportFragmentManager();
        SettingDialogFragment settingDialog = new SettingDialogFragment();
        settingDialog.show(fm, "Setting");
    }*/


}
