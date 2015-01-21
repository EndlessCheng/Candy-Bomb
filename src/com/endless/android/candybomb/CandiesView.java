package com.endless.android.candybomb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class CandiesView extends View {

    private static final String TAG = CandiesView.class.getSimpleName();

    private static final int ROWS_NUMBER = 10;
    private static final int COLUMNS_NUMBER = 10;
    private static final int CANDIES_NUMBER = ROWS_NUMBER * COLUMNS_NUMBER;

    private static final int[] COLORS_ARRAY = {
            Color.rgb(250, 110, 134), // soft pink
            Color.rgb(151, 236, 133), // soft green
            Color.rgb(159, 224, 246), // soft blue
            Color.rgb(243, 229, 154), // soft yellow
            Color.rgb(222, 157, 214), // soft purple
            Color.rgb(177, 148, 153), // soft chocolate
    };
    private static final int COLORS_NUMBER = COLORS_ARRAY.length;

    private static final float CANDY_RADIUS = 31.0f; // * TEMP!!!! MUST CHANGE!!
    private static final float CANDY_DIAMETER = CANDY_RADIUS * 2.0f;
    private static final float CANDY_SPACING = 2.0f;

    private static final int[][] DIRS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    private static final int LEVELS_NUMBER = 4;

    private float mPaddingLeft = getResources().getDimension(R.dimen.activity_horizontal_margin);
    private float mPaddingTop = getResources().getDimension(R.dimen.activity_vertical_margin);

    private Paint mBackgroundPaint;
    private Paint mCandyPaint[];

    private Candy[][] mCandies = new Candy[ROWS_NUMBER][COLUMNS_NUMBER];

    private int mLevel;
    private int mLeftCandies;
    private int mLeftBombs;

    private int mFinalScore;
    private int mLevelScore;

    public CandiesView(Context context) {
        this(context, null);
    }

    public CandiesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0); // off-white
        mCandyPaint = new Paint[COLORS_NUMBER];
        for (int i = 0; i < COLORS_NUMBER; ++i) {
            mCandyPaint[i] = new Paint();
            mCandyPaint[i].setColor(COLORS_ARRAY[i]);
        }

        initGame();
    }

    private void initGame() {
        mLevel = 0;
        mFinalScore = 0;
        generateLevel();
    }

    private void generateLevel() {
        ++mLevel;
        int colorsNumber = mLevel + 2;
        mLeftBombs = colorsNumber - mLevel / LEVELS_NUMBER;
        if (mLeftBombs < 0) mLeftBombs = 0;
        mLeftCandies = CANDIES_NUMBER;
        mLevelScore = 0;

        int[] randomArray = new int[CANDIES_NUMBER];
        for (int i = 0; i < CANDIES_NUMBER; ++i) randomArray[i] = i;
        shuffleArray(randomArray);
        for (int i = 0; i < ROWS_NUMBER; ++i) {
            for (int j = 0; j < COLUMNS_NUMBER; ++j) {
                mCandies[i][j] = new Candy(randomArray[i * 10 + j] % colorsNumber);
            }
        }
    }

    private void shuffleArray(int[] arr) {
        Random rnd = new Random();
        for (int i = arr.length - 1; i > 0; --i) {
            int index = rnd.nextInt(i + 1);
            int temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "left candies: " + mLeftCandies);
        if (mLeftCandies == 0) {
            updateFinalScore();
            generateLevel();
        }

        canvas.drawPaint(mBackgroundPaint);

        for (int i = 0; i < ROWS_NUMBER; ++i) {
            for (int j = 0; j < COLUMNS_NUMBER; ++j) {
                if (mCandies[i][j].isVisiable()) {
                    canvas.drawCircle(
                            mPaddingLeft + (CANDY_DIAMETER + CANDY_SPACING) * j,
                            mPaddingTop + (CANDY_DIAMETER + CANDY_SPACING) * i,
                            CANDY_RADIUS, mCandyPaint[mCandies[i][j].getCandyPaintId()]);
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "event.getX(): " + event.getX());
//                Log.i(TAG, "event.getY(): " + event.getY());

                int raw = (int) (event.getY() / (CANDY_DIAMETER + CANDY_SPACING));
                int column = (int) (event.getX() / (CANDY_DIAMETER + CANDY_SPACING));

                if (raw < ROWS_NUMBER && column < COLUMNS_NUMBER && mCandies[raw][column].isVisiable()) {
                    updateCandy(raw, column);
                    invalidate();
                }
                break;
        }
        return true;
    }

    private void updateCandy(int raw, int column) {
//        Log.i(TAG, "raw: " + raw);
//        Log.i(TAG, "column: " + column);

        int removeCandiesNumber = removeCandy(raw, column);
        if (removeCandiesNumber == 1 && mLeftBombs == 0) {
            mCandies[raw][column].setVisiable(true);
            if (isGameOver()) {
                updateFinalScore();
                initGame();
            }
            return;
        }
        mLeftCandies -= removeCandiesNumber;
        if (removeCandiesNumber == 1) --mLeftBombs;
        updateLevelScore(removeCandiesNumber);
        tidyCandies();
    }

    private int removeCandy(int row, int column) {
//        Log.i(TAG, "row: " + row);
//        Log.i(TAG, "column: " + column);

        mCandies[row][column].setVisiable(false);
        int count = 1;
        for (int[] dir : DIRS) {
            int x = row + dir[0];
            int y = column + dir[1];
            if (x >= 0 && x < ROWS_NUMBER && y >= 0 && y < COLUMNS_NUMBER && mCandies[x][y].isVisiable()
                    && mCandies[x][y].getCandyPaintId() == mCandies[row][column].getCandyPaintId()) {
                count += removeCandy(x, y);
            }
        }
        return count;
    }

    private boolean isGameOver() {
        for (int i = 0; i < ROWS_NUMBER; ++i) {
            for (int j = 0; j < COLUMNS_NUMBER; ++j) {
                if (mCandies[i][j].isVisiable() && isNeighborSame(i, j)) return false;
            }
        }
        return true;
    }

    private boolean isNeighborSame(int row, int column) {
        for (int[] dir : DIRS) {
            int x = row + dir[0];
            int y = column + dir[1];
            if (x >= 0 && x < ROWS_NUMBER && y >= 0 && y < COLUMNS_NUMBER && mCandies[x][y].isVisiable()
                    && mCandies[x][y].getCandyPaintId() == mCandies[row][column].getCandyPaintId()) {
                return true;
            }
        }
        return false;
    }

    private void updateLevelScore(int removeCandiesNumber) {
//        Log.i(TAG, "add score: " + (50 * removeCandiesNumber * removeCandiesNumber));
        mLevelScore += 50 * removeCandiesNumber * removeCandiesNumber;
    }

    private void updateFinalScore() {
        if (mLevel > 0) {
            mFinalScore += mLevelScore;
            mFinalScore += mLeftBombs * 1000;
            mFinalScore -= mLeftCandies * 50;
//            if (mLeftBombs == mLevel + 2 - mLevel / LEVELS_NUMBER) {
//                mFinalScore += 5000;
//            }
        }
    }

    /**
     * move down -> bottom move to left
     */
    private void tidyCandies() {
        // move down
        for (int j = 0; j < COLUMNS_NUMBER; ++j) {
            int[] movePos = new int[ROWS_NUMBER];
            int cnt = 0;
            for (int i = ROWS_NUMBER - 1; i >= 0; --i) {
                if (mCandies[i][j].isVisiable()) {
                    movePos[cnt++] = i;
                }
            }
            for (int i = 0; i < cnt; ++i) {
                mCandies[ROWS_NUMBER - 1 - i][j].setCandyPaintId(mCandies[movePos[i]][j].getCandyPaintId());
                mCandies[ROWS_NUMBER - 1 - i][j].setVisiable(true);
            }
            for (int i = cnt; i < ROWS_NUMBER; ++i) {
                mCandies[ROWS_NUMBER - 1 - i][j].setVisiable(false);
            }
        }
        // bottom move to left
        int[] movePos = new int[COLUMNS_NUMBER];
        int cnt = 0;
        for (int j = 0; j < COLUMNS_NUMBER; ++j) {
            if (mCandies[ROWS_NUMBER - 1][j].isVisiable()) {
                movePos[cnt++] = j;
            }
        }
        for (int i = 0; i < cnt; ++i) {
            if (movePos[i] != i) {
                moveColumn(i, movePos[i]);
            }
        }
    }

    private void moveColumn(int dist, int src) {
        for (int i = 0; i < ROWS_NUMBER; ++i) {
            mCandies[i][dist].setCandyPaintId(mCandies[i][src].getCandyPaintId());
            mCandies[i][dist].setVisiable((mCandies[i][src].isVisiable()));
            mCandies[i][src].setVisiable(false);
        }
    }
}
