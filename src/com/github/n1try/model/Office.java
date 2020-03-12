package com.github.n1try.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        private int x;
        private int y;

        public Tile() {
        }

        public Tile(int x, int y, TileType type) {
            this.x = x;
            this.y = y;
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

        public TileType getType() {
            return type;
        }

        public void setType(TileType type) {
            this.type = type;
        }

        public String print() {
            return y + " " + x;
        }
    }

    private Tile[][] tiles;
    private List<Tile> tileList;

    public Office(int width, int height) {
        tiles = new Tile[height][width];
        tileList = new ArrayList<>(width * height);
    }

    public void init(String[] officeConfig) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                char token = officeConfig[i].charAt(j);
                if (token == TOKEN_UNAVAILABLE) {
                    tiles[i][j] = new Tile(j, i, TileType.UNAVAILABLE);
                } else if (token == TOKEN_DEVELOPER) {
                    tiles[i][j] = new Tile(j, i, TileType.DEVELOPER);
                } else if (token == TOKEN_MANAGER) {
                    tiles[i][j] = new Tile(j, i, TileType.MANAGER);
                }
                tileList.add(tiles[i][j]);
            }
        }

        tileList.sort(Comparator.comparingInt(t -> adjacentTiles(t, t.type).size()));
        Collections.reverse(tileList);
    }

    public Optional<Tile> nextTile(TileType type) {
        return tileList.stream().filter(t -> t.type == type && !t.isOccupied()).findFirst();
    }

    public List<Tile> adjacentTiles(Tile tile, TileType type) {
        List<Tile> adjacentTiles = new ArrayList<>();

        if (tile.x > 0) {
            if (tiles[tile.y][tile.x - 1].type == type) {
                adjacentTiles.add(tiles[tile.y][tile.x - 1]);
            }
        }
        if (tile.x < tiles[0].length - 1) {
            if (tiles[tile.y][tile.x + 1].type == type) {
                adjacentTiles.add(tiles[tile.y][tile.x + 1]);
            }
        }
        if (tile.y > 0) {
            if (tiles[tile.y - 1][tile.x].type == type) {
                adjacentTiles.add(tiles[tile.y - 1][tile.x]);
            }
        }
        if (tile.y < tiles.length - 1) {
            if (tiles[tile.y + 1][tile.x].type == type) {
                adjacentTiles.add(tiles[tile.y + 1][tile.x]);
            }
        }

        return adjacentTiles;
    }

    public int score(Tile tile, Replyer replyer) {
        return adjacentTiles(tile, tile.type).stream()
            .mapToInt(t -> replyer.score(t.getOccupant()))
            .sum();
    }

    public int totalScore() {
        return tileList.stream()
            .filter(t -> t.occupant != null)
            .mapToInt(t -> score(t, t.occupant))
            .sum();
    }

    public Map<Integer, Tile> placements() {
        return tileList.stream()
            .filter(Tile::isOccupied)
            .filter(t -> t.type != TileType.UNAVAILABLE)
            .collect(Collectors.toMap(t -> t.getOccupant().getIndex(), Function.identity()));
    }
}
