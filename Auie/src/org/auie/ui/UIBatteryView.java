package org.auie.ui;

import org.auie.utils.UEHtmlColor;
import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("NewApi")
public class UIBatteryView extends View {

	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_LOW = 1;
	public static final int STATUS_COMLETED = 2;
	public static final int STATUS_CHARGED = 3;
	
	private static final int COLOR_NORMAL = Color.parseColor("#CCFFFFFF");
	private static final int COLOR_LOW = UEHtmlColor.RED;
	private static final int COLOR_COMLETEDL = Color.parseColor("#73DE00");
	
	private int status = STATUS_NORMAL;
	private final Paint mPaint = new Paint();
	private int DP;
	private float mLevel = 1;
	private int paintColor = COLOR_NORMAL;
 	
	private float chargeLevel = 0.1f;
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (status != STATUS_CHARGED) {
				mHandler.removeCallbacks(mRunnable);
			}else {
				if (chargeLevel > 1) {
					chargeLevel = 0.1f;
				}
				setChargeLevel(chargeLevel);
				chargeLevel += 0.1;
				mHandler.postDelayed(mRunnable, 600);
			}
		}
	};
	
	public UIBatteryView(Context context) {
		super(context);
		init();
	}
	public UIBatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public UIBatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public UIBatteryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	private void init(){
		DP = UEMethod.dp2px(getContext(), 1);
	}

	private void setChargeLevel(float level){
		this.mLevel = level > 1 ? 1 : level;
		invalidate();
	}
	
	public void setLevel(float level){
		this.mLevel = level > 1 ? 1 : level;
		status = STATUS_NORMAL;
		if (mLevel <= 0.3) {
			status = STATUS_LOW;
		}
		invalidate();
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		if (status == this.status) {
			return;
		}else {			
			this.status = status;
			if (status == STATUS_CHARGED) {
				mHandler.post(mRunnable);
			}else if (status == STATUS_COMLETED) {
				setLevel(100);
			}
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		canvas.drawARGB(0x00, 0x00, 0x00, 0x00);
		
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(paintColor);
		mPaint.setStrokeWidth(DP);
		
		canvas.drawRoundRect(new RectF(DP, DP, getWidth() - 3.4F * DP, getHeight() - DP), DP/2, DP/2,  mPaint);
		
		mPaint.setStyle(Style.FILL);
		canvas.drawRoundRect(new RectF(getWidth() - 3.4F * DP, getHeight()/3, getWidth() - DP, getHeight()/3*2), DP/2, DP/2,  mPaint);
		
		switch (status) {
		case STATUS_LOW:
			mPaint.setColor(COLOR_LOW);
			break;
		case STATUS_COMLETED:
			mPaint.setColor(COLOR_COMLETEDL);
			break;

		default:
			mPaint.setColor(paintColor);
			break;
		}
		
		canvas.drawRect(2 * DP, 2 * DP, (getWidth() - 4.4F * DP) * mLevel, getHeight() - 2 * DP, mPaint);
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
		invalidate();
	}
	
}
