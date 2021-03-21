package fr.isima.technomobile.db.entities;

import java.util.ArrayList;
import java.util.List;

public class DetailDepense {

    private Contact member;
    private List<Emission> emissions = new ArrayList<>();

    public DetailDepense(Contact member) {
        this.member = member;
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
}
