package mm.pndaza.tipitakamyanmar.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mm.pndaza.tipitakamyanmar.R;
import mm.pndaza.tipitakamyanmar.database.DBOpenHelper;
import mm.pndaza.tipitakamyanmar.utils.SharePref;


public class SplashScreenActivity extends AppCompatActivity {
    private static final String DATABASE_FILENAME = "tipi_mm.db";
    private static String OUTPUT_PATH;
    private static int lastestDatabaseVersion;
    SharePref sharePref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        OUTPUT_PATH = getFilesDir() + "/databases/";

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

        boolean dbCopyState = sharePref.isDatabaseCopyed();
        int savedDatabaseVersion = sharePref.getDatabaseVersion();
        if(dbCopyState){
            lastestDatabaseVersion = DBOpenHelper.getInstance(this).getDatabaseVersion();
            if( lastestDatabaseVersion > savedDatabaseVersion){
                if(deleteDatabase()){
                    copyDatabase();
                }

            } else {
                startMainActivity();}

        }
        else {
            copyDatabase();
        }

    }

    private String getOutputPath() {
        return getFilesDir() + "/databases/";
    }

    private void copyDatabase() {

        new CopyFromAssets().execute();
    }

    public class CopyFromAssets extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {

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

            return null;
        }

        @Override
        protected void onProgressUpdate(final Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            sharePref.setDatabaseVersion(lastestDatabaseVersion);
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
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            finish();
            SplashScreenActivity.this.startActivity(intent);
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

}
