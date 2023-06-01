package mm.pndaza.tipitakamyanmar.model;

public class Book {
    String id;
    String name;
    int firstPage;
    int lastPage;

    public Book(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Book(String id, String name, int firstPage, int lastPage) {
        this.id = id;
        this.name = name;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }
}
