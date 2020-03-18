package com.github.n1try.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Office {
    public enum TileType {
        UNAVAILABLE, DEVELOPER, MANAGER
    }

    private static final char TOKEN_UNAVAILABLE = "#".charAt(0);
    private static final char TOKEN_DEVELOPER = "_".charAt(0);
    private static final char TOKEN_MANAGER = "M".charAt(0);

    public class Tile {
        private TileType type;
        private Replyer occupant;
        private int index;
        private int x;
        private int y;

        public Tile() {
        }

        public Tile(int x, int y, int index, TileType type) {
            this.x = x;
            this.y = y;
            this.index = index;
            this.type = type;
        }

        public Replyer getOccupant() {
            return occupant;
        }

        public void setOccupant(Replyer occupant) {
            this.occupant = occupant;
        }

        public boolean isOccupied() {
            return occupant != null || type == TileType.UNAVAILABLE;
        }

        public boolean isFree() {
            return occupant == null && type != TileType.UNAVAILABLE;
        }

        public TileType getType() {
            return type;
        }

        public void setType(TileType type) {
            this.type = type;
        }

        public int getIndex() {
            return index;
        }

        public String print() {
            return x + " " + y;
        }
    }

    private Tile[][] tiles;
    private List<Tile> tileList;
    private Map<Integer, Integer> scoreCache;
    private long nFree;
    private long nFilled;

    public Office(int width, int height) {
        tiles = new Tile[height][width];
        tileList = new LinkedList<>();
        scoreCache = new ConcurrentHashMap<>();
    }

    public void init(String[] officeConfig) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                char token = officeConfig[i].charAt(j);
                if (token == TOKEN_UNAVAILABLE) {
                    tiles[i][j] = new Tile(j, i, i * j, TileType.UNAVAILABLE);
                } else if (token == TOKEN_DEVELOPER) {
                    tiles[i][j] = new Tile(j, i, i * j, TileType.DEVELOPER);
                } else if (token == TOKEN_MANAGER) {
                    tiles[i][j] = new Tile(j, i, i * j, TileType.MANAGER);
                }
                tileList.add(tiles[i][j]);
            }
        }

        tileList.sort(Comparator.comparingInt(t -> getAdjacentTiles((Tile) t, ((Tile) t).type, false).size()).reversed());
        nFree = tileList.stream()
            .filter(t -> t.type != TileType.UNAVAILABLE)
            .count();
    }

    public Optional<Tile> nextTile(TileType type) {
        return tileList.stream().filter(t -> t.type == type && !t.isOccupied()).findFirst();
    }

    public List<Tile> getAdjacentTiles(Tile tile, TileType type, boolean freeOnly) {
        List<Tile> adjacentTiles = new ArrayList<>();

        if (tile.x > 0) {
            Tile t = tiles[tile.y][tile.x - 1];
            if (t.type == type) {
                if (!freeOnly || t.isFree()) {
                    adjacentTiles.add(t);
                }
            }
        }
        if (tile.x < tiles[0].length - 1) {
            Tile t = tiles[tile.y][tile.x + 1];
            if (t.type == type) {
                if (!freeOnly || t.isFree()) {
                    adjacentTiles.add(t);
                }
            }
        }
        if (tile.y > 0) {
            Tile t = tiles[tile.y - 1][tile.x];
            if (t.type == type) {
                if (!freeOnly || t.isFree()) {
                    adjacentTiles.add(t);
                }
            }
        }
        if (tile.y < tiles.length - 1) {
            Tile t = tiles[tile.y + 1][tile.x];
            if (t.type == type) {
                if (!freeOnly || t.isFree()) {
                    adjacentTiles.add(t);
                }
            }
        }

        return adjacentTiles;
    }

    public Map<Integer, Tile> getPlacements() {
        return tileList.stream()
            .filter(Tile::isOccupied)
            .filter(t -> t.type != TileType.UNAVAILABLE)
            .collect(Collectors.toMap(t -> t.getOccupant().getIndex(), Function.identity()));
    }

    public int score(Tile tile, Replyer replyer) {
        int cacheKey = tile.hashCode() * replyer.hashCode(); // TODO: This is not guaranteed to be unique!
        if (scoreCache.containsKey(cacheKey)) {
            return scoreCache.get(cacheKey);
        }

        int score = getAdjacentTiles(tile, tile.type, false).stream()
            .mapToInt(t -> replyer.score(t.getOccupant()))
            .sum();

        scoreCache.put(cacheKey, score);
        return score;
    }

    // TODO: Fix. Should be counted on the edge, so that the score of two adjacent replyers is only counted once.
    public int totalScore() {
        return tileList.stream()
            .filter(t -> t.occupant != null)
            .mapToInt(t -> score(t, t.occupant))
            .sum();
    }

    public void place(Tile tile, Replyer replyer) {
        tile.setOccupant(replyer);
        scoreCache.clear();
        nFilled++;
    }

    public float fillRate() {
        return (float) nFilled / (float) nFree;
    }

    public int nTiles() {
        return tileList.size();
    }
}
