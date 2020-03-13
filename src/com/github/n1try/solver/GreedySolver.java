package com.github.n1try.solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private BitSet visitedTiles;
    private int c = 0;

    public GreedySolver(Office office, List<Replyer> replyers) {
        this.office = office;
        this.all = replyers;
        this.developers = replyers.stream()
            .filter(t -> t instanceof Developer)
            .map(t -> (Developer) t)
            .collect(Collectors.toList());
        this.managers = replyers.stream()
            .filter(t -> t instanceof Manager)
            .map(t -> (Manager) t)
            .collect(Collectors.toList());
        this.visitedTiles = new BitSet(office.nTiles());
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

        return new Solution(all, office);
    }

    private Optional<? extends Replyer> findBestCandidate(Office.Tile tile, Office.TileType type) {
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

        return list.parallelStream().max(Comparator.comparingInt(r -> office.score(tile, r)));
    }

    private void processRecursively(Office.Tile tile, Office.TileType type) {
        if (tile.isOccupied()) {
            return;
        }

        Optional<? extends Replyer> replyer = findBestCandidate(tile, type);
        if (replyer.isPresent()) {
            replyer.ifPresent(replyer1 -> place(replyer1, tile));

            c++;
            if (c % 100 == 0) {
                System.out.printf("Progress: %.1f %%\n", office.fillRate() * 100);
                c = 0;
            }
        } else {
            return;
        }

        office.getAdjacentTiles(tile, type, true)
            .stream()
            .filter(t -> !visitedTiles.get(t.getIndex()))
            .peek(t -> visitedTiles.set(t.getIndex()))
            .forEach(t -> processRecursively(t, type));
    }

    private void place(Replyer replyer, Office.Tile tile) {
        office.place(tile, replyer);

        if (replyer instanceof Developer) {
            developers.remove(replyer);
        } else if (replyer instanceof Manager) {
            managers.remove(replyer);
        }
    }
}
