package com.game.zxn;

import java.util.ArrayList;

/**
 * User : ZXN
 * Date : 2017-02-19
 * Time : 18:24
 */

public class Grid {

    public Tile[][] filed;
    public Tile[][] lastFiled;

    public boolean canRevert = false;

    int sizeX, sizeY;

    public Grid(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        filed = new Tile[sizeX][sizeY];
        lastFiled = new Tile[sizeX][sizeY];
        for (int i = 0; i < filed.length; i++) {
            for (int j = 0; j < filed[0].length; j++) {
                filed[i][j] = null;
                lastFiled[i][j] = null;
            }
        }
    }

    public Cell randomAvailableCell() {
        ArrayList<Cell> availableCells = getAvailableCells();
        if (availableCells.size() >= 1) {
            return availableCells.get((int) Math.floor(Math.random() * availableCells.size()));
        }
        return null;
    }

    public ArrayList<Cell> getAvailableCells() {
        ArrayList<Cell> availableCells = new ArrayList<Cell>();
        for (int i = 0; i < filed.length; i++) {
            for (int j = 0; j < filed[0].length; j++) {
                if (filed[i][j] == null) {
                    availableCells.add(new Cell(i, j));
                }
            }
        }
        return availableCells;
    }

    public boolean isCellsAvailable() {
        return (getAvailableCells().size() >= 1);
    }

    public boolean isCellAvailable(Cell cell) {
        return !isCellOccupied(cell);
    }

    public boolean isCellOccupied(Cell cell) {
        return (getCellContent(cell) != null);
    }

    public Tile getCellContent(Cell cell) {
        if (cell != null && isCellWithinBounds(cell)) {
            return filed[cell.getX()][cell.getY()];
        } else {
            return null;
        }
    }

    public Tile getCellContent(int x, int y) {
        if (isCellWithinBounds(x, y)) {
            return filed[x][y];
        } else {
            return null;
        }
    }

    public boolean isCellWithinBounds(Cell cell) {
        return 0 <= cell.getX() && cell.getY() < filed.length
                && 0 <= cell.getY() && cell.getY() < filed[0].length;
    }

    public boolean isCellWithinBounds(int x, int y) {
        return 0 <= x && x < filed.length
                && 0 <= y && y < filed[0].length;
    }

    public void insertTile(Tile tile) {
        filed[tile.getX()][tile.getY()] = tile;
    }

    public void removeTile(Tile tile) {
        filed[tile.getX()][tile.getY()] = null;
    }

    public void saveTiles() {
        canRevert = true;

        lastFiled = new Tile[sizeX][sizeY];
        for (int i = 0; i < filed.length; i++) {
            for (int j = 0; j < filed[0].length; j++) {
                if (filed[i][j] == null) {
                    lastFiled[i][j] = null;
                } else {
                    lastFiled[i][j] = new Tile(i, j, filed[i][j].getValue());
                }
            }
        }
    }

    public void revertTiles() {
        canRevert = false;

        for (int i = 0; i < lastFiled.length; i++) {
            for (int j = 0; j < lastFiled[0].length; j++) {
                if (lastFiled[i][j] == null) {
                    filed[i][j] = null;
                } else {
                    filed[i][j] = new Tile(i, j, lastFiled[i][j].getValue());
                }
            }
        }
    }

    @Override
    protected Grid clone() {
        Tile[][] newFiled = new Tile[sizeX][sizeY];
        for (int i = 0; i < filed.length; i++) {
            for (int j = 0; j < filed[0].length; j++) {
                if (filed[i][j] == null) {
                    newFiled[i][j] = null;
                } else {
                    newFiled[i][j] = new Tile(i, j, filed[i][j].getValue());
                }
            }
        }

        Grid newGrid = new Grid(sizeX, sizeY);
        newGrid.filed = newFiled;
        return newGrid;
    }
}
