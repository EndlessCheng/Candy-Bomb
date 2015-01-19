package com.endless.android.candybomb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class CandiesView extends View {

    private static final String TAG = CandiesView.class.getSimpleName();

    private static final int ROWS_NUMBER = 10;
    private static final int COLUMNS_NUMBER = 10;
    private static final int CANDIES_NUMBER = ROWS_NUMBER * COLUMNS_NUMBER;

    private static final int[] COLORS_ARRAY = {Color.BLUE, Color.GREEN, Color.RED};
    private static final int COLORS_NUMBER = COLORS_ARRAY.length;

    private static final float CANDY_RADIUS = 31.0f; // * TEMP!!!! MUST CHANGE!!
    private static final float CANDY_DIAMETER = CANDY_RADIUS * 2.0f;
    private static final float CANDY_SPACING = 2.0f;

    private Paint mBackgroundPaint;

    private Candy[][] mCandies = new Candy[ROWS_NUMBER][COLUMNS_NUMBER];

    private int mLevel;
    private int mNowColorsNumber;

    public CandiesView(Context context) {
        this(context, null);
    }

    public CandiesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0); // off-white

        Paint candyPaint[] = new Paint[COLORS_NUMBER];
        for (int i = 0; i < COLORS_NUMBER; ++i) {
            candyPaint[i] = new Paint();
            candyPaint[i].setColor(COLORS_ARRAY[i]);
        }

        int[] randomArray = new int[CANDIES_NUMBER];
        for (int i = 0; i < CANDIES_NUMBER; ++i) {
            randomArray[i] = i;
        }
        shuffleArray(randomArray);

        mLevel = 1;
        mNowColorsNumber = 3;

        for (int i = 0; i < ROWS_NUMBER; ++i) {
            for (int j = 0; j < COLUMNS_NUMBER; ++j) {
                mCandies[i][j] = new Candy(candyPaint[randomArray[i * 10 + j] % mNowColorsNumber]);
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
        canvas.drawPaint(mBackgroundPaint);

        float paddingLeft = getResources().getDimension(R.dimen.activity_horizontal_margin);
        float paddingTop = getResources().getDimension(R.dimen.activity_vertical_margin);
        for (int i = 0; i < ROWS_NUMBER; ++i) {
            for (int j = 0; j < COLUMNS_NUMBER; ++j) {
                if (mCandies[i][j].isVisiable()) {
                    canvas.drawCircle(
                            paddingLeft + (CANDY_DIAMETER + CANDY_SPACING) * i,
                            paddingTop + (CANDY_DIAMETER + CANDY_SPACING) * j,
                            CANDY_RADIUS, mCandies[i][j].getCandyPaint());
                }
            }
        }
    }
}
