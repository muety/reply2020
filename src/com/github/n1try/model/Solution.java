package com.github.n1try.model;

import java.util.List;
import java.util.Map;

public class Solution {
    private List<Replyer> replyers;
    private Map<Integer, Office.Tile> positions;

    public Solution(List<Replyer> replyers, Map<Integer, Office.Tile> positions) {
        this.positions = positions;
        this.replyers = replyers;
    }

    public int getTotalScore() {
        // TODO
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        replyers.forEach(r -> {
            sb.append(positions.containsKey(r.getIndex()) ? positions.get(r.getIndex()).print() : "X");
            sb.append(System.lineSeparator());
        });
        return sb.toString();
    }
}
