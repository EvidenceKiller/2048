package com.game.zxn;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.game.zxn.settings.SettingsPreference;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

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
        this(context, null);
    }

    public MainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Resources resources = context.getResources();
        int variety = SettingsPreference.getInt(SettingsPreference.KEY_VARIETY, 0);
        String[] varietyEntries = resources.getStringArray(R.array.variety_entries);
        titleTexts = varietyEntries[variety].split("\\|");
        maxValue = (int) Math.pow(2, titleTexts.length);

        inverseMode = SettingsPreference.getBoolean(SettingsPreference.KEY_INVERSE_MODE, false);
        game = new MainGame(context, this);
        if (titleTexts.length > 16 && titleTexts.length <= 25) {
            game.numSquaresX = 5;
            game.numSquaresY = 5;
        } else if (titleTexts.length > 25) {
            game.numSquaresX = 6;
            game.numSquaresY = 6;
        }

        try {
            highScore = resources.getString(R.string.high_score);
            score = resources.getString(R.string.score);
            youWin = resources.getString(R.string.you_win);
            gameOver = resources.getString(R.string.game_over);
            if (!inverseMode) {
                instructions = resources.getString(R.string.instructions)
                        + " " + titleTexts[0] + "=" + titleTexts[1];
            } else {
                instructions = resources.getString(R.string.instructuons_inversed);
            }
            backgroundRectangle = resources.getDrawable(R.drawable.background_rectangle);
            cellRectangle[0] = resources.getDrawable(R.drawable.cell_rectangle);
            cellRectangle[1] = resources.getDrawable(R.drawable.cell_rectangle_2);
            cellRectangle[2] = resources.getDrawable(R.drawable.cell_rectangle_4);
            cellRectangle[3] = resources.getDrawable(R.drawable.cell_rectangle_8);
            cellRectangle[4] = resources.getDrawable(R.drawable.cell_rectangle_16);
            cellRectangle[5] = resources.getDrawable(R.drawable.cell_rectangle_32);
            cellRectangle[6] = resources.getDrawable(R.drawable.cell_rectangle_64);
            cellRectangle[7] = resources.getDrawable(R.drawable.cell_rectangle_128);
            cellRectangle[8] = resources.getDrawable(R.drawable.cell_rectangle_256);
            cellRectangle[9] = resources.getDrawable(R.drawable.cell_rectangle_512);
            cellRectangle[10] = resources.getDrawable(R.drawable.cell_rectangle_1024);
            cellRectangle[11] = resources.getDrawable(R.drawable.cell_rectangle_2048);
            settingIcon = resources.getDrawable(R.drawable.ic_action_refresh);
            lightUpRectangle = resources.getDrawable(R.drawable.light_up_rectangle);
            fadeRectangle = resources.getDrawable(R.drawable.fade_rectangle);
            TEXT_WHITE = resources.getColor(R.color.text_white);
            TEXT_BLACK = resources.getColor(R.color.text_black);
            TEXT_BROWN = resources.getColor(R.color.text_brown);
            backgroundColor = resources.getColor(R.color.background);
            Typeface font = Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf");
            paint.setTypeface(font);
            paint.setAntiAlias(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener = new InputListener(this);
        setOnTouchListener(listener);
        setOnKeyListener(listener);
        game.newGame();
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

    public void drawHeader(Canvas canvas) {
        paint.setTextSize(headerTextSize);
        paint.setColor(TEXT_BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        int textShiftY = centerText() * 2;
        int headerStartY = sYAll - textShiftY;
        canvas.drawText(titleTexts[titleTexts.length - 1], startingX, headerStartY, paint);
    }

    public void drawInstructions(Canvas canvas) {
        paint.setTextSize(instructionsTextSize);
        paint.setTextAlign(Paint.Align.LEFT);
        int textShiftY = centerText() * 2;
        canvas.drawText(instructions, startingX, endingY - textShiftY + textPaddingSize, paint);
    }

    public void drawBackground(Canvas canvas) {
        drawDrawable(canvas, backgroundRectangle, startingX, startingY, endingX, endingY);
    }

    public void drawBackgroundGrid(Canvas canvas) {
        for (int x = 0; x < game.numSquaresX; x++) {
            for (int y = 0; y < game.numSquaresY; y++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) + x;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) + y;
                int eY = sY + cellSize;
                drawDrawable(canvas, cellRectangle[0], sX, sY, eX, eY);
            }
        }
    }

    public void drawCells(Canvas canvas) {
        Tile[][] tiles;
        AnimationGrid aGrid;
        for (int x = 0; x < game.numSquaresX; x++) {
            for (int y = 0; y < game.numSquaresY; y++) {
                int sX = startingX + gridWidth + (cellSize + gridWidth) + x;
                int eX = sX + cellSize;
                int sY = startingY + gridWidth + (cellSize + gridWidth) + y;
                int eY = sY + cellSize;

                Tile currentTile = game.mGrid.filed[x][y];
                if (null != currentTile) {
                    int value = currentTile.getValue();
                    int index = log2(value);

                    ArrayList<AnimationCell> aArray = game.mAnimationGrid.getAnimationCell(x, y);
                    boolean animated = false;
                    for (int i = aArray.size() - 1; i >= 0; i--) {
                        AnimationCell aCell = aArray.get(i);
                        if (aCell.getAnimationType() == MainGame.SPAWN_ANIMATION) {
                            animated = false;
                        }
                        if (!aCell.isActive()) {
                            continue;
                        }
                        if (aCell.getAnimationType() == MainGame.SPAWN_ANIMATION) {
                            double percentDone = aCell.getPercentageDone();
                            float textScaleSize = (float) percentDone;
                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            drawDrawable(canvas, cellRectangle[index],
                                    (int) (sX + cellScaleSize), (int) (sY + cellScaleSize),
                                    (int) (eX + cellScaleSize), (int) (eY + cellScaleSize));
                        } else if (aCell.getAnimationType() == MainGame.MERGE_ANIMATION) {
                            double percentDone = aCell.getPercentageDone();
                            float currentVelocity = 0f;
                            if (percentDone < 0.5f) {
                                currentVelocity = (float) (MERGING_ACCELERATION * percentDone);
                            } else {
                                currentVelocity = (float) (MAX_VELOCITY - MERGING_ACCELERATION
                                        * (percentDone - 0.5));
                            }
                            float textScaleSize = (float) (1 + currentVelocity * percentDone);
                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            drawDrawable(canvas, cellRectangle[index],
                                    (int) (sX + cellScaleSize), (int) (sY + cellScaleSize),
                                    (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                        } else if (aCell.getAnimationType() == MainGame.MOVE_ANIMATION) {
                            double percentDone = aCell.getPercentageDone();
                            int tempIndex = index;
                            if (aArray.size() >= 2) {
                                tempIndex -= 1;
                            }
                            int previousX = aCell.mExtras[0];
                            int previousY = aCell.mExtras[1];
                            int currentX = currentTile.getX();
                            int currentY = currentTile.getY();
                            int dX = (int) ((currentX - previousX) * (cellSize + gridWidth) *
                                    (percentDone - 1) * (percentDone - 1) * -MOVING_ACCELERATION);
                            int dY = (int) ((currentY - previousY) * (cellSize + gridWidth) *
                                    (percentDone - 1) * (percentDone - 1) * -MOVING_ACCELERATION);
                            drawDrawable(canvas, cellRectangle[tempIndex], sX + dX, sY + dY,
                                    eX + dX, eY + dY);
                        }
                        animated = true;
                    }
                    if (!animated) {
                        drawDrawable(canvas, cellRectangle[index], sX, sY, eX, eY);
                    }
                }
            }
        }
    }

    public void drawEndGameState(Canvas canvas) {
        double alphaChange = 1;
        for (AnimationCell animation : game.mAnimationGrid.globalAnimation) {
            if (animation.getAnimationType() == MainGame.FADE_GLOBAL_ANIMATION) {
                alphaChange = animation.getPercentageDone();
            }
        }
        if (game.won) {
            lightUpRectangle.setAlpha((int) (127 * alphaChange));
            drawDrawable(canvas, lightUpRectangle, startingX, startingY, endingX, endingY);
            lightUpRectangle.setAlpha(255);
            paint.setColor(TEXT_WHITE);
            paint.setAlpha((int) (255 * alphaChange));
            paint.setTextSize(gameOverTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(youWin, boardMiddleX, boardMiddleY - centerText(), paint);
            paint.setAlpha(255);
        } else if (game.lose) {
            fadeRectangle.setAlpha((int) (127 * alphaChange));
            drawDrawable(canvas, fadeRectangle, startingX, startingY, endingX, endingY);
            fadeRectangle.setAlpha(255);
            paint.setColor(TEXT_BLACK);
            paint.setAlpha((int) (255 * alphaChange));
            paint.setTextSize(gameOverTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(gameOver, boardMiddleX, boardMiddleY - centerText(), paint);
            paint.setAlpha(255);
        }
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

    public void tick() {
        currentTime = System.nanoTime();
        try {
            game.mGrid.tickAll(currentTime - lastFPSTime);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        lastFPSTime = currentTime;
    }

    public void resyncTime() {
        lastFPSTime = System.nanoTime();
    }

    public static int log2(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        return (int) (Math.log(n) / Math.log(2));
    }

    private void getLayout(int width, int height) {
        cellSize = Math.min(width / (game.numSquaresX + 1), height / (game.numSquaresY + 3));
        gridWidth = cellSize / 7;
        screenMiddleX = width / 2;
        screenMiddleY = height / 2;
        boardMiddleX = screenMiddleX;
        boardMiddleY = screenMiddleY + cellSize / 2;
        iconSize = cellSize / 2;

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellSize);
        textSize = cellSize * cellSize / Math.max(cellSize, paint.measureText("0000"));
        titleTextSize = textSize / 3;
        bodyTextSize = (int) (textSize / 1.5);
        instructionsTextSize = bodyTextSize;
        headerTextSize = textSize * 2;
        gameOverTextSize = headerTextSize;
        textPaddingSize = (int) (textSize / 3);
        iconPaddingSize = textPaddingSize;

        halfNumSquareX = game.numSquaresX / 2;
        halfNumSquareY = game.numSquaresY / 2;

        startingX = (int) (boardMiddleX - (cellSize + gridWidth) * halfNumSquareX - gridWidth / 2);
        endingX = (int) (boardMiddleX + (cellSize + gridWidth) * halfNumSquareX + gridWidth / 2);
        startingY = (int) (boardMiddleY - (cellSize + gridWidth) * halfNumSquareY - gridWidth / 2);
        endingY = (int) (boardMiddleY + (cellSize + gridWidth) * halfNumSquareY + gridWidth / 2);
        paint.setTextSize(titleTextSize);

        int textShiftYAll = centerText();

        sYAll = (int) (startingY - cellSize * 1.5);
        titleStartYAll = (int) (sYAll + textPaddingSize + titleTextSize / 2 - textShiftYAll);
        bodyStartYAll = (int) (titleStartYAll + textPaddingSize + titleTextSize / 2 + bodyTextSize / 2);

        titleWidthHighScore = (int) (paint.measureText(highScore));
        titleWidthScore = (int) (paint.measureText(score));
        paint.setTextSize(bodyTextSize);

        textShiftYAll = centerText();
        eYAll = (int) (bodyStartYAll + textShiftYAll + bodyTextSize / 2 + textPaddingSize);

        sYIcons = (startingY + eYAll) / 2 - iconSize / 2;
        sXNewGame = (endingX - iconSize);
        resyncTime();
        getScreenSize = false;
        initRectangleDrawables();
    }

    private void initRectangleDrawables() {
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        Drawable lastDrawable = cellRectangle[11];
        Drawable[] newArray = new Drawable[titleTexts.length + 1];
        newArray[0] = cellRectangle[0];

        for (int i = 0; i < titleTexts.length + 1; i++) {
            Drawable rect;
            if (i <= 11) {
                rect = cellRectangle[i];
            } else {
                rect = lastDrawable;
            }
            Bitmap bitmap = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawDrawable(canvas, rect, 0, 0, cellSize, cellSize);
            drawCellText(canvas, i, 0, 0);
            rect = new BitmapDrawable(bitmap);
            newArray[i] = rect;
        }
    }

    private int centerText() {
        return (int) ((paint.descent() + paint.ascent()) / 2);
    }




}
