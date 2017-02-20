package com.game.zxn;

/**
 * User : ZXN
 * Date : 2017-02-19
 * Time : 18:25
 */

public class Cell {

    private int x;
    private int y;
    public boolean marked = false;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
