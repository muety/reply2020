package com.github.n1try.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.n1try.model.Developer;
import com.github.n1try.model.Manager;
import com.github.n1try.model.Office;
import com.github.n1try.model.Replyer;
import com.github.n1try.model.Solution;

public class GreedySolver implements Solver {
    private Office office;
    private List<Developer> developers;
    private List<Manager> managers;
    private List<Replyer> all;
    private Map<Integer, Office.Tile> positions;

    public GreedySolver(Office office, List<Developer> developers, List<Manager> managers) {
        this.office = office;
        this.developers = developers;
        this.managers = managers;
        this.positions = new HashMap<>();
        this.all = Stream.of(developers, managers)
            .flatMap(Collection::stream)
            .sorted(Comparator.comparing(Replyer::getIndex))
            .collect(Collectors.toList());
    }

    @Override
    public Solution solve() {
        Optional<Office.Tile> tile;

        while (!developers.isEmpty()) {
            tile = office.nextTile(Office.TileType.DEVELOPER);
            if (tile.isEmpty()) {
                break;
            }
            processRecursively(tile.get(), Office.TileType.DEVELOPER);
        }

        while (!managers.isEmpty()) {
            tile = office.nextTile(Office.TileType.MANAGER);
            if (tile.isEmpty()) {
                break;
            }
            processRecursively(tile.get(), Office.TileType.MANAGER);
        }

        return new Solution(all, positions);
    }

    private Optional<Replyer> findBestCandidate(Office.Tile tile, Office.TileType type) {
        List<? extends Replyer> list;
        if (type == Office.TileType.DEVELOPER) {
            list = developers;
        } else if (type == Office.TileType.MANAGER) {
            list = managers;
        } else {
            list = new ArrayList<>();
        }

        if (list.isEmpty()) {
            return Optional.empty();
        }

        // TODO
        return Optional.of(list.get(0));
    }

    private void processRecursively(Office.Tile tile, Office.TileType type) {
        if (tile.isOccupied()) {
            return;
        }

        Optional<Replyer> replyer = findBestCandidate(tile, type);
        replyer.ifPresent(replyer1 -> place(replyer1, tile));

        office.adjacentTiles(tile, type).forEach(t -> processRecursively(t, type));
    }

    private void place(Replyer replyer, Office.Tile tile) {
        tile.setOccupant(replyer);
        positions.put(replyer.getIndex(), tile);

        if (replyer instanceof Developer) {
            developers.remove(replyer);
        } else if (replyer instanceof Manager) {
            managers.remove(replyer);
        }
    }
}
