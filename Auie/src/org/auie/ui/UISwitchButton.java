package org.auie.ui;

import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class UISwitchButton extends View implements OnTouchListener{
	
	public static final int TYPE_POINT = 0;
	public static final int TYPE_ROUND = 1;
	
	public static final boolean OFF = false;
	public static final boolean ON = true;
	
	private int DP = 0;
	private int HEIGHT = 0;
	private Paint mPaint = new Paint();
	private boolean status = OFF;
	private int type = TYPE_POINT;
	
	private OnUISwitchChangeListener switchChangeListener;
	
	public UISwitchButton(Context context) {
		super(context);
		init();
	}

	public UISwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UISwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public UISwitchButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@SuppressWarnings("deprecation")
	public void init(){
		setOnTouchListener(this);
		HEIGHT = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
		getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				DP = UEMethod.dp2px(getContext(), 1);
				LayoutParams params = getLayoutParams();
				if (getHeight() >= HEIGHT - DP * 72) {
					params.height = 30 * DP;
				}
				params.width = (int) (params.height * 2.5);
				setLayoutParams(params);
				getViewTreeObserver().removeOnPreDrawListener(this);
				return false;
			}
		});
	}
	
	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
		invalidate();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		setStatus(!status);
		if (switchChangeListener != null) {
			switchChangeListener.onSwitchChanged(v, status);
		}
		return false;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setAntiAlias(true);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG));
		switch (type) {
		case TYPE_ROUND:
			if (status) {
				mPaint.setColor(Color.parseColor("#C8C8C8"));
				mPaint.setStrokeWidth(1.8f);
				mPaint.setStyle(Style.STROKE);
				canvas.drawRoundRect(new RectF(DP, DP, getWidth() - DP, getHeight() - DP), getHeight()/2, getHeight()/2, mPaint);
				mPaint.setColor(Color.parseColor("#E8E8E8"));
				mPaint.setStyle(Style.FILL);
				canvas.drawRoundRect(new RectF(1.6f * DP, 1.6f * DP, getWidth() - 1.6f * DP, getHeight() - 1.6f * DP), (getHeight() - DP)/2f, (getHeight() - DP)/2f, mPaint);
				mPaint.setColor(Color.parseColor("#C8C8C8"));
				mPaint.setStrokeWidth(2f);
				mPaint.setStyle(Style.FILL);
				canvas.drawCircle(getWidth() - getHeight()/2, getHeight()/2, getHeight()/2 - 2.8f * DP, mPaint);
				mPaint.setColor(Color.parseColor("#FFFFFF"));
				mPaint.setStyle(Style.FILL);
				canvas.drawCircle(getWidth() - getHeight()/2, getHeight()/2, getHeight()/2 - 3.4f * DP, mPaint);
			}else {
				mPaint.setColor(Color.parseColor("#C8C8C8"));
				mPaint.setStrokeWidth(1.8f);
				mPaint.setStyle(Style.STROKE);
				canvas.drawRoundRect(new RectF(DP, DP, getWidth() - DP, getHeight() - DP), getHeight()/2, getHeight()/2, mPaint);
				mPaint.setColor(Color.parseColor("#FDFDFD"));
				mPaint.setStyle(Style.FILL);
				canvas.drawRoundRect(new RectF(2 * DP, 2 * DP, getWidth() - 2 * DP, getHeight() - 2 * DP), (getHeight() - DP)/2, (getHeight() - DP)/2, mPaint);
				mPaint.setColor(Color.parseColor("#C8C8C8"));
				mPaint.setStrokeWidth(2f);
				mPaint.setStyle(Style.STROKE);
				canvas.drawCircle(getHeight()/2 + 0.5f * DP, getHeight()/2, getHeight()/2 - 2.8f * DP, mPaint);
				mPaint.setColor(Color.parseColor("#D8D8D8"));
				mPaint.setStyle(Style.FILL);
				canvas.drawCircle(getHeight()/2 + 0.5f * DP, getHeight()/2, getHeight()/2 - 3.4f * DP, mPaint);
			}
			break;

		default:
			mPaint.setStrokeWidth(1.8f);
			mPaint.setStyle(Style.STROKE);
			if (status) {
				mPaint.setColor(Color.parseColor("#88FFFFFF"));
				canvas.drawLine(4 * DP, getHeight()/2, getWidth() - getHeight() + 4 * DP, getHeight()/2, mPaint);
				canvas.drawCircle(getWidth() - getHeight()/2, getHeight()/2, getHeight()/2 - 4 * DP, mPaint);
				mPaint.setColor(Color.parseColor("#88FFFFFF"));
				mPaint.setStyle(Style.FILL);
				canvas.drawCircle(getWidth() - getHeight()/2, getHeight()/2, getHeight()/2 - 4.8f * DP, mPaint);
			}else {
				mPaint.setColor(Color.parseColor("#88FFFFFF"));
				canvas.drawLine(getHeight() - 4 * DP, getHeight()/2, getWidth() - 4 * DP, getHeight()/2, mPaint);
				canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2 - 4 * DP, mPaint);
				mPaint.setColor(Color.parseColor("#FF3366"));
				mPaint.setStyle(Style.FILL);
				canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2 - 4.8f * DP, mPaint);
			}
			break;
		}
	}
	
	public void setOnSwitchChangeListener(OnUISwitchChangeListener switchChangeListener) {
		this.switchChangeListener = switchChangeListener;
	}

	public void setType(int type) {
		this.type = type;
	}

	public interface OnUISwitchChangeListener{
		public void onSwitchChanged(View view, boolean checked);
	}
	
}
