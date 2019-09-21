package mm.pndaza.tipitakamyanmar.model;

public class Search {
    private String bookID;
    private String bookName;
    private int pageNumber;
    private String brief;

    public Search(String bookID, String bookName, int pageNumber, String brief) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.pageNumber = pageNumber;
        this.brief = brief;
    }

    public String getBookID() {
        return bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getBrief() {
        return brief;
    }
}
