package com.github.n1try.model;

import java.util.List;
import java.util.Map;

public class Solution {
    private List<Replyer> replyers;
    private Office office;

    public Solution(List<Replyer> replyers, Office office) {
        this.replyers = replyers;
        this.office = office;
    }

    public int getTotalScore() {
        return office.totalScore();
    }

    @Override
    public String toString() {
        Map<Integer, Office.Tile> placements = office.getPlacements();
        StringBuilder sb = new StringBuilder();
        replyers.forEach(r -> {
            sb.append(placements.containsKey(r.getIndex()) ? placements.get(r.getIndex()).print() : "X");
            sb.append(System.lineSeparator());
        });
        return sb.toString();
    }
}
