package com.game.zxn;

/**
 * User : ZXN
 * Date : 2017-02-19
 * Time : 18:27
 */

public class AnimationCell extends Cell {

    private int mAnimationType;
    private long mTimeElapsed;
    private long mAnimationTime;
    private long mDelayTime;
    public int[] mExtras;

    public AnimationCell (int x, int y, int animationType, long length, long delay, int[] extras) {
        super(x, y);
        this.mAnimationType = animationType;
        this.mAnimationTime = length;
        this.mDelayTime = delay;
        this.mExtras = extras;
    }

    public int getAnimationType() {
        return mAnimationType;
    }

    public void tick(long timeElapsed) {
        this.mTimeElapsed += timeElapsed;
    }

    public boolean animationDone() {
        return mAnimationTime + mDelayTime < mTimeElapsed;
    }

    public double getPercentageDone() {
        return Math.max(0, 1.0 * ((mTimeElapsed - mDelayTime) / mAnimationTime));
    }


}
