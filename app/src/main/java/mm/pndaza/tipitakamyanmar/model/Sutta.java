package mm.pndaza.tipitakamyanmar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Sutta implements Parcelable {
    String name;
    String bookID;
    String bookName;
    int pageNumber;


    public Sutta(String name, String bookID, String bookName, int pageNumber) {
        this.name = name;
        this.bookID = bookID;
        this.bookName = bookName;
        this.pageNumber = pageNumber;
    }

    protected Sutta(Parcel in) {
        name = in.readString();
        bookID = in.readString();
        bookName = in.readString();
        pageNumber = in.readInt();
    }

    public static final Creator<Sutta> CREATOR = new Creator<Sutta>() {
        @Override
        public Sutta createFromParcel(Parcel in) {
            return new Sutta(in);
        }

        @Override
        public Sutta[] newArray(int size) {
            return new Sutta[size];
        }
    };

    public String getName() {

        return name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(bookID);
        dest.writeString(bookName);
        dest.writeInt(pageNumber);
    }
}
