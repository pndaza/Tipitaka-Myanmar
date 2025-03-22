package mm.pndaza.tipitakamyanmar.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

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

    public ArrayList<Paragraph> getParagraphs(String bookId, int pageNumber) {
        ArrayList<Paragraph> paragraphs = new ArrayList<>();
        String sql = "SELECT paragraph_number, paragraph_index FROM paragraphs " +
                " WHERE book_id = ? AND page_number = ?";
        Log.d(TAG, "getParagraphs: " + sql);
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.query(PARAGRAPH_TABLE,
                new String[]{PARAGRAPH_NUMBER_COLUMN, PARAGRAPH_INDEX_COLUMN},
                BOOK_ID_COLUMN + " = ? AND " + PAGE_NUMBER_COLUMN + " = ?",
                new String[]{bookId, String.valueOf(pageNumber)},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final int paragraphNumber = cursor.getInt(cursor.getColumnIndexOrThrow(PARAGRAPH_NUMBER_COLUMN));
                    final int paragraphIndex = cursor.getInt(cursor.getColumnIndexOrThrow(PARAGRAPH_INDEX_COLUMN));
                    Paragraph paragraph = new Paragraph(paragraphNumber, paragraphIndex);
                    paragraphs.add(paragraph);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            // Log the error to Crashlytics
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("Error fetching paragraphs for bookId: " + bookId + " and page number: " + pageNumber);
            crashlytics.recordException(e);
        }

        return paragraphs;
    }


    public int getFirstParagraph(String bookId) {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT min(paragraph_number) FROM paragraphs WHERE book_id = ?";

        try (Cursor cursor = database.rawQuery(query, new String[]{bookId})) {
            if (cursor != null && cursor.moveToFirst()) {
                int paraNumber = cursor.getInt(0);
                Log.d(TAG, "getFirstParagraphs: " + paraNumber);
                return paraNumber;
            }
        } catch (SQLiteException e) {
            // Log the error to Crashlytics
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("Error fetching first paragraph for bookId: " + bookId);
            crashlytics.recordException(e);
        }

        return 0;
    }

    public int getLastParagraph(String bookId) {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT max(paragraph_number) FROM paragraphs WHERE book_id = ?";
        try (Cursor cursor = database.rawQuery(query, new String[]{bookId})) {
            if (cursor != null && cursor.moveToFirst()) {
                int paraNumber = cursor.getInt(0);
                Log.d(TAG, "getLastParagraph: " + paraNumber);
                return paraNumber;
            }
        } catch (SQLiteException e) {
            // Log the error to Crashlytics
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("Error fetching last paragraph for bookId: " + bookId);
            crashlytics.recordException(e);
        }
        return 0;
    }

    public int getPageNumber(String bookId, int paragraphNumber) {
        SQLiteDatabase database = getReadableDatabase();
        try (Cursor cursor = database.query(PARAGRAPH_TABLE,
                new String[]{PAGE_NUMBER_COLUMN},
                BOOK_ID_COLUMN + " = ? AND " + PARAGRAPH_NUMBER_COLUMN + " = ?",
                new String[]{bookId, String.valueOf(paragraphNumber)},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int pageNumber = cursor.getInt(0);
                Log.d(TAG, "getPageNumber: " + pageNumber);
                return pageNumber;
            }
        } catch (SQLiteException e) {
            // Log the parameters to Crashlytics
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("bookId: " + bookId);
            crashlytics.log("paragraphNumber: " + paragraphNumber);
            crashlytics.recordException(e); // Send the exception to Crashlytics
        }
        return 0;
    }

    public int getPageNumber(String bookId, int paragraphNumber,
                             int paragraphIndex) {
        SQLiteDatabase database = getReadableDatabase();
        try (Cursor cursor = database.query(PARAGRAPH_TABLE,
                new String[]{PAGE_NUMBER_COLUMN},
                BOOK_ID_COLUMN + " = ? AND " + PARAGRAPH_NUMBER_COLUMN + " = ? AND " + PARAGRAPH_INDEX_COLUMN + " = ?",
                new String[]{bookId, String.valueOf(paragraphNumber), String.valueOf(paragraphIndex)},
                null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int pageNumber = cursor.getInt(0);
                cursor.close();
                return pageNumber;
            }
        } catch (SQLiteException e) {
            // Log the parameters to Crashlytics
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("bookId: " + bookId);
            crashlytics.log("paragraphNumber: " + paragraphNumber);
            crashlytics.log("paragraphIndex: " + paragraphIndex);
            crashlytics.recordException(e); // Send the exception to Crashlytics
        }
        return 0;
    }
}
