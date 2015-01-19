package com.endless.android.candybomb;

public class Candy {
	private int mCandyPaintId;
	private boolean mVisiable;

	public Candy(int candyPaint) {
		mCandyPaintId = candyPaint;
		mVisiable = true;
	}

	public int getCandyPaintId() {
		return mCandyPaintId;
	}

	public void setCandyPaintId(int candyPaint) {
		mCandyPaintId = candyPaint;
	}

	public boolean isVisiable() {
		return mVisiable;
	}

	public void setVisiable(boolean visiable) {
		mVisiable = visiable;
	}
}
