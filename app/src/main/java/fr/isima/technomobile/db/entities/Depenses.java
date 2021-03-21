package fr.isima.technomobile.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Depenses implements Parcelable {

    private int id;
    private String date;
    private String title;
    private int groupId;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Depenses createFromParcel(Parcel in ) {
            return new Depenses( in );
        }

        public Depenses[] newArray(int size) {
            return new Depenses[size];
        }
    };

    public Depenses() {

    }

    public Depenses(Parcel in ) {
        readFromParcel( in );
    }
    private void readFromParcel(Parcel in ) {
        id = Integer.parseInt(in.readString());
        date = in.readString();
        title = in.readString();
        groupId = Integer.parseInt(in.readString());
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public Depenses(int id, String date, String title) {
        this.id = id;
        this.date = date;
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        dest.writeString(date);
        dest.writeString(title);
        dest.writeString(String.valueOf(groupId));
    }
}
