package com.deliration.auie.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.deliration.auie.utils.UEMethod;

public class UIViewSwitcher extends LinearLayout {

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	public OnPageChangeListener delegatePageListener;

	private LinearLayout tabsContainer;
	private UISwitcherViewPager pager;

	private int tabCount;

	private int selectedPosition = 0;

	private boolean shouldExpand = true;
	
	private int tabPadding = 24;
	private int tabTextSize = 12;
	
	private int tabTextColor = 0xDD666666;
	private int tabTextSelectedColor = 0xFF4D7EA6;
	private int tabBackgroundColor = 0xFFF8F8F8;
	private int toplineColor = 0x33D8D8D8;
	private Typeface tabTypeface = null;
	private int tabTypefaceStyle = Typeface.NORMAL;
	
	public Drawable drawable;

	public interface SwitchProvider {
		public Drawable getIconDrawable(int position);
		public Drawable getIconSelectedDrawable(int position);
	}
	
	public UIViewSwitcher(Context context) {
		this(context, null);
	}

	public UIViewSwitcher(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UIViewSwitcher(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setWillNotDraw(false);
		
		LinearLayout rootLayout = new LinearLayout(context);
		rootLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		
		View view = new View(getContext());
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, UEMethod.dp2px(getContext(), 0.5f)));
		view.setBackgroundColor(toplineColor);
		
		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		rootLayout.addView(view);
		rootLayout.addView(tabsContainer);
		addView(rootLayout);
		
		defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

	}

	public void setViewPager(UISwitcherViewPager pager) {
		this.pager = pager;

		if (pager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}
		
		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}

	public void notifyDataSetChanged() {

		tabsContainer.removeAllViews();

		tabCount = pager.getAdapter().getCount();

		for (int i = 0; i < tabCount; i++) {
			addTab(i, pager.getAdapter().getPageTitle(i).toString(), ((SwitchProvider) pager.getAdapter()).getIconDrawable(i));
		}

		updateTabStyles();

	}


	private void addTab(final int position, String title, Drawable drawable) {
		
		LinearLayout tab = new LinearLayout(getContext());
		tab.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, UEMethod.dp2px(getContext(), 52)));
		tab.setGravity(Gravity.CENTER);
		tab.setOrientation(LinearLayout.VERTICAL);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, UEMethod.dp2px(getContext(), 30));
		params.setMargins(0, UEMethod.dp2px(getContext(), 4), 0, 0);
		ImageView imageView = new ImageView(getContext());
		imageView.setImageDrawable(drawable);
		imageView.setLayoutParams(params);
		imageView.setScaleType(ScaleType.FIT_CENTER);
		
		TextView textView = new TextView(getContext());
		textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		textView.setText(title);
		textView.setTextSize(tabTextSize);
		textView.setTextColor(tabTextColor);
		textView.setSingleLine();
		
		tab.addView(imageView);
		tab.addView(textView);
		
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(position);
				selectedPosition = position;
				updateTabStyles();
				if (delegatePageListener != null) {
					delegatePageListener.onPageSelected(position);
				}
			}
		});
		
		tab.setPadding(tabPadding, 0, tabPadding, 0);
		tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
	}

	private void updateTabStyles() {

		for (int i = 0; i < tabCount; i++) {

			View v = tabsContainer.getChildAt(i);
			
			v.setBackgroundColor(tabBackgroundColor);
			
			if (v instanceof LinearLayout) {
				View childView = ((ViewGroup) v).getChildAt(0);
				
				if (childView instanceof ImageView) {
					ImageView imageView = (ImageView) childView;
					
					SwitchProvider provider = (SwitchProvider) pager.getAdapter();
					
					if (i == selectedPosition) {
						imageView.setImageDrawable(provider.getIconSelectedDrawable(i));
					}else{
						imageView.setImageDrawable(provider.getIconDrawable(i));
					}
				}
				
				childView = ((ViewGroup) v).getChildAt(1);
				
				if (childView instanceof TextView) {

					TextView textView = (TextView) childView;
					textView.setTypeface(tabTypeface, tabTypefaceStyle);
					textView.setTextColor(tabTextColor);
					textView.setText(textView.getText().toString());
					
					if (i == selectedPosition) {
						textView.setTextColor(tabTextSelectedColor);
					}else{
						textView.setTextColor(tabTextColor);
					}
				}
			}
		}

	}

}
