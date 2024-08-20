package mm.pndaza.tipitakamyanmar.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.model.Bookmark;
import mm.pndaza.tipitakamyanmar.model.Recent;
import mm.pndaza.tipitakamyanmar.utils.SharePref;


public class SplashScreenActivity extends AppCompatActivity {
    private static final String DATABASE_FILENAME = "tipi_mm.db";
    private static String OUTPUT_PATH;
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

        OUTPUT_PATH = getFilesDir() + "/databases/";



        boolean dbCopyState = sharePref.isDatabaseCopied();
        boolean dbFileExit = new File(OUTPUT_PATH, DATABASE_FILENAME).exists();
        int savedDatabaseVersion = sharePref.getDatabaseVersion();
        int latestDatabaseVersion = DBOpenHelper.getInstance(this).getDatabaseVersion();

        if (dbCopyState && dbFileExit) {
            if ( latestDatabaseVersion == savedDatabaseVersion) {
                startMainActivity();
            } else if (latestDatabaseVersion > savedDatabaseVersion){
//                Log.d(TAG, "onCreate: last db version " + lastDatabaseVersion);
//                Log.d(TAG, "onCreate: saved db version " + savedDatabaseVersion);
                // update database
                sharePref.saveDefault();
                bookmarks = backupBookmarks();
                recents = backupRecents();

                deleteDatabase();
                copyDatabase(latestDatabaseVersion);
            }
        } else {
            copyDatabase(latestDatabaseVersion);
        }
    }

    private String getOutputPath() {
        return getFilesDir() + "/databases/";
    }

    private void copyDatabase(int dbVersion) {
        new CopyFromAssets().execute(dbVersion);
    }

    public class CopyFromAssets extends AsyncTask<Integer, Void, Integer> {

        protected Integer doInBackground(Integer... integers) {

            int dbVersion = integers[0];
            File path = new File(getOutputPath());
            // check database folder is exist and if not, make folder.
            if (!path.exists()) {
                path.mkdirs();
            }

            try {
                InputStream inputStream = getAssets().open("databases/" + DATABASE_FILENAME);
                OutputStream outputStream = new FileOutputStream(OUTPUT_PATH + DATABASE_FILENAME);

                byte[] buffer = new byte[1024];
                int length;
                while (( length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            if(bookmarks.size() > 0){
//                Log.d(TAG, "doInBackground: restoring backup bookmark");
//                Log.d(TAG, "doInBackground: Bookmark count - " + bookmarks.size() );
                restoreBookmark(bookmarks);
                restoreRecents(recents);
            }

            return dbVersion;
        }

        @Override
        protected void onProgressUpdate(final Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer value) {
            sharePref.setDatabaseVersion(value);
            sharePref.setDbCopyState(true);
            startMainActivity();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private void startMainActivity() {

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 500);
    }

    private boolean deleteDatabase(){
        // deleting  temporary files created by sqlite
        File temp1 = new File(OUTPUT_PATH, DATABASE_FILENAME + "-shm");
        if(temp1.exists()){
            temp1.delete();
        }
        File temp2 = new File(OUTPUT_PATH, DATABASE_FILENAME + "-wal");
        if(temp2.exists()){
            temp2.delete();
        }

        return new File(OUTPUT_PATH, DATABASE_FILENAME).delete();
    }

    private ArrayList<Recent> backupRecents(){
        ArrayList<Recent> recents = DBOpenHelper.getInstance(this).getAllRecent();
        DBOpenHelper.getInstance(this).close();
        return recents;
    }

    private void restoreRecents(ArrayList<Recent> recents){

        for (Recent recent: recents){
            DBOpenHelper.getInstance(this).addToRecent( recent.getBookid(), recent.getPageNumber());
        }
    }
    private ArrayList<Bookmark> backupBookmarks(){
        ArrayList<Bookmark> bookmarks = DBOpenHelper.getInstance(this).getBookmarks();
        DBOpenHelper.getInstance(this).close();
        return bookmarks;
    }

    private void restoreBookmark(ArrayList<Bookmark> bookmarks){

        for (Bookmark bookmark: bookmarks){
            DBOpenHelper.getInstance(this).addToBookmark(bookmark.getNote(), bookmark.getBookID(), bookmark.getPageNumber());
        }
    }

}
