package org.auie.ui;

import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("NewApi")
public class UISingalView extends View {

	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_NONE = 1;
	
	private static final int COLOR_NORMAL = Color.parseColor("#CCFFFFFF");
	private static final int MAX = 5;
	
	private int status = STATUS_NORMAL;
	private final Paint mPaint = new Paint();
	private int DP;
	private int mLevel = 4;
	private int paintColor = COLOR_NORMAL;
	
	public UISingalView(Context context) {
		super(context);
		init();
	}
	public UISingalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public UISingalView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public UISingalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	private void init(){
		DP = UEMethod.dp2px(getContext(), 1);
	}
	
	public void setLevel(int level) {
		this.mLevel = level;
		invalidate();
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		canvas.drawARGB(0x00, 0x00, 0x00, 0x00);
		
		mPaint.setAntiAlias(true);
		mPaint.setColor(paintColor);
		
		float cy = getHeight()/2;
		float radius = (getHeight() - DP) / 2;
		
		for(int i = 0; i < mLevel; i++){
			canvas.drawCircle((cy + DP/2) * 2 * i + cy, cy, radius, mPaint);
		}
		
		if (mLevel < MAX) {
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(DP);
			for(int i = mLevel; i < MAX; i++){
				canvas.drawCircle((cy + DP/2) * 2 * i + cy, cy, radius - DP/2, mPaint);
			}
		}
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
		if (status == STATUS_NONE) {
			setVisibility(GONE);
		}else {
			setVisibility(VISIBLE);
		}
	}
	
	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
		invalidate();
	}
	
}
