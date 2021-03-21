package fr.isima.technomobile.db.entities;


public class Emission {

    private int id;
    private Contact member;
    private double value;
    private String designation;

    public Emission() {
    }

    public Emission(int id, String designation, Contact member, double value) {
        this.id = id;
        this.designation = designation;
        this.member = member;
        this.value = value;
    }

    public Contact getMember() {
        return member;
    }

    public int getId() {
        return id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMember(Contact member) {
        this.member = member;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
