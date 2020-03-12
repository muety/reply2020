package com.github.n1try.model;

public abstract class Replyer {
    int index;
    int bonus;
    String company;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int score() {
        return bonus;
    }

    public int score(Replyer other) {
        return company.equals(other.company) ? bonus * other.bonus : 0;
    }
}
