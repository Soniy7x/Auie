package org.auie.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

@SuppressLint("NewApi")
public class UILoadingBar extends View {

	public static final int SPEED_NORMAL = 600;
	public static final int SPEED_SLOWLY = 800;
	public static final int SPEED_QUICKLY = 400;
	
	public static final int TYPE_NONE = 0;
	public static final int TYPE_SECTORE_ONE = 1;
	public static final int TYPE_SECTORE_TWO = 2;
	public static final int TYPE_SECTORE_THREE = 3;
	public static final int TYPE_ARC = 4;
	public static final int TYPE_OVAL = 5;
	
	private Paint paint = new Paint();
	private float strokeWidth = 2f;
	private int[] colors = { Color.RED, Color.YELLOW};
	private int speed = SPEED_NORMAL;
	private int type = TYPE_SECTORE_ONE;
	private Animation animation;

	public UILoadingBar(Context context) {
		super(context);
		createView();
	}
	
	public UILoadingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		createView();
	}
	
	public UILoadingBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		createView();
	}

	private void createView(){
		
		super.setBackgroundColor(Color.TRANSPARENT);
		
		LinearInterpolator interpolator = new LinearInterpolator();
		animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(speed);
		animation.setRepeatCount(-1);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setInterpolator(interpolator);
		setAnimation(animation);
	}
	
	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int[] getColors() {
		return colors;
	}

	public void setColors(int[] colors) {
		this.colors = colors;
	}
	
	public void setColors(int color) {
		this.colors = new int[]{color};
	}
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void setBackgroundColor(int color){
		super.setBackgroundColor(color);
		setColors(new int[]{color});
	}
	
	public void setImage(Drawable drawable){
		setBackground(drawable);
		setType(TYPE_NONE);
	}

	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		switch (visibility) {
		case VISIBLE:
			setAnimation(animation);
			break;
		case INVISIBLE:
		case GONE:
			clearAnimation();
			break;
		default:
			break;
		}
	};
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE);
		if (colors.length < 2) {
			paint.setColor(colors[0]);
		}else {
			Shader shader = new LinearGradient(0, 0, 100, 100, colors, null, Shader.TileMode.CLAMP);  
			paint.setShader(shader);  			
		}
        RectF oval = new RectF(6, 6, getWidth() - 6, getHeight() - 6);
        
        switch (type) {
		case TYPE_SECTORE_ONE:
	        canvas.drawArc(oval, 0, 320, true, paint);
			break;
		case TYPE_SECTORE_TWO:
			canvas.drawArc(oval, 0, 270, true, paint);
			break;
		case TYPE_SECTORE_THREE:
			canvas.drawArc(oval, 0, 359, true, paint);
			break;
		case TYPE_ARC:
			canvas.drawArc(oval, 0, 280, false, paint);
			break;
		case TYPE_OVAL:
			canvas.drawOval(oval, paint);
			paint.setStyle(Paint.Style.FILL);
			oval.set(12, 12, getWidth() - 12, getHeight() - 12);
			canvas.drawOval(oval, paint);
			break;
		default:
			break;
		}
	}
	
	
}
