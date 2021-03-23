package fr.isima.technomobile.db.entities;

import java.util.ArrayList;
import java.util.List;

public class DetailDepense {

    private Contact member;
    private List<Emission> emissions = new ArrayList<>();
    private int depenseId;

    public DetailDepense(Contact member) {
        this.member = member;
    }
    public DetailDepense(Contact member, int depenseId) {
        this.member = member;
        this.depenseId = depenseId;
    }

    public DetailDepense() {
    }

    public Contact getMember() {
        return member;
    }

    public void setMember(Contact member) {
        this.member = member;
    }


    public List<Emission> getEmissions() {
        return emissions;
    }

    public void setEmissions(List<Emission> emissions) {
        this.emissions = emissions;
    }

    public int getDepenseId() {
        return depenseId;
    }

    public void setDepenseId(int depenseId) {
        this.depenseId = depenseId;
    }
}
