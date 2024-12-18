package mm.pndaza.tipitakamyanmar.model;

public record SearchQuery(
        String bookId,
        String bookName,
        int pageNumber,
        String content,
        String searchTerm
) {}