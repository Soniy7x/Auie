package org.auie.ui;

import org.auie.utils.UEHtmlColor;
import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.EditText;

@TargetApi(Build.VERSION_CODES.L)
public class UIEditText extends EditText {
	
	public static final int TYPE_LINE = 0;
	public static final int TYPE_FRAME_LIGHT = 1;
	public static final int TYPE_FRAME_DARK = 2;
	public static final int TYPE_ARC = 3;
	public static final int TYPE_CIRCLE = 4;
	
	private static final int DEFAULT_STROKECOLOR = Color.parseColor("#009EFC");
	private static final int DEFAULT_CLEARCOLOR = UEHtmlColor.GRAY;
	private static final int DEFAULT_CLEARCENTERCOLOR = UEHtmlColor.WHITE;
	
	private Paint mPaint = new Paint();
	private boolean mClear = false;
	private boolean openClear = true;
	private float clearX = 999;
	private float DP = 0;
	private float centerX = 0;
	private float centerY =  0;
	private float over = 0;
	private float radius = 0;
	private int type = TYPE_LINE;
	private int strokeColor = DEFAULT_STROKECOLOR;
	private int clearColor = DEFAULT_CLEARCOLOR;
	private int clearCenterColor = DEFAULT_CLEARCENTERCOLOR;
//	private int widthMode = -2;
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
			try {
				setStrokeColor(((ColorDrawable)getBackground()).getColor());
				super.setBackgroundColor(Color.TRANSPARENT);
			} catch (Exception e) {
//				super.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		super.addTextChangedListener(mTextChangedListener);
		super.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				if (heightMode < 0) {					
					setPadding(14 * (int)DP, 14 * (int)DP, 14 * (int)DP, 14 * (int)DP);
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
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (heightMeasureSpec < 0) {
			heightMode = -2;
		}else if (heightMeasureSpec == 1073742878) {
			heightMode = -1;
		}else {
			heightMode = 1;
		}
//		if (widthMeasureSpec < 0) {
//			widthMode = -2;
//		}else if (widthMeasureSpec == 1073742464) {
//			widthMode = -1;
//		}else {
//			widthMode = 1;
//		}
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

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mPaint.setAntiAlias(true);
		
		if (mClear) {
			mPaint.setColor(clearColor);
			mPaint.setStyle(Style.FILL);
			mPaint.setAlpha(180);
			centerX = getWidth() - 16 * DP;
			centerY =  getHeight()/2.0f;
			over = 3.5355f * DP;
			canvas.drawCircle(centerX, centerY, 8 * DP, mPaint);
			mPaint.setColor(clearCenterColor);
			mPaint.setStrokeWidth(DP);
			canvas.drawLine(centerX - over, centerY - over, centerX + over, centerY + over, mPaint);
			canvas.drawLine(centerX + over, centerY - over, centerX - over, centerY + over, mPaint);
			clearX = centerX - 10 * DP;
		}
		
		if (getBackground() == null) {
			return;
		}
	
		mPaint.setStrokeWidth(DP);	
		mPaint.setColor(strokeColor);
		
		switch (type) {
		case 3:
			if (radius > getHeight() / 2 || radius < 0) {
				radius = getHeight() / 2;
			}
			mPaint.setStyle(Paint.Style.STROKE);
	        canvas.drawRoundRect(new RectF(2 * DP, 4 * DP, getWidth() - 2 * DP, getHeight() - 4 * DP), radius, radius, mPaint);
	        mPaint.setColor(Color.parseColor("#88F8F8F8"));
	        mPaint.setStyle(Paint.Style.FILL);
	        canvas.drawRoundRect(new RectF(2 * DP, 4 * DP, getWidth() - 2 * DP, getHeight() - 4 * DP), radius, radius, mPaint);
			break;
		case 4:
			radius = getHeight() / 2;
			mPaint.setStyle(Paint.Style.STROKE);
	        canvas.drawRoundRect(new RectF(2 * DP, 4 * DP, getWidth() - 2 * DP, getHeight() - 4 * DP), radius, radius, mPaint);
	        mPaint.setColor(Color.parseColor("#88F8F8F8"));
	        mPaint.setStyle(Paint.Style.FILL);
	        canvas.drawRoundRect(new RectF(2 * DP, 4 * DP, getWidth() - 2 * DP, getHeight() - 4 * DP), radius, radius, mPaint);
			break;
		case 2:
			mPaint.setStyle(Style.STROKE);
			canvas.drawRoundRect(new RectF(2 * DP, 4 * DP, getWidth() - 2 * DP, getHeight() - 4 * DP), DP, DP, mPaint);
			mPaint.setStyle(Style.FILL);
			mPaint.setAlpha(40);
			canvas.drawRoundRect(new RectF(2 * DP, 4 * DP, getWidth() - 2 * DP, getHeight() - 4 * DP), DP, DP, mPaint);
			break;
		case 1:
			mPaint.setStyle(Style.STROKE);
			canvas.drawRoundRect(new RectF(2 * DP, 4 * DP, getWidth() - 2 * DP, getHeight() - 4 * DP), DP, DP, mPaint);
			break;
		default:
			canvas.drawLine(2f * DP, getHeight() - 4 * DP, getWidth() - 2f * DP, getHeight() - 4 * DP, mPaint);
			break;
		}
	}
}
