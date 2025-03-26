package mm.pndaza.tipitakamyanmar.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.fragment.BookmarkFragment;
import mm.pndaza.tipitakamyanmar.fragment.HomeFragment;
import mm.pndaza.tipitakamyanmar.fragment.RecentFragment;
import mm.pndaza.tipitakamyanmar.fragment.SearchFragment;
import mm.pndaza.tipitakamyanmar.utils.MDetect;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        MDetect.init(this);
        setTitle(MDetect.getInstance().getDeviceEncodedText(getString(R.string.app_name_mm)));

        AppBarLayout appBar = findViewById(R.id.appbar);
        ViewCompat.setOnApplyWindowInsetsListener(appBar, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        if (savedInstanceState == null) {
            openFragment(new HomeFragment());
        }

        // Create an OnBackPressedCallback
        /* enabled by default */
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                onClickBackButton();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                openFragment(new HomeFragment());
            } else if (itemId == R.id.navigation_bookmark) {
                openFragment(new BookmarkFragment());
            } else if (itemId == R.id.navigation_recent) {
                openFragment(new RecentFragment());
            } else if (itemId == R.id.navigation_search) {
                openFragment(new SearchFragment());
            } else if (itemId == R.id.navigation_setting) {
                startSettingActivity();
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

    private void startSettingActivity() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    void onClickBackButton() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_layout);
        if (currentFragment instanceof HomeFragment) {
            finish();
        } else {
            BottomNavigationView navView = findViewById(R.id.navigation);
            navView.setSelectedItemId(R.id.navigation_home);
        }
    }
}
