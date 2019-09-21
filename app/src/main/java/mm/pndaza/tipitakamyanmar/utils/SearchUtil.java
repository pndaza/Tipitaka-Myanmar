package mm.pndaza.tipitakamyanmar.utils;

import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Search;

public class SearchUtil {
    private static String TAG = "SearchUtil";

    public static ArrayList<Search> searchWord(String bookid, String bookName, int pageNumber, String content, String query) {
        ArrayList<Search> results = new ArrayList<>();
        String simplePageContent = htmlToText(content);
        int index;
        int startFrom = 0;
        int lengthOfQuery = query.length();

        while ((index = simplePageContent.indexOf(query, startFrom)) != -1) {
            String brief = getBrief(simplePageContent, query, index);
            results.add(new Search(bookid, bookName, pageNumber, brief));
            startFrom = index + lengthOfQuery;
        }
        return results;
    }

    private static String htmlToText(String htmlText) {
        return htmlText.replaceAll("<[^>]*>", "");
    }

    private static String getBrief(String content, String query, int index) {

        int length = content.length();
        int startIndexOfQuery = index;
        int endIndexOfQuery = startIndexOfQuery + query.length();
        int briefCharCount = 65;
        int counter = 1;

        while (startIndexOfQuery - counter >= 0 && counter < briefCharCount) {
            counter++;
        }
        int startIndexOfBrief = startIndexOfQuery - (counter - 1);

        counter = 1; //reset counter
        while (endIndexOfQuery + counter < length && counter < briefCharCount) {
            counter++;
        }
        int endIndexOfBrief = endIndexOfQuery + (counter - 1);

/*        Log.d(TAG, "length - " + length);
        Log.d(TAG, "startIndexOfQuery - " + startIndexOfQuery);
        Log.d(TAG, "endIndexOfQuery - " + endIndexOfQuery);
        Log.d(TAG, "startIndexOfBrief - " + startIndexOfBrief);
        Log.d(TAG, "endIndexOfBrief - " + endIndexOfBrief);*/

        return content.substring(startIndexOfBrief, endIndexOfBrief);
    }
}
