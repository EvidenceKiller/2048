package com.game.zxn;

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

}
