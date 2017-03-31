package com.game.zxn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
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
    private Drawable settingIcon;
    private Drawable lightUpRectangle;
    private Drawable fadeRectangle;
    private Bitmap background = null;
    private int backgroundColor;

    private static final int TEXT_BLACK;
    private static final int TEXT_WHITE;
    private static final int TEXT_BROWN;

    private double halfNumSquareX;
    private double halfNumSquareY;

    private int startingX;
    private int startingY;

    private int endingX;
    private int endingY;

    private int sYAll;
    private int titleStartYAll;
    private int bodyStartYAll;
    private int eYAll;
    private int titleWidthHighScore;
    private int titleWidthScore;

    private int sYIcons;
    private int sXNewGame;
    private int iconSize;

    private long lastFPSTime = System.nanoTime();
    private long currentTime = System.nanoTime();

    private float titleTextSize;
    private float bodyTextSize;
    private float headerTextSize;
    private float instructionsTextSize;
    private float gameOverTextSize;

    private boolean refreshLastTime = true;

    private String highScore, score, youWin, gameOver, instructions;

    private String[] titleTexts;

    private static int maxValue;

    public static boolean inverseMode = false;

    public static int BASE_ANIMATION_TIME = 120000000;
    public static int textPaddingSize = 0;
    public static int iconPaddingSize = 0;

    public static final float MOVING_ACCELERATION = 0.6f;
    public static final float MERGING_ACCELERATION = 0.6f;
    public static final float MAX_VELOCITY = (float) (MERGING_ACCELERATION * 0.5);

    Handler aiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getLayout(w, h);
        createBackgroundBitmap(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, paint);

        drawScoreText(canvas);

        if ((game.won || game.lose) && !game.aGrid.isAnimationActive()) {
            drawNewGameButton(canvas);
        }
        drawCells(canvas);
        drawEndGameState(canvas);
        if (game.aGrid.isAnimationActive()) {
            invalidate(startingX, startingY, endingX, endingY);
            tick();
        } else if ((game.won || game.lose) && refreshLastTime) {
            invalidate();
            refreshLastTime = false;
        }
    }

    public void drawDrawable(Canvas canvas, Drawable drawable,
                             int startingX, int startingY, int endingX, int endingY) {
        drawable.setBounds(startingX, startingY, endingX, endingY);
        drawable.draw(canvas);
    }

    public void drawCellText(Canvas canvas, int value, int sX, int sY) {
        int textShiftY = centerText();
        if (value >= 3) {
            paint.setColor(TEXT_WHITE);
        } else {
            paint.setColor(TEXT_BLACK);
        }
        canvas.drawText(titleTexts[value - 1], sX + cellSize / 2, sY + cellSize / 2 - textShiftY, paint);
    }

    public void drawScoreText(Canvas canvas) {
        paint.setTextSize(bodyTextSize);
        paint.setTextAlign(Paint.Align.CENTER);

        int bodyWidthHighScore = (int) (paint.measureText("" + game.highScore));
        int bodyWidthScore = (int) (paint.measureText("" + game.score));

        int textWidthHighScore = Math.max(titleWidthHighScore, bodyWidthHighScore) + textPaddingSize * 2;
        int textWidthScore = Math.max(titleWidthScore, bodyWidthScore) + textPaddingSize * 2;

        int textMiddleHighScore = textWidthHighScore / 2;
        int textMiddleScore = textWidthScore / 2;

        int eXHighScore = endingX;
        int sXHighScore = eXHighScore - textWidthHighScore;

        int eXScore = sXHighScore - textPaddingSize;
        int sXScore = eXScore - textWidthScore;

        backgroundRectangle.setBounds(sXHighScore, sYAll, eXHighScore, eYAll);
        backgroundRectangle.draw(canvas);
        paint.setTextSize(titleTextSize);
        paint.setColor(TEXT_BROWN);
        canvas.drawText(highScore, sXHighScore + textMiddleHighScore, titleStartYAll, paint);
        paint.setTextSize(bodyTextSize);
        paint.setColor(TEXT_WHITE);
        canvas.drawText("" + game.highScore, sXHighScore + textMiddleHighScore, bodyStartYAll, paint);

        backgroundRectangle.setBounds(sXScore, sYAll, eXScore, eYAll);
        backgroundRectangle.draw(canvas);
        paint.setTextSize(titleTextSize);
        paint.setColor(TEXT_BROWN);
        canvas.drawText(score, sXScore + textMiddleScore, titleStartYAll, paint);
        paint.setTextSize(bodyTextSize);
        paint.setColor(TEXT_WHITE);
        canvas.drawText("" + game.score, sXScore + textMiddleScore, bodyStartYAll, paint);
    }

    public void drawNewGameButton(Canvas canvas) {
        if (game.won || game.lose) {
            drawDrawable(canvas, lightUpRectangle, sXNewGame, sYIcons,
                    sXNewGame + iconSize, sYIcons + iconSize);
        } else {
            drawDrawable(canvas, backgroundRectangle, sXNewGame, sYIcons,
                    sXNewGame + iconSize, sYIcons + iconSize);
        }
        drawDrawable(canvas, settingIcon, sXNewGame + iconPaddingSize, sYIcons + iconPaddingSize,
                sXNewGame + iconSize - iconPaddingSize, sYIcons + iconSize - iconPaddingSize);
    }

    public void drawaHeader(Canvas canvas) {
        
    }

    private void getLayout(int width, int height) {
        cellSize = Math.min(width / (game.numSquaresX + 1), height / (game.numSquaresY + 3));
        gridWidth = cellSize / 7;
        screenMiddleX = width / 2;
        screenMiddleY = height / 2;
        boardMiddleX = screenMiddleX;
        boardMiddleY = screenMiddleY + cellSize / 2;
        iconSize = cellSize / 2;
    }

    public void createBackgroundBitmap(int width, int height) {
        background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(background);
        canvas.drawColor(backgroundColor);
        drawHeader(canvas);
        drawNewGameButton(canvas);
        drawBackground(canvas);
        drawBackgroundGrid(canvas);
        drawInstructions(canvas);
    }
}
