package com.github.n1try.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    }

    private Tile[][] tiles;

    public Office(int width, int height) {
        tiles = new Tile[height][width];
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
            }
        }
    }

    public Optional<Tile> getFreeTile(TileType type) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                Tile t = tiles[i][j];
                if (t.type == type && !t.isOccupied()) {
                    return Optional.of(t);
                }
            }
        }
        return Optional.empty();
    }

    public List<Tile> getAdjacentTiles(Tile tile, TileType type) {
        List<Tile> adjacentTiles = new ArrayList();

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
}
