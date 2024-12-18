package mm.pndaza.tipitakamyanmar.utils;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import mm.pndaza.tipitakamyanmar.model.SearchQuery;
import mm.pndaza.tipitakamyanmar.model.SearchResult;



public class SearchUtil {
    private static final int BRIEF_CONTEXT_LENGTH = 65;

    public static List<SearchResult> findMatches(SearchQuery query) {
        List<SearchResult> results = new ArrayList<>();
        String plainText = htmlToPlainText(query.content());

        int startPosition = 0;
        int queryLength = query.searchTerm().length();

        while (true) {
            int matchPosition = plainText.indexOf(query.searchTerm(), startPosition);
            if (matchPosition == -1) break;

            String contextSnippet = extractContext(plainText, matchPosition, query.searchTerm());
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
        return Jsoup.parse(html).wholeText();
//        return html.replaceAll("<[^>]*>", "");
    }

    private static String extractContext(String text, int matchPosition, String searchTerm) {
        int textLength = text.length();
        int matchEnd = matchPosition + searchTerm.length();

        // Calculate context boundaries
        int contextStart = Math.max(0, matchPosition - BRIEF_CONTEXT_LENGTH);
        int contextEnd = Math.min(textLength, matchEnd + BRIEF_CONTEXT_LENGTH);

        return text.substring(contextStart, contextEnd);
    }

}

