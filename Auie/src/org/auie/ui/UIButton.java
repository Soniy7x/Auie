package org.auie.ui;

import org.auie.image.UEImage;
import org.auie.utils.UEDevice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RemoteViews.RemoteView;

/**
 * 
 * 按钮类
 * 
 * @author Soniy7x
 *
 */
@RemoteView
@SuppressLint("NewApi")
public class UIButton extends Button {

	private static final int DEFAULT_BACKGROUNDCOLOR = Color.parseColor("#D8D8D8");

	private int backgroundColor = DEFAULT_BACKGROUNDCOLOR;
	private Bitmap bitmap;
	private float radius = 5;
	private float distanceX = 0;
	private float distanceY = 0;
	private float alpha = 0;
	private Paint mPaint = new Paint();

	/**
	 * 构造方法
	 */
	public UIButton(Context context) {
		super(context);
		init();
	}

	/**
	 * 构造方法
	 */
	public UIButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 构造方法
	 */
	public UIButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	/**
	 * 构造方法
	 */
	public UIButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	/**
	 * 初始化方法
	 */
	private void init() {
		if (UEDevice.getOSVersionCode() >= 11) {
			try {
				setBackgroundColor(((ColorDrawable) getBackground()).getColor());
			} catch (Exception e) {
				setBackgroundColor(backgroundColor);
			}
		} else {
			setBackgroundColor(backgroundColor);
		}
		alpha = getAlpha();
		setGravity(Gravity.CENTER);
	}

	/**
	 * 设置背景颜色
	 * @param backgroundColor
	 */
	@SuppressWarnings("deprecation")
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		if (UEDevice.getOSVersionCode() >= 16) {
			super.setBackground(UEImage.createBackground(backgroundColor, radius));
		} else {
			super.setBackgroundDrawable(UEImage.createBackground(backgroundColor, radius));
		}
	}

	/**
	 * 获得圆角大小
	 * @return 圆角大小
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * 设置圆角大小
	 * @param radius 圆角大小
	 */
	public void setRadius(float radius) {
		this.radius = radius;
		setBackgroundColor(backgroundColor);
	}
	
	/**
	 * 设置图片资源
	 * @param resId 资源ID
	 */
	public void setImageResource(int resId){
		setImage(new UEImage(getResources(), resId).toBitmap());
	}
	
	/**
	 * 设置图片资源
	 * @param bitmap 图片
	 */
	public void setImage(Bitmap bitmap){
		this.bitmap = bitmap;
		this.setText("");
		invalidate();
	}
	
	/**
	 * 设置文字内容
	 * @param text 内容
	 */
	public void setText(String text){
		if (bitmap == null) {
			super.setText(text);
		}
	}

	/**
	 * 设置控件透明度
	 * @param alpha
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
		super.setAlpha(alpha);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			super.setAlpha(alpha - 0.3f);
			super.onTouchEvent(event);
			return true;
		case MotionEvent.ACTION_UP:
			super.setAlpha(alpha);
			return super.onTouchEvent(event);
		default:
			super.setAlpha(alpha - 0.3f);
			return super.onTouchEvent(event);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (bitmap != null) {
			distanceX = (float) ((getWidth() - bitmap.getWidth())/2.0);
			distanceY = (float) ((getHeight() - bitmap.getHeight())/2.0);
			canvas.drawBitmap(bitmap, distanceX, distanceY, mPaint);
		}
	}
	
}
