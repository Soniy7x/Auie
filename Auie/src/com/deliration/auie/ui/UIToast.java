package com.deliration.auie.ui;

import com.deliration.auie.utils.UEMethod;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class UIToast extends Toast{

	public static final long TYPE_FILLET = 0;
	public static final long TYPE_ANGLE = 1;
	
	public UIToast(Context context) {
		super(context);
	}

	private static LinearLayout.LayoutParams getParams(){
        return new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
	
	private static ShapeDrawable createBackground(int color, float radius){
		float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        return shapeDrawable;
	}
	
	private static UIToast builder(Context context, CharSequence text, int color, long type){

		UIToast result = new UIToast(context);
		
		LinearLayout rootLayout = new LinearLayout(context);
		rootLayout.setLayoutParams(getParams());
		rootLayout.setGravity(Gravity.CENTER);
		int padding = UEMethod.dp2px(context, 8);
		TextView textView = new TextView(context);
		textView.setLayoutParams(getParams());
		textView.setPadding(padding * 3, padding, padding * 3, padding);
		textView.setTextSize(14);
		textView.setText(text);
		textView.setTextColor(Color.parseColor("#F8F8F8"));
		if (type == 0) {
			rootLayout.setBackgroundDrawable(createBackground(color, padding * 4 + textView.getHeight()));
		}else {
			rootLayout.setBackgroundColor(color);
		}
		rootLayout.addView(textView);
		
		result.setView(rootLayout);
		
		return result;
	}
	
	public static void show(Context context, CharSequence text){
		show(context, text, Color.parseColor("#99000000"));
	}
	
	public static void show(Context context, CharSequence text, int backgroundColor){
		show(context, text, backgroundColor, TYPE_FILLET);
	}
	
	public static void show(Context context, CharSequence text, long type){
		show(context, text, Color.parseColor("#99000000"), type);
	}
	
	public static void show(Context context, CharSequence text, int backgroundColor, long type){
		builder(context, text, backgroundColor, type).show();
	}
	
	public static void showTime(Context context, CharSequence text, int duration){
		showTime(context, text, Color.parseColor("#99000000"), duration);
	}
	
	public static void showTime(Context context, CharSequence text, int backgroundColor, int duration){
		showTime(context, text, backgroundColor, TYPE_FILLET, duration);
	}
	
	public static void showTime(Context context, CharSequence text, long type, int duration){
		showTime(context, text, Color.parseColor("#99000000"), type, duration);
	}
	
	public static void showTime(Context context, CharSequence text, int backgroundColor, long type, int duration){
		UIToast toast = builder(context, text, backgroundColor, type);
		toast.setDuration(duration);
		toast.show();
	}
	
}
