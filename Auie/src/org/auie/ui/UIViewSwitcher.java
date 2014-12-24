package org.auie.ui;

import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UIViewSwitcher extends LinearLayout {

	private LinearLayout.LayoutParams expandedTabLayoutParams;

	public OnPageChangeListener delegatePageListener;

	private LinearLayout tabsContainer;
	private UIViewPager pager;

	private int tabCount;
	private int selectedPosition = 0;

	private boolean autoNotice = true;
	private boolean autoAnimate = false;
	
	private int tabPadding = 24;
	private int tabTextSize = 12;
	private int tabImageWidth = 32;
	private int tabImageHeight = 32;
	
	private int tabTextColor = 0xDD666666;
	private int tabTextSelectedColor = 0xFF45C01A;
	private int tabBackgroundColor = 0xFFF8F8F8;
	private int tabBackgroundSelectColor = Color.TRANSPARENT;
	private int toplineColor = 0x33D8D8D8;
	
	private Typeface tabTypeface = null;
	private int tabTypefaceStyle = Typeface.NORMAL;

	public interface SwitchProvider {
		public Drawable getIconDrawable(int position);
		public Drawable getIconSelectedDrawable(int position);
	}
	
	public UIViewSwitcher(Context context) {
		super(context);
		createView();
	}

	public UIViewSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		createView();
	}

	@SuppressLint("NewApi")
	public UIViewSwitcher(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createView();
	}
	
	private void createView(){

		setBackgroundColor(Color.LTGRAY);
		
		LinearLayout rootLayout = new LinearLayout(getContext());
		rootLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		
		View view = new View(getContext());
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, UEMethod.dp2px(getContext(), 0.5f)));
		view.setBackgroundColor(toplineColor);
		
		tabsContainer = new LinearLayout(getContext());
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setBackgroundColor(tabBackgroundColor);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		rootLayout.addView(view);
		rootLayout.addView(tabsContainer);
		addView(rootLayout);
		
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
	}

	public void setViewPager(UIViewPager pager) {
		this.pager = pager;
		this.pager.setEnableScroll(false);

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
		tab.setOrientation(LinearLayout.HORIZONTAL);
		
		RelativeLayout tabContent = new RelativeLayout(getContext());
		tabContent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(UEMethod.dp2px(getContext(), tabImageWidth), UEMethod.dp2px(getContext(), tabImageHeight));
		params.setMargins(0, UEMethod.dp2px(getContext(), 4), 0, 0);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		ImageView imageView = new ImageView(getContext());
		imageView.setImageDrawable(drawable);
		imageView.setLayoutParams(params);
		imageView.setId(1);
		imageView.setScaleType(ScaleType.FIT_CENTER);
		
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params2.addRule(RelativeLayout.BELOW, 1);
		params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params2.setMargins(0, 0, 0, 6);
		TextView textView = new TextView(getContext());
		textView.setLayoutParams(params2);
		textView.setText(title);
		textView.setTextSize(tabTextSize);
		textView.setTextColor(tabTextColor);
		textView.setSingleLine();
		if (TextUtils.isEmpty(title)) {
			textView.setVisibility(View.GONE);
		}
		
		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(UEMethod.dp2px(getContext(), 10), UEMethod.dp2px(getContext(), 10));
		params3.addRule(RelativeLayout.RIGHT_OF, 1);
		params3.setMargins(0, UEMethod.dp2px(getContext(), 6), 0, 0);
		ImageView noticeImageView = new ImageView(getContext());
		noticeImageView.setLayoutParams(params3);
		noticeImageView.setScaleType(ScaleType.FIT_CENTER);
		
		tabContent.addView(imageView);
		tabContent.addView(textView);
		tabContent.addView(noticeImageView);
		tab.addView(tabContent);
		
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setEnableScroll(true);
				pager.setCurrentItem(position, autoAnimate);
				pager.setEnableScroll(false);
				selectedPosition = position;
				updateTabStyles();
				if (delegatePageListener != null) {
					delegatePageListener.onPageSelected(position);
				}
			}
		});
		
		tab.setPadding(tabPadding, 0, tabPadding, 0);
		tabsContainer.addView(tab, position, expandedTabLayoutParams);
	}
	
	public void addNotice(int position, Drawable drawable){
		View v = tabsContainer.getChildAt(position);
		if (v instanceof LinearLayout) {
			View parentView = ((ViewGroup) v).getChildAt(0);
			View noticeView = ((ViewGroup) parentView).getChildAt(2);
			if (noticeView instanceof ImageView) {
				((ImageView) noticeView).setImageDrawable(drawable);
			}
		}
	}
	
	public void removeNotice(int position){
		View v = tabsContainer.getChildAt(position);
		if (v instanceof LinearLayout) {
			View parentView = ((ViewGroup) v).getChildAt(0);
			View noticeView = ((ViewGroup) parentView).getChildAt(2);
			if (noticeView instanceof ImageView) {
				((ImageView) noticeView).setImageDrawable(null);
			}
		}
	}
	
	private void updateTabStyles() {

		for (int i = 0; i < tabCount; i++) {

			View v = tabsContainer.getChildAt(i);
			
			if (i == selectedPosition) {
				v.setBackgroundColor(tabBackgroundSelectColor);
			}else{
				v.setBackgroundColor(Color.TRANSPARENT);
			}
			
			if (v instanceof LinearLayout) {
				View parentView = ((ViewGroup) v).getChildAt(0);
				View childView = ((ViewGroup) parentView).getChildAt(0);
				
				if (childView instanceof ImageView) {
					ImageView imageView = (ImageView) childView;
					
					SwitchProvider provider = (SwitchProvider) pager.getAdapter();
					
					if (i == selectedPosition) {
						imageView.setImageDrawable(provider.getIconSelectedDrawable(i));
					}else{
						imageView.setImageDrawable(provider.getIconDrawable(i));
					}
				}
				
				childView = ((ViewGroup) parentView).getChildAt(1);
				
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
				
				childView = ((ViewGroup) parentView).getChildAt(2);
				if (childView instanceof ImageView) {
					if (autoNotice && i == selectedPosition) {
						((ImageView) childView).setImageDrawable(null);
					}
				}
			}
		}

	}

	public OnPageChangeListener getDelegatePageListener() {
		return delegatePageListener;
	}

	public void setDelegatePageListener(OnPageChangeListener delegatePageListener) {
		this.delegatePageListener = delegatePageListener;
	}

	public int getTabTextSize() {
		return tabTextSize;
	}

	public void setTabTextSize(int tabTextSize) {
		this.tabTextSize = tabTextSize;
	}

	public boolean isAutoNotice() {
		return autoNotice;
	}

	public void setAutoNotice(boolean autoNotice) {
		this.autoNotice = autoNotice;
	}
	
	public void setAutoAnimate(boolean autoAnimate) {
		this.autoAnimate = autoAnimate;
	}

	public int getTabTextColor() {
		return tabTextColor;
	}

	public void setTabTextColor(int tabTextColor) {
		this.tabTextColor = tabTextColor;
	}

	public int getTabTextSelectedColor() {
		return tabTextSelectedColor;
	}

	public void setTabTextSelectedColor(int tabTextSelectedColor) {
		this.tabTextSelectedColor = tabTextSelectedColor;
	}

	public int getTabBackgroundColor() {
		return tabBackgroundColor;
	}

	public void setTabBackgroundColor(int tabBackgroundColor) {
		this.tabBackgroundColor = tabBackgroundColor;
		tabsContainer.setBackgroundColor(tabBackgroundColor);
	}

	public int getTabBackgroundSelectColor() {
		return tabBackgroundSelectColor;
	}

	public void setTabBackgroundSelectColor(int tabBackgroundSelectColor) {
		this.tabBackgroundSelectColor = tabBackgroundSelectColor;
	}

	public int getToplineColor() {
		return toplineColor;
	}

	public void setToplineColor(int toplineColor) {
		this.toplineColor = toplineColor;
	}

	public Typeface getTabTypeface() {
		return tabTypeface;
	}

	public void setTabTypeface(Typeface tabTypeface) {
		this.tabTypeface = tabTypeface;
	}

	public int getTabTypefaceStyle() {
		return tabTypefaceStyle;
	}

	public void setTabTypefaceStyle(int tabTypefaceStyle) {
		this.tabTypefaceStyle = tabTypefaceStyle;
	}

	public UIViewPager getPager() {
		return pager;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}
	
	public void setTabImageSize(int tabImageSize) {
		this.tabImageWidth = tabImageSize;
		this.tabImageHeight = tabImageSize;
	}
	
	public void setTabImageSize(int tabImageWidth, int tabImageHeight) {
		this.tabImageWidth = tabImageWidth;
		this.tabImageHeight = tabImageHeight;
	}

	public int getTabImageWidth() {
		return tabImageWidth;
	}

	public void setTabImageWidth(int tabImageWidth) {
		this.tabImageWidth = tabImageWidth;
	}

	public int getTabImageHeight() {
		return tabImageHeight;
	}

	public void setTabImageHeight(int tabImageHeight) {
		this.tabImageHeight = tabImageHeight;
	}
	
}
