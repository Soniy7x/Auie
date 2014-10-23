package com.deliration.auie.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UISwitcherViewPager extends ViewPager{
	  
    public UISwitcherViewPager(Context context) {  
        super(context);  
    }  
  
    public UISwitcherViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }

    /**
     * 禁止响应滑动事件
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
    
}
