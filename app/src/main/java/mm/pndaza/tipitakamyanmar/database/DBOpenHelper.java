package mm.pndaza.tipitakamyanmar.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.model.Bookmark;
import mm.pndaza.tipitakamyanmar.model.Recent;
import mm.pndaza.tipitakamyanmar.utils.MDetect;
import mm.pndaza.tipitakamyanmar.utils.Rabbit;

public class DBOpenHelper extends SQLiteOpenHelper {

    private  static DBOpenHelper sInstance;
    private static final String DATABASE_NAME = "tipi_mm.db";
    private static final int DATABASE_VERSION = 3;


    public static synchronized DBOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (sInstance == null) {
            sInstance = new DBOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBOpenHelper(Context context) {
        super(context, context.getFilesDir()+ "/databases/" + DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Book getBookInfo(String bookid){
        String bookName="";
        int firstPage=1;
        int lastPage=1;

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name,first_page,last_page FROM book_list where id = '" + bookid + "'", null);
        if( cursor != null && cursor.moveToFirst()){
            bookName = cursor.getString(cursor.getColumnIndex("name"));
            firstPage = cursor.getInt(cursor.getColumnIndex("first_page"));
            lastPage = cursor.getInt(cursor.getColumnIndex("last_page"));
        }

        return new Book(bookid,bookName,firstPage,lastPage);
    }

    public String getBoookName(String bookid){
        String bookName = "";
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name FROM book_list where id = '" + bookid + "'", null);
        if( cursor != null && cursor.moveToFirst()){
            bookName = cursor.getString(0);
        }
        return bookName;
    }


    public String getToc(String bookid){

        String toc = null;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                String.format("SELECT toc FROM book_list WHERE id = '%s'", bookid), null );
        if( cursor != null && cursor.moveToFirst()){
            toc = cursor.getString(0);
        }
        if(toc != null && !MDetect.isUnicode()){
            toc = Rabbit.uni2zg(toc);
        }
        sqLiteDatabase.close();
        cursor.close();

        return toc;
    }

    public ArrayList<String> getAllBook(){
        ArrayList<String> bookList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT id FROM book_list", null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                bookList.add(cursor.getString(cursor.getColumnIndex("id")));
            } while (cursor.moveToNext());

        }
        return bookList;
    }

    public ArrayList<Bookmark> getBookmarks(){
        ArrayList<Bookmark> bookmarkList = new ArrayList<>();
        Cursor cursor = getReadableDatabase()
                .rawQuery(" SELECT note, bookid, pagenumber FROM bookmark", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String note = cursor.getString(cursor.getColumnIndex("note"));
                    String bookid = cursor.getString(cursor.getColumnIndex("bookid"));
                    String bookName = getBoookName(bookid);
                    int pageNumber = cursor.getInt(cursor.getColumnIndex("pagenumber"));
                    bookmarkList.add(new Bookmark(note, bookid, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return bookmarkList;
    }

    public void addToBookmark(String note, String bookid, int pageNumber){
        getWritableDatabase()
                .execSQL("INSERT INTO bookmark (note, bookid, pagenumber) VALUES (?,?,?)",
                        new Object[]{note, bookid, pageNumber});
    }

    public void removeFromBookmark(int rowid){
        getWritableDatabase().execSQL("DELETE FROM bookmark WHERE rowid = " + rowid);
    }

    public void removeAllBookmarks(){
        getWritableDatabase().execSQL("DELETE FROM bookmark");
    }

    public ArrayList<Recent> getAllRecent() {
        ArrayList<Recent> recentList = new ArrayList<>();
        Cursor cursor = getReadableDatabase()
                .rawQuery(" SELECT rowid, bookid, pagenumber FROM recent ORDER BY rowid DESC", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String bookid = cursor.getString(cursor.getColumnIndexOrThrow("bookid"));
                    String bookName = getBoookName(bookid);
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow("pagenumber"));
                    recentList.add(new Recent(bookid, bookName, pageNumber));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return recentList;
    }

    private boolean isBookExistInRecent(String bookid) {
        Cursor cursor = this.getReadableDatabase()
                .rawQuery("SELECT bookid FROM recent Where bookid = '" + bookid + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void addToRecent(String bookid, int pageNumber){
        if(isBookExistInRecent(bookid)) {
            getWritableDatabase()
                    .execSQL("UPDATE recent SET bookid = '" + bookid + "', pagenumber = '" + pageNumber
                            + "' WHERE bookid = '" + bookid + "'");
        } else {
            getWritableDatabase()
                    .execSQL("INSERT INTO recent (bookid, pagenumber) VALUES (?,?)", new Object[]{bookid, pageNumber});
        }

    }

    public void removeAllRecent() {
        getWritableDatabase().execSQL("DELETE FROM recent");
    }

    public int getDatabaseVersion(){
        return DATABASE_VERSION;
    }
}
