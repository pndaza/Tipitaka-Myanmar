package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Book;
import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class BookRepository extends BaseRepository {

    public BookRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    private static final String BOOK_TABLE = "book";
    private static final String BOOK_ID_COLUMN = "id";
    private static final String BOOK_NAME_COLUMN = "name";
    private static final String CATEGORY_COLUMN = "category_id";
    private static final String FIRST_PAGE_COLUMN = "first_page";
    private static final String LAST_PAGE_COLUMN = "last_page";


    public Book getBookInfo(String bookId) {
        String bookName = "";
        int firstPage = 1;
        int lastPage = 1;
        SQLBuilder sqlBuilder = new SQLBuilder().
                select(BOOK_NAME_COLUMN, FIRST_PAGE_COLUMN, LAST_PAGE_COLUMN).
                from(BOOK_TABLE).
                where(BOOK_ID_COLUMN, "=", bookId);
        String sql = sqlBuilder.build();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                bookName = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_NAME_COLUMN));
                firstPage = cursor.getInt(cursor.getColumnIndexOrThrow(FIRST_PAGE_COLUMN));
                lastPage = cursor.getInt(cursor.getColumnIndexOrThrow(LAST_PAGE_COLUMN));
            }
        }
        return new Book(bookId, bookName, firstPage, lastPage);
    }

    public String getBookName(String bookId) {
        String bookName = "";
        final String sql = new SQLBuilder().
                select(BOOK_NAME_COLUMN).
                from(BOOK_TABLE).
                where(BOOK_ID_COLUMN, "=", bookId).build();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                bookName = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_NAME_COLUMN));
            }
        }
        return bookName;
    }
/*
    public ArrayList<String> getAllBooks() {
        final String sql = new SQLBuilder().
                select(BOOK_ID_COLUMN).
                from(BOOK_TABLE).build();
        ArrayList<String> bookList = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    bookList.add(cursor.getString(cursor.getColumnIndexOrThrow(BOOK_ID_COLUMN)));
                } while (cursor.moveToNext());
            }
        }
        return bookList;
    }
    */

    public ArrayList<Book> getAllBooks() {
        final String sql = new SQLBuilder().
                select(BOOK_ID_COLUMN, BOOK_NAME_COLUMN, FIRST_PAGE_COLUMN, LAST_PAGE_COLUMN).
                from(BOOK_TABLE).build();
        ArrayList<Book> books = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final String bookId = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_ID_COLUMN));
                    final String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_NAME_COLUMN));
                    final int firstPage = cursor.getInt(cursor.getColumnIndexOrThrow(FIRST_PAGE_COLUMN));
                    final int lastPage = cursor.getInt(cursor.getColumnIndexOrThrow(LAST_PAGE_COLUMN));
                    books.add(new Book(bookId, bookName, firstPage, lastPage));
                } while (cursor.moveToNext());
            }
        }
        return books;
    }

    public  ArrayList<Book> getBooksByCategory(int categoryId) {
        final String sql = new SQLBuilder().
                select(BOOK_ID_COLUMN, BOOK_NAME_COLUMN, FIRST_PAGE_COLUMN, LAST_PAGE_COLUMN).
                from(BOOK_TABLE).
                where(CATEGORY_COLUMN, "=", categoryId).
                build();
        ArrayList<Book> books = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final String bookId = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_ID_COLUMN));
                    final String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BOOK_NAME_COLUMN));
                    final int firstPage = cursor.getInt(cursor.getColumnIndexOrThrow(FIRST_PAGE_COLUMN));
                    final int lastPage = cursor.getInt(cursor.getColumnIndexOrThrow(LAST_PAGE_COLUMN));
                    books.add(new Book(bookId, bookName, firstPage, lastPage));
                } while (cursor.moveToNext());
            }
        }
        return books;

    }

    public int getFirstPage(String bookId) {
        int firstPage = 1;
        final String sql = new SQLBuilder().
                select(FIRST_PAGE_COLUMN).
                from(BOOK_TABLE).
                where(BOOK_ID_COLUMN, "=", bookId).build();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                firstPage = cursor.getInt(cursor.getColumnIndexOrThrow(FIRST_PAGE_COLUMN));
            }
        }
        return firstPage;
        }

        public  int getLastPage(String bookId) {
        int lastPage = 1;
        final String sql = new SQLBuilder().
                select(LAST_PAGE_COLUMN).
                from(BOOK_TABLE).
                where(BOOK_ID_COLUMN, "=", bookId).build();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                lastPage = cursor.getInt(cursor.getColumnIndexOrThrow(LAST_PAGE_COLUMN));
            }
        }
        return lastPage;
        }
}