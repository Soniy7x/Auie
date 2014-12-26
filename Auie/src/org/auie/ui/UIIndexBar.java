package org.auie.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

@SuppressLint("NewApi")
public class UIIndexBar extends View{
	
	private static final String[] INDEX_ITEM = {
		"*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
		"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
	};
	
	private TextView mTextView;
	private int currentItem = -1;
	private String[] mItems = INDEX_ITEM;
	private OnUIIndexItemOnTouchListener itemOnTouchListener;
	
	private Paint mPaint = new Paint();
	
	public UIIndexBar(Context context) {
		super(context);
	}

	public UIIndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public UIIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public UIIndexBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int count = mItems.length;
		int width  = getWidth();
		int height = getHeight();
		int subHeight = height / count;
		
		for(int i = 0; i < count; i ++){
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.GRAY);
			mPaint.setTextSize(18);
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
			if (i == currentItem) {
				mPaint.setColor(Color.BLACK);
				mPaint.setFakeBoldText(true);
			}
			canvas.drawText(mItems[i], width / 2 - mPaint.measureText(mItems[i]) / 2, subHeight * (i + 1), mPaint);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			currentItem = -1;
			invalidate();
			if (mTextView != null) {
				mTextView.setVisibility(View.INVISIBLE);
			}
			break;
		default:
			int pressItem = (int) (event.getY() / getHeight() * mItems.length);
			if (pressItem >= 0 && pressItem < mItems.length && pressItem != currentItem) {
				if (itemOnTouchListener != null) {
					itemOnTouchListener.onIndexItemChanged(mItems[pressItem]);
				}
				if (mTextView != null) {
					mTextView.setText(mItems[pressItem]);
					mTextView.setVisibility(VISIBLE);
				}
				currentItem = pressItem;
				invalidate();
			}
			break;
		}
		return true;
	}
	
	public void setItemOnTouchListener(OnUIIndexItemOnTouchListener itemOnTouchListener) {
		this.itemOnTouchListener = itemOnTouchListener;
	}

	public void setTextView(TextView mTextView) {
		this.mTextView = mTextView;
	}

	public String[] getmItems() {
		return mItems;
	}

	public void setmItems(String[] mItems) {
		this.mItems = mItems;
	}

	public interface OnUIIndexItemOnTouchListener{
		void onIndexItemChanged(String item);
	}
}
