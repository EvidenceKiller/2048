package com.game.zxn;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User : ZXN
 * Date : 2017-02-19
 * Time : 18:22
 */

public class MainGame {

    public Grid mGrid;

    public AnimationGrid mAnimationGrid;

    public boolean emulating = false;
    public static int numSquaresX = 4;
    public static int numSquaresY = 4;
    public final int startTiles = 2;

    public long score = 0;
    public long lastScore = 0;
    public long highScore = 0;
    public boolean won = false;
    public boolean lose = false;

    private Context mContext;

    private MainView mView;

    public static final int SPAWN_ANIMATION = -1;
    public static final int MOVE_ANIMATION = 0;
    public static final int MERGE_ANIMATION = 1;

    public static final int FADE_GLOBAL_ANIMATION = 0;

    public static final long MOVE_ANIMATION_TIME = MainView.BASE_ANIMATION_TIME;
    public static final long SPAWN_ANIMATION_TIME = (int) (MainView.BASE_ANIMATION_TIME * 1.5);
    public static final long NOTIFICATION_ANIMATION_TIME = MainView.BASE_ANIMATION_TIME * 5;

    public static final long NOTIFICATION_DELAY_TIME = MOVE_ANIMATION_TIME + SPAWN_ANIMATION_TIME;
    public static final String HIGH_SCORE = "high score";

    public MainGame(Context context, MainView view) {
        mContext = context;
        mView = view;
    }

    public void newGame() {
        mGrid = new Grid(numSquaresX, numSquaresY);
        mAnimationGrid = new AnimationGrid(numSquaresX, numSquaresY);
        highScore = getHighScore();
        if (score >= highScore) {
            highScore = score;
            recordHighScore();
        }
        score = 0;
        won = false;
        lose = false;
        addStartTiles();
        mView.refreshLastTime = true;
        mView.resyncTime();
        mView.postInvalidate();
    }

    public void addStartTiles() {
        for (int xx = 0; xx < startTiles; xx++) {
            this.addRandomTile();
        }
    }

    public void addRandomTile() {
        if (mGrid.isCellsAvailable()) {
            addRandomTile(mGrid.randomAvailableCell());
        }
    }

    public void addRandomTile(Cell cell) {
        int value = Math.random() < 0.9 ? 2 : 4;
        Tile tile = new Tile(cell, value);
        mGrid.insertTile(tile);
        if (!emulating) mAnimationGrid.startAnimation(tile.getX(), tile.getY(), SPAWN_ANIMATION,
                SPAWN_ANIMATION_TIME, MOVE_ANIMATION_TIME, null); //Direction: -1 = EXPANDING
    }

    public void recordHighScore() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(HIGH_SCORE, highScore);
        editor.commit();
    }

    public long getHighScore() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getLong(HIGH_SCORE, -1);
    }

    public void prepareTiles() {
        for (Tile[] array : mGrid.field) {
            for (Tile tile : array) {
                if (mGrid.isCellOccupied(tile)) {
                    tile.setMergedFrom(null);
                    tile.savePosition();
                }
            }
        }
    }

    public void moveTile(Tile tile, Cell cell) {
        mGrid.field[tile.getX()][tile.getY()] = null;
        mGrid.field[cell.getX()][cell.getY()] = tile;
        tile.updatePosition(cell);
    }

    public void saveState() {
        mGrid.saveTiles();
        lastScore = score;
    }

    public void revertState() {
        mAnimationGrid = new AnimationGrid(numSquaresX, numSquaresY);
        mGrid.revertTiles();
        score = lastScore;

        if (!emulating) {
            mView.refreshLastTime = true;
            mView.resyncTime();
            mView.invalidate();
        }
    }

    public boolean move (int direction) {
        saveState();

        if (!emulating) mAnimationGrid = new AnimationGrid(numSquaresX, numSquaresY);
        // 0: up, 1: right, 2: down, 3: left
        if (lose || won) {
            return false;
        }
        Cell vector = getVector(direction);
        List<Integer> traversalsX = buildTraversalsX(vector);
        List<Integer> traversalsY = buildTraversalsY(vector);
        boolean moved = false;

        prepareTiles();

        for (int xx: traversalsX) {
            for (int yy: traversalsY) {
                Cell cell = new Cell(xx, yy);
                Tile tile = mGrid.getCellContent(cell);

                if (tile != null) {
                    Cell[] positions = findFarthestPosition(cell, vector);
                    Tile next = mGrid.getCellContent(positions[1]);

                    if (next != null && next.getValue() == tile.getValue() && next.getMergedFrom() == null) {
                        Tile merged = new Tile(positions[1], tile.getValue() * 2);
                        Tile[] temp = {tile, next};
                        merged.setMergedFrom(temp);

                        mGrid.insertTile(merged);
                        mGrid.removeTile(tile);

                        // Converge the two tiles' positions
                        tile.updatePosition(positions[1]);

                        if (!emulating) {
                            int[] extras = {xx, yy};
                            mAnimationGrid.startAnimation(merged.getX(), merged.getY(), MOVE_ANIMATION,
                                    MOVE_ANIMATION_TIME, 0, extras); //Direction: 0 = MOVING MERGED
                            mAnimationGrid.startAnimation(merged.getX(), merged.getY(), MERGE_ANIMATION,
                                    SPAWN_ANIMATION_TIME, MOVE_ANIMATION_TIME, null);
                        }

                        // Update the score
                        score = score + merged.getValue();
                        highScore = Math.max(score, highScore);

                        // The mighty max tile
                        if (merged.getValue() == MainView.maxValue) {
                            won = true;
                            endGame();
                        }
                    } else {
                        moveTile(tile, positions[0]);
                        int[] extras = {xx, yy, 0};
                        if (!emulating) mAnimationGrid.startAnimation(positions[0].getX(), positions[0].getY(), MOVE_ANIMATION, MOVE_ANIMATION_TIME, 0, extras); //Direction: 1 = MOVING NO MERGE
                    }

                    if (!positionsEqual(cell, tile)) {
                        moved = true;
                    }
                }
            }
        }

        if (moved) {
            if (!emulating && !MainView.inverseMode) {
                addRandomTile();
            }

            if (!movesAvailable()) {
                lose = true;
                endGame();
            }

        }

        if (!emulating) {
            mView.resyncTime();
            mView.postInvalidate();
        }

        return moved;
    }

    public void endGame() {
        if (emulating) return;

        mAnimationGrid.startAnimation(-1, -1, FADE_GLOBAL_ANIMATION, NOTIFICATION_ANIMATION_TIME, NOTIFICATION_DELAY_TIME, null);
        if (score >= highScore) {
            highScore = score;
            recordHighScore();
        }

        mGrid.canRevert = false;
    }

    public Cell getVector(int direction) {
        Cell[] map = {
                new Cell(0, -1), // up
                new Cell(1, 0),  // right
                new Cell(0, 1),  // down
                new Cell(-1, 0)  // left
        };
        return map[direction];
    }

    public List<Integer> buildTraversalsX(Cell vector) {
        List<Integer> traversals = new ArrayList<Integer>();

        for (int xx = 0; xx < numSquaresX; xx++) {
            traversals.add(xx);
        }
        if (vector.getX() == 1) {
            Collections.reverse(traversals);
        }

        return traversals;
    }

    public List<Integer> buildTraversalsY(Cell vector) {
        List<Integer> traversals = new ArrayList<Integer>();

        for (int xx = 0; xx <numSquaresY; xx++) {
            traversals.add(xx);
        }
        if (vector.getY() == 1) {
            Collections.reverse(traversals);
        }

        return traversals;
    }

    public Cell[] findFarthestPosition(Cell cell, Cell vector) {
        Cell previous;
        Cell nextCell = new Cell(cell.getX(), cell.getY());
        do {
            previous = nextCell;
            nextCell = new Cell(previous.getX() + vector.getX(),
                    previous.getY() + vector.getY());
        } while (mGrid.isCellWithinBounds(nextCell) && mGrid.isCellAvailable(nextCell));

        Cell[] answer = {previous, nextCell};
        return answer;
    }
    public boolean movesAvailable() {
        return mGrid.isCellsAvailable() || tileMatchesAvailable();
    }
    public boolean tileMatchesAvailable() {
        Tile tile;

        for (int xx = 0; xx < numSquaresX; xx++) {
            for (int yy = 0; yy < numSquaresY; yy++) {
                tile = mGrid.getCellContent(new Cell(xx, yy));

                if (tile != null) {
                    for (int direction = 0; direction < 4; direction++) {
                        Cell vector = getVector(direction);
                        Cell cell = new Cell(xx + vector.getX(), yy + vector.getY());

                        Tile other = mGrid.getCellContent(cell);

                        if (other != null && other.getValue() == tile.getValue()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean positionsEqual(Cell first, Cell second) {
        return first.getX() == second.getX() && first.getY() == second.getY();
    }

    // Only for emulation
    @Override
    public MainGame clone() {
        MainGame newGame = new MainGame(mContext, null);

        newGame.mGrid = mGrid.clone();
        newGame.score = score;
        newGame.emulating = true;

        return newGame;
    }
}
