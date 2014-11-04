package org.auie.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

@SuppressLint("DrawAllocation")
@TargetApi(Build.VERSION_CODES.L)
public class UIEditText extends EditText {
	
	public static final float TYPE_NONE = 0;
	public static final float TYPE_ARC = 5;
	public static final float TYPE_CIRCLE = -1;
	
	private float type = TYPE_ARC;
 	private float strokeWidth = 0.8f;
 	private int strokeColor = Color.parseColor("#A8A8A8");
	private Paint paint = new Paint();
	
	public UIEditText(Context context) {
		super(context);
		createView();
	}

	public UIEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		createView();
	}

	public UIEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		createView();
	}

	public UIEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		createView();
	}

	private void createView(){
		setBackgroundColor(Color.TRANSPARENT);
	}

	public float getRadius() {
		return type;
	}

	public void setType(float type) {
		this.type = type;
	}
	
	public void setRadius(float radius) {
		this.type = radius;
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (type == TYPE_NONE) {
			return;
		}
		if (type > getHeight() / 2 || type < TYPE_NONE) {
			type = TYPE_CIRCLE;
		}
		if (type == TYPE_CIRCLE) {
			type = (float) (getHeight() / 2.0);
		}
		paint.setAntiAlias(true);
		paint.setColor(strokeColor);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(1, 1, getWidth() - 1, getHeight() - 1), type, type, paint);
        paint.setColor(Color.parseColor("#88F8F8F8"));
		paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(strokeWidth + 1, strokeWidth + 1, getWidth() - strokeWidth - 1, getHeight() - strokeWidth - 1), type, type, paint);
	}
}
