package mm.pndaza.tipitakamyanmar.utils;
import android.content.Context;
import androidx.annotation.NonNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mm.pndaza.tipitakamyanmar.model.Page;

public class BookUtil {
    private static final String BOOKS_DIRECTORY = "Books";
    private static final String PAGE_BREAK_MARKER = "--+";
    private static final String FILE_EXTENSION = ".html";

    /**
     * Reads a book file and splits it into pages
     *
     * @param context Application context
     * @param bookId ID of the book to read
     * @return List of pages from the book
     * @throws IOException if file reading fails
     * @throws IllegalArgumentException if context or bookId is null
     */
    public static List<Page> readBook(@NonNull Context context, @NonNull String bookId,
                                      int firstPage) throws IOException {
        validateInputs(context, bookId);

        List<Page> pages = new ArrayList<>();
        String filePath = buildFilePath(bookId);

        try (BufferedReader reader = createReader(context, filePath)) {
            pages = parsePages(reader, firstPage);
        }

        return pages;
    }

    private static void validateInputs(Context context, String bookId) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
    }

    private static String buildFilePath(String bookId) {
        return BOOKS_DIRECTORY + "/" + bookId + FILE_EXTENSION;
    }

    private static BufferedReader createReader(Context context, String filePath) throws IOException {
        return new BufferedReader(
                new InputStreamReader(
                        context.getAssets().open(filePath),
                        StandardCharsets.UTF_8
                )
        );
    }

    private static List<Page> parsePages(BufferedReader reader, int firstPage) throws IOException {
        List<Page> pages = new ArrayList<>();
        int pageNumber = firstPage;
        StringBuilder pageContent = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.matches(PAGE_BREAK_MARKER)) {
                addPage(pages, pageNumber++, pageContent);
                pageContent = new StringBuilder();
                continue;
            }
            pageContent.append(line).append("\n");
        }

        // Add the last page
        addPage(pages, pageNumber, pageContent);

        return pages;
    }

    private static void addPage(List<Page> pages, int pageNumber, StringBuilder content) {
        pages.add(new Page(pageNumber, content.toString()));
    }
}
