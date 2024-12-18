package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mm.pndaza.tipitakamyanmar.model.Paragraph;
import mm.pndaza.tipitakamyanmar.utils.SQLBuilder;

public class ParagraphRepository extends BaseRepository {
    public ParagraphRepository(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    private static final String PARAGRAPH_TABLE = "paragraphs";
    private static final String BOOK_ID_COLUMN = "book_id";
    private static final String PAGE_NUMBER_COLUMN = "page_number";
    private static final String PARAGRAPH_NUMBER_COLUMN = "paragraph_number";
    private static final String PARAGRAPH_INDEX_COLUMN = "paragraph_index";

    private static final String TAG = "ParagraphRepository";
/*
    public ArrayList<Integer> getParagraphs(String bookId, int pageNumber) {
        SQLiteDatabase database = getReadableDatabase();
        SQLBuilder sqlBuilder = new SQLBuilder().

                select("paragraph_number").from(PARAGRAPH_TABLE)
                .where(BOOK_ID_COLUMN, "=", bookId)
                .where(PAGE_NUMBER_COLUMN, "=", pageNumber);
        String sql = sqlBuilder.build();
//        Object[] args = new Object[]{bookId, pageNumber};
        Log.d(TAG, "book id:" + bookId + " page number:" + pageNumber);
        Log.d(TAG, "getParagraphs: " + sql);
        Cursor cursor = database.rawQuery(sql, null);
        ArrayList<Integer> paraNumbers = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                paraNumbers.add(cursor.getInt(0));
            } while (cursor.moveToNext());
            cursor.close();
            Log.d(TAG, "getParagraphs: " + paraNumbers);
        }
        return paraNumbers;
    }
    */

    public ArrayList<Paragraph> getParagraphs(String bookId, int pageNumber) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<Paragraph> paragraphs = new ArrayList<Paragraph>();
//        String sql = new SQLBuilder().
//                select(PARAGRAPH_NUMBER_COLUMN,  PARAGRAPH_INDEX_COLUMN).
//                from(PARAGRAPH_TABLE).
//                where(BOOK_ID_COLUMN, "=", bookId).
//                build();
        String sql = "SELECT paragraph_number, paragraph_index FROM paragraphs " +
                " WHERE book_id = ? AND page_number = ?";
        Log.d(TAG, "getParagraphs: " + sql);
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{bookId, String.valueOf(pageNumber)});
        if (cursor != null && cursor.moveToFirst()) {
            do {

                final int paragraphNumber = cursor.getInt(cursor.getColumnIndexOrThrow(PARAGRAPH_NUMBER_COLUMN));
                final int paragraphIndex = cursor.getInt(cursor.getColumnIndexOrThrow(PARAGRAPH_INDEX_COLUMN));
                Paragraph paragraph = new Paragraph(paragraphNumber, paragraphIndex);
                paragraphs.add(paragraph);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return paragraphs;
    }


    public int getFirstParagraph(String bookId) {
        SQLiteDatabase database = getReadableDatabase();
//        String query = "SELECT paragraph_number FROM paragraphs WHERE book_id = ? ORDER BY paragraph_number ASC LIMIT 1";
        String query = "SELECT min(paragraph_number) FROM paragraphs WHERE book_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{bookId});

        if (cursor != null && cursor.moveToFirst()) {
            int paraNumber = cursor.getInt(0);
            cursor.close();
            Log.d(TAG, "getFirstParagraphs: " + paraNumber);
            return paraNumber;
        }
        return 0;
    }

    public int getLastParagraph(String bookId) {
        SQLiteDatabase database = getReadableDatabase();
//        String query = "SELECT paragraph_number FROM paragraphs WHERE book_id = ? ORDER BY rowid DESC LIMIT 1";
        String query = "SELECT max(paragraph_number) FROM paragraphs WHERE book_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{bookId});
        if (cursor != null && cursor.moveToFirst()) {
            int paraNumber = cursor.getInt(0);
            cursor.close();
            return paraNumber;
        }
        return 0;
    }

    public int getPageNumber(String bookId, int paragraphNumber) {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT page_number FROM paragraphs " +
                "WHERE book_id = ? AND paragraph_number = ?";
        Cursor cursor = database.rawQuery(query, new String[]{bookId, String.valueOf(paragraphNumber)});
        if (cursor != null && cursor.moveToFirst()) {
            int pageNumber = cursor.getInt(0);
            cursor.close();
            return pageNumber;
        }
        return 0;
    }

    public int getPageNumber(String bookId, int paragraphNumber, int paragraphIndex) {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT page_number FROM paragraphs " +
                "WHERE book_id = ? AND paragraph_number = ? AND paragraph_index = ?";
        Cursor cursor = database.rawQuery(query, new String[]{bookId, String.valueOf(paragraphNumber), String.valueOf(paragraphIndex)});
        if (cursor != null && cursor.moveToFirst()) {
            int pageNumber = cursor.getInt(0);
            cursor.close();
            return pageNumber;
        }
        return 0;
    }
}
