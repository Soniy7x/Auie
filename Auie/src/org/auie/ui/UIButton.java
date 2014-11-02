package org.auie.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility") 
@SuppressWarnings("deprecation")
public class UIButton extends RelativeLayout implements OnTouchListener{
	
	public static final int RADIUS_MIN = Integer.MIN_VALUE;
	public static final int RADIUS_MAX = Integer.MAX_VALUE;
	
	private LinearLayout rootLayout;
	private ImageView imageView;
	private TextView textView;
	
	private boolean showStroke = false;
	
	private float radius = 5;
	private float strokeWidth = 0.7f;
	
	private int backgroundColor = Color.parseColor("#D8D8D8");
	private int textColor = Color.parseColor("#F8F8F8");
	private int strokeColor = Color.parseColor("#D8D8D8");
	
	private int textSize = 14;
	private String text = "UIButton";
	
	public UIButton(Context context) {
		super(context);
		onCreate(null);
	}

	public UIButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate(attrs);
	}

	public UIButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onCreate(attrs);
	}
	
	private void onCreate(AttributeSet attrs){
		
		setBackgroundDrawable(createStateColor());
		setOnTouchListener(this);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
		rootLayout = new LinearLayout(getContext());
		rootLayout.setLayoutParams(params);
		rootLayout.setGravity(Gravity.CENTER);
		rootLayout.setBackgroundColor(Color.TRANSPARENT);
		rootLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		
		textView = new TextView(getContext());
		textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		textView.setText(text);
		textView.setTextSize(textSize);
		textView.setTextColor(textColor);
		textView.setSingleLine(true);
		
		int size = (int) textView.getTextSize();
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(size / 2 * 5, size / 2 * 5);
		params2.setMargins(0, 0, size / 2, 0);
		imageView = new ImageView(getContext());
		imageView.setScaleType(ScaleType.FIT_CENTER);
		imageView.setVisibility(View.GONE);
		imageView.setLayoutParams(params2);
		
		rootLayout.addView(imageView);
		rootLayout.addView(textView);
		addView(rootLayout);
	}
	
	private ShapeDrawable createBackground(int color, int alpha){
		float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setAlpha(alpha);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        return shapeDrawable;
	}
	
	public int getBackgroundColor(){
		return backgroundColor;
	}
	
	public void setBackgroundColor(int color){
		this.backgroundColor = color;
		super.setBackgroundDrawable(createStateColor());
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
		this.textView.setTextColor(textColor);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.textView.setText(text);
	}
	
	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		setBackgroundDrawable(createStateColor());
	}
	
	public boolean isShowStroke() {
		return showStroke;
	}

	public void setShowStroke(boolean showStroke) {
		this.showStroke = showStroke;
	}

	public Drawable getImage(){
		return imageView.getDrawable();
	}

	public void setImageDrawable(Drawable drawable){
		imageView.setImageDrawable(drawable);
		imageView.setVisibility(VISIBLE);
	}
	
	public void setImageResource(int resId){
		imageView.setImageResource(resId);
		imageView.setVisibility(VISIBLE);
	}
	
	public void setImageBitmap(Bitmap bitmap){
		imageView.setImageBitmap(bitmap);
		imageView.setVisibility(VISIBLE);
	}
	
	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(textSize / 2 * 5, textSize / 2 * 5);
		params.setMargins(0, 0, textSize / 2, 0);
		textView.setTextSize(textSize);
		imageView.setLayoutParams(params);
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

	private StateListDrawable createStateColor(){
		StateListDrawable colors = new StateListDrawable();
		colors.addState(View.PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET, createBackground(backgroundColor, 188));
		colors.addState(View.EMPTY_STATE_SET, createBackground(backgroundColor, 255));
		return colors;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getBackground().setState(View.PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET);
			super.onTouchEvent(event);
			return true;
		case MotionEvent.ACTION_UP:
			getBackground().setState(View.EMPTY_STATE_SET);
			return false;
		default:
			getBackground().setState(View.PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET);
			return false;
		}
	}
	
	@SuppressLint("DrawAllocation") 
	@Override  
    protected void onDraw(Canvas canvas)  
    {  
        super.onDraw(canvas);
        
        if (showStroke) {
        	
        	Paint paint = new Paint(); 
        	paint.setAntiAlias(true);
            paint.setColor(strokeColor);
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(Paint.Style.STROKE);
            
            float radius = getRadius() > getHeight() ? getHeight() - 40 : getRadius();
            
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), radius, radius, paint);  
		}
    }
	
}
