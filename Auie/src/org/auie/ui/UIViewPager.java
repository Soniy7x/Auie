package org.auie.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UIViewPager extends ViewPager{
	
	private boolean enableScroll = true;
	
    public UIViewPager(Context context) {  
        super(context);  
    }  
  
    public UIViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);
    }

    public boolean isEnableScroll() {
		return enableScroll;
	}

	public void setEnableScroll(boolean enableScroll) {
		this.enableScroll = enableScroll;
	}

	/**
     * 禁止响应滑动事件
     */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!enableScroll) {			
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void scrollTo(int x, int y) {
		if (enableScroll) {
			super.scrollTo(x, y);
		}
	}
    
	
}
