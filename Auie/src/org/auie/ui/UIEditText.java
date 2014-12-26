package org.auie.ui;

import org.auie.utils.UEDevice;
import org.auie.utils.UEHtmlColor;
import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.EditText;

@SuppressLint("NewApi")
public class UIEditText extends EditText {
	
	public static final int TYPE_LINE = 0;
	public static final int TYPE_FRAME_LIGHT = 1;
	public static final int TYPE_FRAME_DARK = 2;
	public static final int TYPE_ARC = 3;
	public static final int TYPE_CIRCLE = 4;
	
	private static final int DEFAULT_STROKECOLOR = Color.parseColor("#009EFC");
	private static final int DEFAULT_UNSTROKEOLOR = Color.parseColor("#CCCCCCCC");
	private static final int DEFAULT_CLEARCOLOR = Color.parseColor("#CC808080");
	private static final int DEFAULT_CLEARCENTERCOLOR = UEHtmlColor.WHITE;
	
	private Paint mPaint = new Paint();
	private boolean mClear = false;
	private boolean mFocus = false;
	private boolean openClear = true;
	private float clearX = 999;
	private float DP = 0;
	private float centerX = 0;
	private float centerY =  0;
	private float over = 0;
	private float radius = 0;
	private int scrollX = 0;
	private int realX = 0;
	private int realY = 0;
	private int type = TYPE_LINE;
	private int strokeColor = DEFAULT_STROKECOLOR;
	private int unStrokeColor = DEFAULT_UNSTROKEOLOR;
	private int clearColor = DEFAULT_CLEARCOLOR;
	private int clearCenterColor = DEFAULT_CLEARCENTERCOLOR;
	private int heightMode = -2;
	
	private TextWatcher mTextWatcher;
	
	public UIEditText(Context context) {
		super(context);
		initDatas();
	}

	public UIEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDatas();
	}

	public UIEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initDatas();
	}

	public UIEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initDatas();
	}
	
	private void initDatas(){
		DP = UEMethod.dp2pxReturnFloat(getContext(), 1);
		if (getBackground() != null) {
			if (UEDevice.getOSVersionCode() >= 11) {
				try {
					setStrokeColor(((ColorDrawable)getBackground()).getColor());
					super.setBackgroundColor(Color.TRANSPARENT);
				} catch (Exception e) {
					super.setBackgroundColor(Color.TRANSPARENT);
				}
			}else {
				super.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		super.setPadding(getPaddingLeft() + 10 * (int)DP, getPaddingTop(), (getPaddingRight() + 10 * (int)DP) * 2 + 16 * (int)DP, getPaddingBottom());
		super.addTextChangedListener(mTextChangedListener);
		super.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				if (heightMode < 0) {					
					setPadding(getPaddingLeft(), 10 * (int)DP, getPaddingRight(), 10 * (int)DP);
				}else {
					setPadding(getPaddingLeft(), getPaddingTop() + 7 * (int)DP, getPaddingRight(), getPaddingBottom() + 7 * (int)DP);
				}
				getViewTreeObserver().removeOnPreDrawListener(this);
				return false;
			}
		});
	}
	
	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
		invalidate();
	}
	
	public void setClearColor(int clearColor) {
		this.clearColor = clearColor;
		if (mClear) {			
			invalidate();
		}
	}

	public void setClearCenterColor(int clearCenterColor) {
		this.clearCenterColor = clearCenterColor;
		if (mClear) {			
			invalidate();
		}
	}
	public void setUnStrokeColor(int unStrokeColor) {
		this.unStrokeColor = unStrokeColor;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if (type != this.type){			
			this.type = type;
			invalidate();
		}
	}

	public void setRadius(float radius) {
		this.radius = radius;
		invalidate();
	}
	
	public boolean isOpenClear() {
		return openClear;
	}

	public void setOpenClear(boolean openClear) {
		this.openClear = openClear;
		if (openClear && getText().length() > 0) {
			this.mClear = true;
			invalidate();
		}
	}
	
	@Override
	public void addTextChangedListener(TextWatcher textWatcher){
		mTextWatcher = textWatcher;
	}

	private TextWatcher mTextChangedListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (openClear && s.toString().length() > 0) {
				mClear = true;
			}else {
				mClear = false;
			}
			invalidate();
			if (mTextWatcher != null) {
				mTextWatcher.onTextChanged(s, start, before, count);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			if (mTextWatcher != null) {
				mTextWatcher.beforeTextChanged(s, start, count, after);
			}
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (mTextWatcher != null) {
				mTextWatcher.afterTextChanged(s);
			}
		}
	};
	
	@Override //widthMeasureSpec = 1073742464
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (heightMeasureSpec < 0) {
			heightMode = -2;
		}else if (heightMeasureSpec == 1073742878) {
			heightMode = -1;
		}else {
			heightMode = 1;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mClear && event.getX() >= clearX) {
			getText().clear();
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		this.mFocus = focused;
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mPaint.setAntiAlias(true);
		
		scrollX = getScrollX() + getMeasuredWidth();
		realX = (int) (scrollX - getWidth() + 2 * DP);
		realY = (int) (getHeight() - 4 * DP);
		
		if (mClear) {
			mPaint.setColor(clearColor);
			mPaint.setStyle(Style.FILL);
			mPaint.setAlpha(180);
			centerX = scrollX - 16 * DP;
			centerY =  getHeight()/2.0f;
			over = 3.5355f * DP;
			canvas.drawCircle(centerX, centerY, 8 * DP, mPaint);
			mPaint.setColor(clearCenterColor);
			mPaint.setStrokeWidth(DP);
			canvas.drawLine(centerX - over, centerY - over, centerX + over, centerY + over, mPaint);
			canvas.drawLine(centerX + over, centerY - over, centerX - over, centerY + over, mPaint);
			clearX = getWidth() - 26 * DP;
		}
		
		if (getBackground() == null) {
			return;
		}
	
		mPaint.setStrokeWidth(0.6f * DP);	
		if (mFocus) {			
			mPaint.setColor(strokeColor);
		}else {
			mPaint.setColor(unStrokeColor);
		}
		
		switch (type) {
		case 3:
			if (radius > getHeight() / 2 || radius < 0) {
				radius = getHeight() / 2;
			}
			mPaint.setStyle(Paint.Style.STROKE);
	        canvas.drawRoundRect(new RectF(realX, 4 * DP, scrollX - 2 * DP, realY), radius, radius, mPaint);
	        mPaint.setColor(Color.parseColor("#88F8F8F8"));
	        mPaint.setStyle(Paint.Style.FILL);
	        canvas.drawRoundRect(new RectF(realX, 4 * DP, scrollX - 2 * DP, realY), radius, radius, mPaint);
			break;
		case 4:
			radius = getHeight() / 10 * 4.6f;
			mPaint.setStyle(Paint.Style.STROKE);
	        canvas.drawRoundRect(new RectF(realX, 4 * DP, scrollX - 2 * DP, realY), radius, radius, mPaint);
	        mPaint.setColor(Color.parseColor("#88F8F8F8"));
	        mPaint.setStyle(Paint.Style.FILL);
	        canvas.drawRoundRect(new RectF(realX, 4 * DP, scrollX - 2 * DP, realY), radius, radius, mPaint);
			break;
		case 2:
			mPaint.setStyle(Style.STROKE);
			canvas.drawRoundRect(new RectF(realX, 4 * DP, scrollX - 2 * DP, realY), DP, DP, mPaint);
			mPaint.setStyle(Style.FILL);
			mPaint.setAlpha(40);
			canvas.drawRoundRect(new RectF(realX, 4 * DP, scrollX - 2 * DP, realY), DP, DP, mPaint);
			break;
		case 1:
			mPaint.setStyle(Style.STROKE);
			canvas.drawRoundRect(new RectF(realX, 4 * DP, scrollX - 2 * DP, realY), DP, DP, mPaint);
			break;
		default:
			canvas.drawLine(realX, realY, scrollX - 2f * DP, realY, mPaint);
			break;
		}
	}
}
