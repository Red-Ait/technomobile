package fr.isima.technomobile.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Group createFromParcel(Parcel in ) {
            return new Group( in );
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    private int id;
    private String title;

    public Group() {
    }
    public Group(Parcel in ) {
        readFromParcel( in );
    }

    public Group(int id, String title) {
        this.id = id;
        this.title = title;
    }
    private void readFromParcel(Parcel in ) {
        id = new Integer(in.readString());
        title = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(String.valueOf(id));
        dest.writeString(title);
    }
}
