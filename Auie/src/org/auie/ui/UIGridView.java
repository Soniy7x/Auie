package org.auie.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

@SuppressLint("NewApi")
public class UIGridView extends GridView {
	
	private boolean measure = false;
	
	public UIGridView(Context context) {
		super(context);
	}

	public UIGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UIGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public UIGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measure = true;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		measure = false;
		super.onLayout(changed, l, t, r, b);
	}

	public boolean isMeasure() {
		return measure;
	}

	public void setMeasure(boolean measure) {
		this.measure = measure;
	}

}
