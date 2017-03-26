package com.game.zxn;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * User : ZXN
 * Date : 2017-02-19
 * Time : 18:18
 */
public class MainView extends View {

    private Paint paint = new Paint();

    private MainGame game;

    private InputListener listener;

    private AI ai;

    private boolean getScreenSize = true;
    private int cellSize = 0;
    private float textSize = 0;
    private int gridWidth = 0;
    private int screenMiddleX = 0;
    private int screenMiddleY = 0;
    private int boardMiddleX = 0;
    private int boardMiddleY = 0;
    private Drawable backgroundRectangle;
    private Drawable[] cellRectangle = new Drawable[12];


    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
