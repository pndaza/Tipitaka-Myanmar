package mm.pndaza.tipitakamyanmar.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.data.Constants;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Bookmark;
import mm.pndaza.tipitakamyanmar.model.Recent;
import mm.pndaza.tipitakamyanmar.utils.SharePref;


public class SplashScreenActivity extends AppCompatActivity {
    //    private static final String DATABASE_FILENAME = "tipi_mm.db";
//    private static String OUTPUT_PATH;
    private String SAVED_PATH;
    //    private static int latestDatabaseVersion;
    private ArrayList<Bookmark> bookmarks = new ArrayList<>();
    private ArrayList<Recent> recents = new ArrayList<>();
    SharePref sharePref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        sharePref = SharePref.getInstance(this);
        if (sharePref.isFirstTime()) {
            sharePref.noLongerFirstTime();
            sharePref.saveDefault();
        }

        if (SharePref.getInstance(this).getPrefNightModeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        SAVED_PATH = getFilesDir().toString() + File.separator + Constants.DATABASE_PATH;
        File databaseFile = new File(SAVED_PATH, Constants.DATABASE_FILE_NAME);
        boolean isDatabaseCopied = sharePref.setDatabaseCopied();
        boolean isDatabaseFileExist = databaseFile.exists();
        int savedDatabaseVersion = sharePref.getDatabaseVersion();
        int latestDatabaseVersion = Constants.DATABASE_VERSION;

        if (isDatabaseCopied && isDatabaseFileExist) {
            Log.d("onCreate", "database exist");
            if (savedDatabaseVersion == latestDatabaseVersion) {
                startMainActivity();
            } else {
                Log.d("db setup mode", "updating");
                recents = backupRecent();
                // log recent  backup
                Log.d("Backup", "recents - " + recents.size());
                bookmarks = backupBookmarks();
                // log bookmark backup
                Log.d("Backup", "bookmarks - " + bookmarks.size());
                updateDatabase();
            }
        } else {
            Log.d("db setup mode", "first time");
            setupDatabase();
        }

    }

    private void updateDatabase() {
        deleteDatabase();
        setupDatabase();
    }

    private void deleteDatabase() {
        // deleting  temporary files created by sqlite
        File temp1 = new File(SAVED_PATH, Constants.DATABASE_FILE_NAME + "-shm");
        if (temp1.exists()) {
            temp1.delete();
        }
        File temp2 = new File(SAVED_PATH, Constants.DATABASE_FILE_NAME + "-wal");
        if (temp2.exists()) {
            temp2.delete();
        }

        new File(SAVED_PATH, Constants.DATABASE_FILE_NAME).delete();
    }


    private void setupDatabase() {

        File file = new File(SAVED_PATH, Constants.DATABASE_FILE_NAME);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            // check databases folder is exist and if not, make folder.
            if (!file.getParentFile().exists()) {
                final boolean result = file.getParentFile().mkdirs();
                Log.d("folder creation result", String.valueOf(result));
            }

            try {
                InputStream input = SplashScreenActivity.this.getAssets().open(
                        Constants.DATABASE_PATH + File.separator
                                + Constants.DATABASE_FILE_NAME);
                OutputStream output = new FileOutputStream(file);


                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
                input.close();
                output.close();

                Log.i("db copy", "success");

            } catch (FileNotFoundException e) {
                Log.d("setupDatabase", "File not found" + e.toString());
            } catch (IOException e) {
                Log.d("setupDatabase", "IO Exception" + e.toString());
            }

            restoreRecents(recents);
            restoreBookmark(bookmarks);

            handler.post(() -> {
                //UI Thread work here
                SharePref sharePref = SharePref.getInstance(this);
                sharePref.setDatabaseCopied(true);
                sharePref.setDatabaseVersion(Constants.DATABASE_VERSION);
                startMainActivity();
            });
        });

    }

    private void startMainActivity() {

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            SplashScreenActivity.this.startActivity(intent);
            SplashScreenActivity.this.finish();
        }, 500);

    }

    private ArrayList<Recent> backupRecent() {
        ArrayList<Recent> allRecent = new ArrayList<>();
        SQLiteDatabase database = DBOpenHelper.getInstance(this).getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM recent", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String bookid = cursor.getString(cursor.getColumnIndexOrThrow("bookid"));
                    String bookName = "";
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow("pagenumber"));
                    allRecent.add(new Recent(bookid, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        DBOpenHelper.getInstance(this).close();
        return allRecent;
    }

    private void restoreRecents(ArrayList<Recent> recents) {

        for (Recent recent : recents) {
            DBOpenHelper.getInstance(this).addToRecent(recent.getBookid(), recent.getPageNumber());
        }
    }

    private ArrayList<Bookmark> backupBookmarks() {
        ArrayList<Bookmark> bookmarkList = new ArrayList<>();
        SQLiteDatabase database = DBOpenHelper.getInstance(this).getReadableDatabase();
        Cursor cursor = database
                .rawQuery(" SELECT note, bookid, pagenumber FROM bookmark", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                    String bookid = cursor.getString(cursor.getColumnIndexOrThrow("bookid"));
                    String bookName = "";
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow("pagenumber"));
                    bookmarkList.add(new Bookmark(note, bookid, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        DBOpenHelper.getInstance(this).close();
        return bookmarkList;
    }

    private void restoreBookmark(ArrayList<Bookmark> bookmarks) {

        for (Bookmark bookmark : bookmarks) {
            DBOpenHelper.getInstance(this).addToBookmark(bookmark.getNote(), bookmark.getBookID(), bookmark.getPageNumber());
        }
    }

}
