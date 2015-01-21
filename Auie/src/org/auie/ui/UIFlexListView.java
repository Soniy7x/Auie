package org.auie.ui;

import org.auie.utils.UEViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 
 * UIFlexListView
 * 
 * @author Soniy7x
 *
 */
public class UIFlexListView extends FrameLayout implements OnScrollListener{
	
	private RelativeLayout mContentLayout;
	private LinearLayout mHeaderLayout;
	private LinearLayout mHeaderContentLayout;
	private LinearLayout mHeaderActionBarLayout;
	private View mHeaderView;
	private UIListView mListView;
	private OnFlexListener mFlexListener;
	
	private boolean isReset = false;
	private boolean isSuspend = false;
	private int headerHeight;
	
	/**
	 * 构造方法
	 */
	public UIFlexListView(Context context) {
		super(context);
		createContentView();
	}
	
	/**
	 * 构造方法
	 */
	public UIFlexListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		createContentView();
	}

	/**
	 * 构造方法
	 */
	public UIFlexListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		createContentView();
	}

	/**
	 * 构建内容视图
	 */
	private void createContentView() {
		mContentLayout = new RelativeLayout(getContext());
		mContentLayout.setId(1992);
		mContentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mHeaderView = new FrameLayout(getContext());
		mHeaderView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
		mHeaderView.setBackgroundColor(Color.WHITE);
		mHeaderView.setPadding(0, headerHeight, 0, 0);
		mListView = new UIListView(getContext());
		mListView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mListView.setType(UIListView.TYPE_NONE);
		mListView.addHeaderView(mHeaderView);
		mListView.setBackgroundColor(Color.WHITE);
		mListView.setOnScrollListener(this);
		mHeaderLayout = new LinearLayout(getContext());
		mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
		mHeaderLayout.setBackgroundColor(Color.WHITE);
		mHeaderLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mHeaderLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				headerHeight = mHeaderLayout.getHeight();
				resetHeaderView(headerHeight);
			}
		});
		mHeaderContentLayout = new LinearLayout(getContext());
		mHeaderContentLayout.setOrientation(LinearLayout.VERTICAL);
		mHeaderContentLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mHeaderActionBarLayout = new LinearLayout(getContext());
		mHeaderActionBarLayout.setBackgroundColor(Color.GRAY);
		mHeaderActionBarLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mHeaderLayout.addView(mHeaderContentLayout);
		mHeaderLayout.addView(mHeaderActionBarLayout);
		mContentLayout.addView(mListView);
		addView(mContentLayout);
		addView(mHeaderLayout);
	}

	/**
	 * 复写onScroll
	 * @param view
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 * @param totalItemCount
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		float translationY = Math.max(-getScrollY(view), -mHeaderContentLayout.getHeight());
		UEViewHelper.setTranslationY(mHeaderLayout, translationY);
		//判断是否已经悬停
		if (translationY <= -mHeaderContentLayout.getHeight()) {
			isSuspend = true;
		}else {
			isSuspend = false;
		}
		if (mFlexListener != null) {
			mFlexListener.flexStatus(isSuspend);
		}
		if (visibleItemCount == totalItemCount && totalItemCount > 1) {
			isReset = true;
		} else {
			isReset = false;
		}
	}

	/**
	 * 获得滑动距离
	 * @param view
	 * @return
	 */
	private int getScrollY(AbsListView view) {
		View c = view.getChildAt(0);
		if (c == null) {
			return 0;
		}
		int firstVisiblePosition = view.getFirstVisiblePosition();
		int top = c.getTop();
		int headerHeight = 0;
		if (firstVisiblePosition >= 1) {
			headerHeight = mHeaderLayout.getHeight();
		}
		return -top + firstVisiblePosition * c.getHeight() + headerHeight;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			//控制上拉加载更多
			if (isSuspend && view.getLastVisiblePosition() == (view.getCount() - 1)) {
                mListView.setType(UIListView.TYPE_ONLY_UP_LOADMORD);
			}else {
				mListView.setType(UIListView.TYPE_NONE);
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 重设列表状态
	 * @param scrollHeight
	 */
	public void resetListViewState(int scrollHeight){
		if (isReset) {
			return;
		}
		if (mListView == null) {
			return;
		}
		if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
			return;
		}
		mListView.setSelectionFromTop(1, scrollHeight);
	}
	
	/**
	 * 重设头部列表Padding高度
	 * @param height
	 */
	public void resetHeaderView(int height){
		if (mHeaderView != null) {
			mHeaderView.setPadding(0, height, 0, 0);
		}
	}
	
	/**
	 * 获取列表视图
	 */
	public UIListView getListView(){
		return mListView;
	}
	
	/**
	 * 获取内容视图
	 */
	public LinearLayout getContentView(){
		return mHeaderContentLayout;
	}
	
	/**
	 * 获取悬停条视图
	 */
	public LinearLayout getActionBarView(){
		return mHeaderActionBarLayout;
	}
	
	public LinearLayout getHeaderLayout(){
		return mHeaderLayout;
	}
	
	public OnFlexListener getOnFlexListener() {
		return mFlexListener;
	}

	public void setOnFlexListener(OnFlexListener mFlexListener) {
		this.mFlexListener = mFlexListener;
	}

	public static interface OnFlexListener{
		public void flexStatus(boolean status);
	}
}
