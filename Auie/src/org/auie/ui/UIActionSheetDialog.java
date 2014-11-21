package org.auie.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class UIActionSheetDialog extends PopupWindow{
	
	private Context context;
	private String title;
	private Typeface typeface;
	private LinearLayout rootLayout;
	private LinearLayout contentLayout;
	private LinearLayout parentLayout;
	private ScrollView sheetLayout;
	private List<SheetItem> sheetItemList;
	private Display display;
	private float scale;
	private OnActionSheetClickListener onActionSheetClickListener;
	
	private int backgroundColor = Color.parseColor("#00000000");
	private int titleColor = Color.parseColor("#8F8F8F");
	private int cancelColor = Color.parseColor("#3DB399");

	public UIActionSheetDialog(Context context, Typeface typeface) {
		this(context);
		this.typeface = typeface;
	}

	public UIActionSheetDialog(Context context) {
		this.context = context;
		display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		this.scale = context.getResources().getDisplayMetrics().density;
	}
	
	@SuppressWarnings("deprecation")
	private View createContentView(){
		//根布局
		rootLayout = new LinearLayout(context);
		rootLayout.setLayoutParams(getParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		rootLayout.setBackgroundColor(Color.parseColor("#77000000"));
		rootLayout.setGravity(Gravity.BOTTOM);
		
		parentLayout = new LinearLayout(context);
		parentLayout.setLayoutParams(getParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		parentLayout.setOrientation(LinearLayout.VERTICAL);
		parentLayout.setBackgroundColor(backgroundColor);
		
		LinearLayout childLayout = new LinearLayout(context);
		LayoutParams childParams = getParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		childParams.setMargins(dp2px(8), 0, dp2px(8), 0);
		childLayout.setLayoutParams(childParams);
		childLayout.setOrientation(LinearLayout.VERTICAL);
		childLayout.setBackgroundDrawable(createBackground(Color.parseColor("#CCFFFFFF"), 10));
		
		//标题
		TextView titleTextView = new TextView(context);
		titleTextView.setLayoutParams(getParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		titleTextView.setPadding(0, dp2px(10), 0, dp2px(10));
		titleTextView.setMinHeight(dp2px(45));
		titleTextView.setTextSize(14);
		titleTextView.setGravity(Gravity.CENTER);
		titleTextView.setTextColor(titleColor);
		if (title == null) {
			titleTextView.setVisibility(View.GONE);
		}else{
			titleTextView.setVisibility(View.VISIBLE);
			titleTextView.setText(title);
		}
		
		//内容外层布局
		sheetLayout = new ScrollView(context);
		sheetLayout.setLayoutParams(getParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		sheetLayout.setFadingEdgeLength(0);
		
		//内容内层布局
		contentLayout = new LinearLayout(context);
		contentLayout.setLayoutParams(getParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		sheetLayout.addView(contentLayout);
		
		//取消按钮
		final TextView cancelTextView = new TextView(context);
		LayoutParams params = getParams(LayoutParams.MATCH_PARENT, dp2px(45));
		params.setMargins(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
		cancelTextView.setLayoutParams(params);
		cancelTextView.setTextColor(cancelColor);
		cancelTextView.setTextSize(16);
		cancelTextView.setGravity(Gravity.CENTER);
		cancelTextView.setText("取消");
		cancelTextView.setBackgroundDrawable(createBackground(Color.parseColor("#CCFFFFFF"), 10));
		cancelTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		
		//控件创建结束
		childLayout.addView(titleTextView);
		childLayout.addView(sheetLayout);
		parentLayout.addView(childLayout);
		parentLayout.addView(cancelTextView);
		rootLayout.addView(parentLayout);
		if (typeface != null) {
			titleTextView.setTypeface(typeface);
			cancelTextView.setTypeface(typeface);
		}
		return rootLayout;
	}
	
	private ShapeDrawable createBackground(int color, float radius){
		float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        return shapeDrawable;
	}
	
	@SuppressWarnings("deprecation")
	private UIActionSheetDialog builder() {
		setBackgroundDrawable(new BitmapDrawable());
		setContentView(createContentView());
		setWidth(display.getWidth());
		setHeight(android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		setFocusable(true);
		return this;
	}

	private int dp2px(float dp){
        return (int) (dp * scale + 0.5f);
    }
	
	private LinearLayout.LayoutParams getParams(int width, int height){
        return new LinearLayout.LayoutParams(width, height);
    }
	
	public UIActionSheetDialog setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public UIActionSheetDialog addSheetItem(String strItem, int color, OnSheetItemClickListener listener) {
		if (sheetItemList == null) {
			sheetItemList = new ArrayList<SheetItem>();
		}
		sheetItemList.add(new SheetItem(strItem, color, listener));
		return this;
	}
	
	public UIActionSheetDialog addSheetItem(String strItem, int color) {
		return addSheetItem(strItem, color);
	}

	@SuppressWarnings("deprecation")
	private void setSheetItems() {
		if (sheetItemList == null || sheetItemList.size() <= 0) {
			return;
		}

		int size = sheetItemList.size();
		if (size >= 7) {
			LinearLayout.LayoutParams params = (LayoutParams) sheetLayout.getLayoutParams();
			params.height = display.getHeight() / 2;
			sheetLayout.setLayoutParams(params);
		}

		for (int i = 1; i <= size; i++) {
			final int index = i;
			SheetItem sheetItem = sheetItemList.get(i - 1);
			String itemName = sheetItem.name;
			int color = sheetItem.color;
			final OnSheetItemClickListener listener = (OnSheetItemClickListener) sheetItem.itemClickListener;

			TextView textView = new TextView(context);
			textView.setText(itemName);
			textView.setTextSize(16);
			textView.setGravity(Gravity.CENTER);
			if (typeface != null) {
				textView.setTypeface(typeface);
			}

			textView.setTextColor(color);

			textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dp2px(45)));

			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						listener.onClick();
					}
					if (onActionSheetClickListener != null) {
						onActionSheetClickListener.onClick(index);
					}
					dismiss();
				}
			});

			View view = new View(context);
			LayoutParams params = getParams(LayoutParams.MATCH_PARENT, dp2px(0.5f));
			params.setMargins(dp2px(2), 0, dp2px(2), 0);
			view.setLayoutParams(params);
			view.setBackgroundColor(Color.parseColor("#33444444"));
			
			contentLayout.addView(view);
			contentLayout.addView(textView);
		}
	}

	@SuppressWarnings("deprecation")
	public void show() {
		builder();
		setSheetItems();
		AlphaAnimation animation1 = new AlphaAnimation(0.3f, 1.0f);
		animation1.setDuration(200);
		TranslateAnimation animation = new TranslateAnimation(0, 0, display.getHeight(), 0);
		animation.setDuration(320);
		rootLayout.startAnimation(animation1);
		parentLayout.startAnimation(animation);
		showAtLocation(((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0), Gravity.BOTTOM, 0, 0);
	}

	public Typeface getTypeface() {
		return typeface;
	}

	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}
	
	public void setOnActionSheetClickListener(OnActionSheetClickListener onActionSheetClickListener) {
		this.onActionSheetClickListener = onActionSheetClickListener;
	}

	public interface OnSheetItemClickListener {
		void onClick();
	}
	
	public interface OnActionSheetClickListener {
		void onClick(int which);
	}

	public class SheetItem {
		String name;
		OnSheetItemClickListener itemClickListener;
		int color;

		public SheetItem(String name, int color, OnSheetItemClickListener itemClickListener) {
			this.name = name;
			this.color = color;
			this.itemClickListener = itemClickListener;
		}
	}
}
