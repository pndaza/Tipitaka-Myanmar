package mm.pndaza.tipitakamyanmar.utils;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mm.pndaza.tipitakamyanmar.model.SearchQuery;
import mm.pndaza.tipitakamyanmar.model.SearchResult;

public class SearchUtil {
    private static final int BRIEF_CONTEXT_LENGTH = 65;

    private static final Pattern LEADING_COMBINING_MARKS = Pattern.compile("^[\u102b-\u103e]*");
    private static final Pattern LEADING_CONSONANT_VIRAMA = Pattern.compile("^[က-အ]်း?");
    private static final Pattern LEADING_NGA_HAT = Pattern.compile("^င့်");

    public static List<SearchResult> findMatches(SearchQuery query) {
        if (query == null || query.searchTerm() == null || query.searchTerm().isEmpty() || query.content() == null) {
            return new ArrayList<>();
        }

        List<SearchResult> results = new ArrayList<>();
        String searchTerm = query.searchTerm();
        String textToSearch = htmlToPlainText(query.content());

        int startPosition = 0;
        int queryLength = searchTerm.length();

        while (true) {
            int matchPosition = textToSearch.indexOf(searchTerm, startPosition);
            if (matchPosition == -1) break;

            String contextSnippet = extractContext(textToSearch, matchPosition, searchTerm);
            results.add(new SearchResult(
                    query.bookId(),
                    query.bookName(),
                    query.pageNumber(),
                    contextSnippet
            ));

            startPosition = matchPosition + queryLength;
        }

        return results;
    }

    private static String htmlToPlainText(String html) {
        if (html == null) return "";
        return Jsoup.parse(html).wholeText();
//        return html.replaceAll("<[^>]*>", "");
    }

    private static String extractContext(String text, int matchPosition, String searchTerm) {
        int textLength = text.length();
        int matchEnd = matchPosition + searchTerm.length();

        // Calculate context boundaries
        int contextStart = Math.max(0, matchPosition - BRIEF_CONTEXT_LENGTH);
        int contextEnd = Math.min(textLength, matchEnd + BRIEF_CONTEXT_LENGTH);
        String brief = text.substring(contextStart, contextEnd);

        return cleanDescription(brief);
    }

    public static String cleanDescription(String description) {
        if (description == null) return "";

        return LEADING_NGA_HAT.matcher(
                LEADING_CONSONANT_VIRAMA.matcher(
                        LEADING_COMBINING_MARKS.matcher(description).replaceAll("")
                ).replaceAll("")
        ).replaceAll("");
    }
}

