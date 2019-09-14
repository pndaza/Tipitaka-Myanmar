package mm.pndaza.tipitakamyanmar.model;

public class Page {
    int pageNumber;
    String pageContent;

    public Page(int pageNumber,String pageContent ) {
        this.pageNumber = pageNumber;
        this.pageContent = pageContent;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getPageContent() {
        return pageContent;
    }
}
