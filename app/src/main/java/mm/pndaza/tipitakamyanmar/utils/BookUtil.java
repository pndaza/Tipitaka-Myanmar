package mm.pndaza.tipitakamyanmar.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mm.pndaza.tipitakamyanmar.model.Page;

public class BookUtil {
    public static ArrayList<Page> read(Context context, String bookid) throws IOException {
        ArrayList<Page> listOfPage = new ArrayList<>();

        String fileName = bookid + ".html";
        String dir = "Books";
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(dir+"/"+fileName), "UTF-8"));

        try {

            int pageNumber = 1;
            String pageContent = "";
            String pageBrakeMark = "--";
            while (true) {
                String strLine = bufferedReader.readLine();
                if (strLine == null) {
                    listOfPage.add(new Page(pageNumber,pageContent));
                    break;
                }
                if(strLine.equals(pageBrakeMark)){
                    listOfPage.add(new Page(pageNumber,pageContent));
                    pageNumber++;
                    pageContent="";
                    strLine = "";
                }
                pageContent+= (strLine + "\n" );
            }

        } finally {
            bufferedReader.close();
        }

        return listOfPage;
    }
}
