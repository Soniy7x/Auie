package org.auie.ui;

import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("NewApi")
public class UIWIFIView extends View {

	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_NONE = 1;
	
	private static final int COLOR_NORMAL = Color.parseColor("#CCFFFFFF");
	private static final int MAX = 3;
	
	private int status = STATUS_NORMAL;
	private final Paint mPaint = new Paint();
	private int DP;
	private int mLevel = 4;
	private int paintColor = COLOR_NORMAL;
	
	public UIWIFIView(Context context) {
		super(context);
		init();
	}
	public UIWIFIView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public UIWIFIView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public UIWIFIView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	private void init(){
		DP = UEMethod.dp2px(getContext(), 1);
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		canvas.drawARGB(0x00, 0x00, 0x00, 0x00);
		
		mPaint.setAntiAlias(true);
		mPaint.setColor(paintColor);
		
		RectF[] rectF = {
			new RectF(8 * DP, 10.2f * DP, getWidth() - 8 * DP, getHeight() - 6 * DP),
			new RectF(5 * DP, 7.2f * DP, getWidth() - 5 * DP, getHeight() - 5 * DP),
			new RectF(2.4f * DP, 4.2f * DP, getWidth() - 2.4f * DP, getHeight() - 4.2f *DP),
		};
		
		if (mLevel > 0) {
			mPaint.setAlpha(255);
		}else {
			mPaint.setAlpha(51);
		}
		
		mPaint.setStyle(Style.FILL);
		canvas.drawCircle(getWidth()/2, getHeight() - 8.4f * DP, DP, mPaint);
		
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth( 1.4f * DP);
		
		for (int i = 1; i <= MAX; i++) {
			if (mLevel > i) {
				mPaint.setAlpha(204);
			}else {
				mPaint.setAlpha(75);
			}
			canvas.drawArc(rectF[i-1], 220, 100, false, mPaint);
		}
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
		if (status == STATUS_NORMAL) {
			setVisibility(VISIBLE);
		}else {
			setVisibility(GONE);
		}
	}
	public int getLevel() {
		return mLevel;
	}
	public void setLevel(int level) {
		this.mLevel = level;
		invalidate();
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
		invalidate();
	}
	
}
