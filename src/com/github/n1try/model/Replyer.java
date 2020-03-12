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

    public int getIndex() {
        return index;
    }

    public int score() {
        return bonus;
    }

    public int score(Replyer other) {
        if (other == null || !other.getClass().equals(getClass()) || !other.company.equals(company)) {
            return 0;
        }
        return bonus * other.bonus;
    }
}
