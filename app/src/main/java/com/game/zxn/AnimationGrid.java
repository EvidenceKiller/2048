package com.game.zxn;

import java.util.ArrayList;

/**
 * Created by ZXN on 2017/3/13 0013.
 */
public class AnimationGrid {

    public ArrayList<AnimationCell>[][] field;
    public ArrayList<AnimationCell> globalAnimation = new ArrayList<AnimationCell>();

    private int activeAnimations = 0;
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
        activeAnimations += 1;
    }

    public void tickAll(long timeElapsed) {
        ArrayList<AnimationCell> cancelledAnimation = new ArrayList<AnimationCell>();
        for (AnimationCell animation : globalAnimation) {
            animation.tick(timeElapsed);
            if (animation.animationDone()) {
                cancelledAnimation.add(animation);
                activeAnimations -=1;
            }
        }
        for (ArrayList<AnimationCell>[] array : field) {
            for (ArrayList<AnimationCell> list : array) {
                for (AnimationCell animation : list) {
                    animation.tick(timeElapsed);
                    if (animation.animationDone()) {
                        cancelledAnimation.add(animation);
                        activeAnimations -=1;
                    }
                }
            }
        }
        for (AnimationCell animation : cancelledAnimation) {
            cancelAnimation(animation);
        }
    }

    public boolean isAnimationActive() {
        if (activeAnimations != 0) {
            oneMoreFrame = true;
            return true;
        } else if (oneMoreFrame) {
            oneMoreFrame = false;
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<AnimationCell> getAnimationCell(int x, int y) {
        return field[x][y];
    }

    public void cancelAnimation(AnimationCell animation) {
        if (animation.getX() == -1 && animation.getY() == -1) {
            globalAnimation.remove(animation);
        } else {
            field[animation.getX()][animation.getY()].remove(animation);
        }
    }

}
