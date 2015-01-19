package com.endless.android.candybomb;

import android.graphics.Paint;

public class Candy {
	private Paint mCandyPaint;
	private boolean mVisiable;

	public Candy(Paint candyPaint) {
		mCandyPaint = candyPaint;
		mVisiable = true;
	}

	public Paint getCandyPaint() {
		return mCandyPaint;
	}

	public void setCandyPaint(Paint candyPaint) {
		mCandyPaint = candyPaint;
	}

	public boolean isVisiable() {
		return mVisiable;
	}

	public void setVisiable(boolean visiable) {
		mVisiable = visiable;
	}
}
