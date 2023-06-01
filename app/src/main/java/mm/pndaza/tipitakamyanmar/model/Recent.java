package mm.pndaza.tipitakamyanmar.model;

public class Recent {

    private String bookid;
    private String bookName;
    private int pageNumber;

    public Recent(String bookid, String bookName, int pageNumber) {
        this.bookid = bookid;
        this.bookName = bookName;
        this.pageNumber = pageNumber;
    }

    public String getBookid() {
        return bookid;
    }

    public String getBookName() {
        return bookName;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
