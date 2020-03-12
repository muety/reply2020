package com.github.n1try.model;

import java.util.HashSet;
import java.util.Set;

public class Developer extends Replyer {
    private Set<String> skills;

    public Developer(int index) {
        this.index = index;
        skills = new HashSet<>();
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public void addSkill(String skill) {
        skills.add(skill);
    }

    @Override
    public int score() {
        return super.score() + skills.size();
    }

    @Override
    public int score(Replyer other) {
        if (other instanceof Developer) {
            return super.score(other) + getCommonSkills((Developer) other).size() * getDifferentSkills((Developer) other).size();
        }
        return super.score(other);
    }

    private Set<String> getCommonSkills(Developer other) {
        Set<String> intersection = new HashSet<>(skills);
        intersection.retainAll(other.skills);
        return intersection;
    }

    private Set<String> getDifferentSkills(Developer other) {
        Set<String> difference = new HashSet<>(skills);
        difference.addAll(other.skills);
        difference.removeAll(getCommonSkills(other));
        return difference;
    }
}
