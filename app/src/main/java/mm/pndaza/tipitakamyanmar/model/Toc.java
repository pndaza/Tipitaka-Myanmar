package mm.pndaza.tipitakamyanmar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Toc implements Parcelable {
    String type;
    String name;
    int page;


    public Toc(String type, String name, int page) {
        this.type = type;
        this.name = name;
        this.page = page;
    }

    protected Toc(Parcel in) {
        type = in.readString();
        name = in.readString();
        page = in.readInt();
    }

    public static final Creator<Toc> CREATOR = new Creator<Toc>() {
        @Override
        public Toc createFromParcel(Parcel in) {
            return new Toc(in);
        }

        @Override
        public Toc[] newArray(int size) {
            return new Toc[size];
        }
    };

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getPage() {
        return page;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(name);
        dest.writeInt(page);
    }
}
