package fr.isima.technomobile.db.entities;

import java.util.ArrayList;
import java.util.Date;

public class Depenses {

    private String date;
    private String title;


    public Depenses(String date, String title) {
        this.date = date;
        this.title = title;
    }



    public Depenses() {

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

    public void setTitle(String title) {
        this.title = title;
    }
}
