package com.game.zxn;

import java.util.ArrayList;

/**
 * Created by ZXN on 2017/3/13 0013.
 */

public class AnimationGrid {

    public ArrayList<AnimationCell>[][] field;
    public ArrayList<AnimationCell> globalAnimation = new ArrayList<AnimationCell>();

    private int activeAnimation = 0;
    private boolean oneMoreFrame = false;

    public AnimationGrid(int x, int y) {
        field = new ArrayList[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                field[i][j] = new ArrayList<AnimationCell>();
            }
        }
    }

    public void startAnimation(int x, int y, int animationType, long length, long delay, int[] extra) {
        AnimationCell animationToAdd = new AnimationCell(x, y, animationType, length, delay, extra);
        if (x == -1 && y == -1) {
            globalAnimation.add(animationToAdd);
        } else {
            field[x][y].add(animationToAdd);
        }
        activeAnimation += 1;
    }

}
