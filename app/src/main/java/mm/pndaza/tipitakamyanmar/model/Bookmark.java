package mm.pndaza.tipitakamyanmar.model;

public class Bookmark {

    private String note;
    private String bookID;
    private String bookName;
    private int pageNumber;

    public Bookmark(String note, String bookID, String bookName, int pageNumber) {
        this.note = note;
        this.bookID = bookID;
        this.bookName = bookName;
        this.pageNumber = pageNumber;
    }

    public String getNote() {
        return note;
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
}
