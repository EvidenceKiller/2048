package com.game.zxn;

import android.content.Context;

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
    public static final int MOVE_ANIMTION = 0;
    public static final int MERGE_ANIMATION = 1;

    public static final int FADE_GLOBAL_ANIMATION = 0;

    public static final long MOVE_ANIMTION_TIME = MainView.BASE
}
