package fr.isima.technomobile.db.entities;

public class Contact {
    private String number;
    private String name;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contact() {
        name = "";
        number = "";
    }
    public Contact(String number, String name) {
        this.number = number;
        this.name = name;
    }
}
