package org.auie.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.auie.ui.UIListViewItem.Item;
import org.auie.ui.UIListViewItem.Menu;
import org.auie.ui.UIListViewItem.MenuLayout;
import org.auie.ui.UIListViewItem.MenuView;
import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.WrapperListAdapter;
import android.widget.AbsListView.OnScrollListener;

@SuppressLint("NewApi")
public class UIListView extends ListView implements OnScrollListener{
	
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400;
	private final static int PULL_LOAD_MORE_DELTA = 50; 
	private final static float OFFSET_RADIO = 1.8f;
	
	public final static int TYPE_NONE = 92;
	public final static int TYPE_ONLY_DOWN_REFRESH = 93;
	public final static int TYPE_ONLY_UP_LOADMORD = 94;
	public final static int TYPE_BOTH = 95;
	
	private static final int TOUCH_STATE_NONE = 0;
	private static final int TOUCH_STATE_X = 1;
	private static final int TOUCH_STATE_Y = 2;
	
	private int DISTANCE_X = 3;
	private int DISTANCE_Y = 5;
	
	private Scroller scroller;
	private HeaderView headerView;
	private LinearLayout headerViewContent;
	private int headerViewHeight;
	private FooterView footerView;
	private boolean refreshing = false;
	private boolean loading = false;
	private boolean isFooterReady = false;
	private float lastY = -1;
	private OnScrollListener scrollListener;
	private OnItemClickListener onItemClickListener;
	private OnItemLongClickListener onItemLongClickListener;
	private UIListViewListener listViewListener;
	private int type = TYPE_BOTH;
	
	private int totalItemCount;
	private int scrollBack;
	
	private float pressX;
	private float pressY;
	private int mTouchState;
	private int mTouchPosition;
	private MenuLayout mTouchView;
	private UIListViewItem.ItemControl mItemControl;
	private OnMenuItemClickListener mOnMenuItemClickListener;
	
	public UIListView(Context context) {
		super(context);
		init();
	}

	public UIListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UIListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public UIListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	private void init(){
		
		super.setOnScrollListener(this);
		
		this.mTouchState = TOUCH_STATE_NONE;
		DISTANCE_X = UEMethod.dp2px(getContext(), DISTANCE_X);
		DISTANCE_Y = UEMethod.dp2px(getContext(), DISTANCE_Y);
		scroller = new Scroller(getContext(), new DecelerateInterpolator());
		
		headerView = new HeaderView(getContext());
		headerViewContent = headerView.mContent;
		addHeaderView(headerView);
		
		footerView = new FooterView(getContext());

		headerView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				headerViewHeight = headerViewContent.getHeight();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		super.setOnItemClickListener(new UIListViewOnItemClickListener());
		super.setOnItemLongClickListener(new UIListViewOnItemLongClickListener());
	}

	public void setType(int type){
		this.type = type;
		loading = false;
		refreshing = false;
		switch (type) {
		case TYPE_ONLY_DOWN_REFRESH:
			headerView.show();
			footerView.hide();
			break;
		case TYPE_ONLY_UP_LOADMORD:
			headerView.hide();
			footerView.show();
			footerView.setState(FooterView.STATE_NORMAL);
			break;
		case TYPE_BOTH:
			headerView.show();
			footerView.show();
			footerView.setState(FooterView.STATE_NORMAL);
			break;
		default:
			headerView.hide();
			footerView.hide();
			break;
		}
	}
	
	private boolean isCanRefresh(){
		return type == TYPE_BOTH || type == TYPE_ONLY_DOWN_REFRESH;
	}
	
	private boolean isCanLoadmore(){
		return type == TYPE_BOTH || type == TYPE_ONLY_UP_LOADMORD;
	}

	private void invokeOnScrolling() {
		if (scrollListener instanceof UIScrollListener) {
			UIScrollListener listener = (UIScrollListener) scrollListener;
			listener.onScrolling(this);
		}
	}
	
	public void refreshCompleted() {
		if (refreshing == true) {
			refreshing = false;
			resetHeaderHeight();
		}
	}
	
	private void resetHeaderHeight() {
		int height = headerView.getVisiableHeight();
		if (height == 0)
			return;
		if (refreshing && height <= headerViewHeight) {
			return;
		}
		int finalHeight = 0;
		if (refreshing && height > headerViewHeight) {
			finalHeight = headerViewHeight;
		}
		scrollBack = SCROLLBACK_HEADER;
		scroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		invalidate();
	}
	
	private void updateFooterHeight(float delta) {
		int height = footerView.getBottomMargin() + (int) delta;
		if (isCanLoadmore() && !loading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				footerView.setState(FooterView.STATE_READY);
			} else {
				footerView.setState(FooterView.STATE_NORMAL);
			}
		}
		footerView.setBottomMargin(height);
	}
	
	private void resetFooterHeight() {
		int bottomMargin = footerView.getBottomMargin();
		if (bottomMargin > 0) {
			scrollBack = SCROLLBACK_FOOTER;
			scroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		loading = true;
		footerView.setState(FooterView.STATE_LOADING);
		if (listViewListener != null) {
			listViewListener.onLoadMore();
		}
	}

	

	private void updateHeaderHeight(float delta) {
		headerView.setVisiableHeight((int) delta + headerView.getVisiableHeight());
		if (isCanRefresh() && !refreshing) { 
			if (headerView.getVisiableHeight() > headerViewHeight) {
				headerView.setState(HeaderView.STATE_READY);
			} else {
				headerView.setState(HeaderView.STATE_NORMAL);
			}
		}
		setSelection(0);
	}
	
	public void loadMoreCompleted() {
		if (loading == true) {
			loading = false;
			footerView.setState(FooterView.STATE_NORMAL);
		}
	}

	public void setRefreshImage(Drawable drawable){
		if (headerView != null) {
			headerView.mArrowImageView.setImageDrawable(drawable);
		}
	}
	
	public void setRefreshImageResource(int resId){
		if (headerView != null) {
			headerView.mArrowImageView.setImageResource(resId);
		}
	}
	
	public void setRefreshLoadingBarImage(Drawable drawable){
		if (headerView != null) {			
			headerView.mProgressBar.setImage(drawable);
		}
	}
	
	public void setRefreshLoadingBarImageResource(int resId){
		if (headerView != null) {			
			headerView.mProgressBar.setImage(getResources().getDrawable(resId));
		}
	}
	
	public void setRefreshLoadingBar(UILoadingBar bar){
		if (headerView != null) {			
			headerView.mProgressBar = bar;
		}
	}
	
	public void setLoadMoreLoadingBarImage(Drawable drawable){
		if (footerView != null) {			
			footerView.mProgressBar.setImage(drawable);
		}
	}
	
	public void setLoadMoreLoadingBarImageResource(int resId){
		if (footerView != null) {			
			footerView.mProgressBar.setImage(getResources().getDrawable(resId));
		}
	}
	
	public void setLoadMoreLoadingBar(UILoadingBar bar){
		if (footerView != null) {			
			footerView.mProgressBar = bar;
		}
	}
	
	public void setRefreshNormalText(String text){
		if (headerView != null) {			
			headerView.STRING_NORMAL = text;
		}
	}
	
	public void setRefreshReadyText(String text){
		if (headerView != null) {			
			headerView.STRING_READY = text;
		}
	}
	
	public void setRefreshRefreshingText(String text){
		if (headerView != null) {			
			headerView.STRING_REFRESHING = text;
		}
	}
	
	public void setLoadMoreNormalText(String text){
		if (footerView != null) {			
			footerView.STRING_LOAD = text;
		}
	}
	
	public void setRefreshTextColor(int color){
		if (headerView != null) {	
			headerView.mHintTextView.setTextColor(color);
		}
	}
	
	public void setRefreshTimeColor(int color){
		if (headerView != null) {			
			headerView.mTimeTextView.setTextColor(color);
		}
	}
	
	public void setRefreshTextAndTimeColor(int color){
		if (headerView != null) {			
			headerView.mHintTextView.setTextColor(color);
			headerView.mTimeTextView.setTextColor(color);
		}
	}
	
	public void setLoadMoreTextColor(int color){
		if (footerView != null) {			
			footerView.mHintTextView.setTextColor(color);
		}
	}
	
	public void setRefreshShowTime(boolean b){
		if (headerView != null) {	
			headerView.showTime = b;
		}
	}
	
	public UIListViewItem.ItemControl getItemControl() {
		return mItemControl;
	}

	public void setItemControl(UIListViewItem.ItemControl itemControl) {
		this.mItemControl = itemControl;
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		if (isFooterReady == false) {
			isFooterReady = true;
			addFooterView(footerView);
		}
		super.setAdapter(new UIListAdapter(adapter) {
			@Override
			public void createMenu(Menu menu) {
				if (mItemControl != null) {
					mItemControl.createMenu(menu);
				}
			}
			@Override
			public void onItemClick(MenuView view, Menu menu, int index) {
				boolean flag = false;
				if (mOnMenuItemClickListener != null) {
					flag = mOnMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
				}
				if (mTouchView != null && !flag) {
					mTouchView.smoothCloseMenu();
				}
			}
		});
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null){
			return super.onTouchEvent(ev);			
		}
		int action = MotionEventCompat.getActionMasked(ev);
		action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			int oldPos = mTouchPosition;
			lastY = ev.getRawY();
			pressX = ev.getX();
			pressY = ev.getY();
			mTouchState = TOUCH_STATE_NONE;
			mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
			if (mTouchPosition == oldPos && mTouchView != null && mTouchView.isShow()) {
				mTouchState = TOUCH_STATE_X;
				mTouchView.onSwipe(ev);
				return true;
			}

			View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

			if (mTouchView != null && mTouchView.isShow()) {
				mTouchView.smoothCloseMenu();
				mTouchView = null;
				return super.onTouchEvent(ev);
			}
			if (view instanceof MenuLayout) {
				mTouchView = (MenuLayout) view;
			}
			if (mTouchView != null) {
				mTouchView.onSwipe(ev);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = Math.abs((ev.getY() - pressY));
			float dx = Math.abs((ev.getX() - pressX));
			if (mTouchState == TOUCH_STATE_X) {
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
				}
				getSelector().setState(new int[] { 0 });
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			} else if (mTouchState == TOUCH_STATE_NONE) {
				if (Math.abs(dy) > DISTANCE_Y) {
					mTouchState = TOUCH_STATE_Y;
				} else if (dx > DISTANCE_X) {
					mTouchState = TOUCH_STATE_X;
				}
			}
			final float deltaY = ev.getRawY() - lastY;
			lastY = ev.getRawY();
			if (isCanRefresh() && getFirstVisiblePosition() == 0 && (headerView.getVisiableHeight() > 0 || deltaY > 0)) {
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (isCanLoadmore() && getLastVisiblePosition() == totalItemCount - 1 && (footerView.getBottomMargin() > 0 || deltaY < 0)) {
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_X) {
				if (mTouchView != null) {
					mTouchView.onSwipe(ev);
					if (!mTouchView.isShow()) {
						mTouchPosition = -1;
						mTouchView = null;
					}
				}
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			}
		default:
			if (getFirstVisiblePosition() == 0) {
				if (isCanRefresh() && headerView.getVisiableHeight() > headerViewHeight) {
					refreshing = true;
					headerView.setState(HeaderView.STATE_REFRESHING);
					if (listViewListener != null) {
						listViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			}
			if (isCanLoadmore() && getLastVisiblePosition() == totalItemCount - 1) {
				if ((type == TYPE_ONLY_UP_LOADMORD || type == TYPE_BOTH) && footerView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			if (scrollBack == SCROLLBACK_HEADER) {
				headerView.setVisiableHeight(scroller.getCurrY());
			} else {
				footerView.setBottomMargin(scroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setOnItemLongClickListener(
			OnItemLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		scrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollListener != null) {
			scrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.totalItemCount = totalItemCount;
		if (scrollListener != null) {
			scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}
	
	public void setListViewListener(UIListViewListener l) {
		listViewListener = l;
	}
	
	public void smoothOpenMenu(int position) {
		if (position >= getFirstVisiblePosition()
				&& position <= getLastVisiblePosition()) {
			View view = getChildAt(position - getFirstVisiblePosition());
			if (view instanceof MenuLayout) {
				mTouchPosition = position;
				if (mTouchView != null && mTouchView.isShow()) {
					mTouchView.smoothCloseMenu();
				}
				mTouchView = (MenuLayout) view;
				mTouchView.smoothOpenMenu();
			}
		}
	}
	
	public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
		this.mOnMenuItemClickListener = mOnMenuItemClickListener;
	}

	/**
	 * interface
	 */
	public interface OnMenuItemClickListener {
		boolean onMenuItemClick(int position, Menu menu, int index);
	}
	
	public interface UIScrollListener extends OnScrollListener {
		public void onScrolling(View view);
	}

	public interface UIListViewListener {
		public void onRefresh();
		public void onLoadMore();
	}
	
	/**
	 *  Listener
	 */
	class UIListViewOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (onItemClickListener != null) {
				onItemClickListener.onItemClick(parent, view, position - getHeaderViewsCount(), id);
			}
		}
		
	}
	
	class UIListViewOnItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if (onItemLongClickListener != null) {
				onItemLongClickListener.onItemLongClick(parent, view, position - getHeaderViewsCount(), id);
			}
			return false;
		}
	}
	
	/**
	 * HeaderView
	 */
	class HeaderView extends LinearLayout {
		
		private final static int ROTATE_ANIM_DURATION = 180;
	
		private final static int STATE_NORMAL = 0;
		private final static int STATE_READY = 1;
		private final static int STATE_REFRESHING = 2;
	
		private final static  String STRING_REFRESHING_TIME = "最后更新 %1$s";
		public String STRING_NORMAL = "下拉刷新";
		public String STRING_READY = "释放刷新";
		public String STRING_REFRESHING = "加载中...";
		
		public boolean showTime = true;
		
		public LinearLayout mContainer;
		public LinearLayout mContent;
		public ImageView mArrowImageView;
		public UILoadingBar mProgressBar;
		public TextView mHintTextView;
		public TextView mTimeTextView;
		
		private int mState = STATE_NORMAL;
		
		private long mLastTime = -1;
		private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

		private Animation mRotateUpAnim;
		private Animation mRotateDownAnim;
		
		public HeaderView(Context context) {
			super(context);
			initView();
		}
		
		public HeaderView(Context context, AttributeSet attrs) {
			super(context, attrs);
			initView();
		}

		private void initView() {
			
			mContainer = new LinearLayout(getContext());
			mContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0));
			mContainer.setGravity(Gravity.BOTTOM);
			
			mContent = new LinearLayout(getContext());
			mContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UEMethod.dp2px(getContext(), 60)));
			mContent.setOrientation(LinearLayout.HORIZONTAL);
			mContent.setGravity(Gravity.CENTER);
			
			RelativeLayout imageLayout = new RelativeLayout(getContext());
			imageLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
			
			int size = UEMethod.dp2px(getContext(), 36);
			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(size, size);
			params1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			mArrowImageView = new ImageView(getContext());
			mArrowImageView.setLayoutParams(params1);
			
			mProgressBar = new UILoadingBar(getContext());
			mProgressBar.setLayoutParams(params1);
			mProgressBar.setType(UILoadingBar.TYPE_SECTORE_THREE);
			
			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params2.setMargins(UEMethod.dp2px(getContext(), 10), 0, 0, 0);
			LinearLayout textLayout = new LinearLayout(getContext());
			textLayout.setLayoutParams(params2);
			textLayout.setGravity(Gravity.CENTER_VERTICAL);
			textLayout.setOrientation(LinearLayout.VERTICAL);
			
			LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			mHintTextView = new TextView(getContext());
			mHintTextView.setLayoutParams(params3);
			mHintTextView.setTextSize(12);
			mHintTextView.setText(STRING_NORMAL);
			mHintTextView.setTextColor(Color.parseColor("#777777"));
			
			mTimeTextView = new TextView(getContext());
			mTimeTextView.setLayoutParams(params3);
			mTimeTextView.setTextSize(10);
			mTimeTextView.setTextColor(Color.parseColor("#777777"));
			
			textLayout.addView(mHintTextView);
			textLayout.addView(mTimeTextView);
			imageLayout.addView(mArrowImageView);
			imageLayout.addView(mProgressBar);
			mContent.addView(imageLayout);
			mContent.addView(textLayout);
			mContainer.addView(mContent);
			addView(mContainer);
			
			setState(STATE_NORMAL);
				
			mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
			mRotateUpAnim.setFillAfter(true);
			mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
			mRotateDownAnim.setFillAfter(true);
		}
		
		public void setState(int state) {
			if (state == mState) return ;
			
			if (state == STATE_REFRESHING) {
				mArrowImageView.clearAnimation();
				mArrowImageView.setVisibility(GONE);
				mProgressBar.setVisibility(VISIBLE);
			} else {
				mArrowImageView.setVisibility(VISIBLE);
				mProgressBar.setVisibility(GONE);
			}
			
			switch(state){
			case STATE_NORMAL:
				mTimeTextView.setVisibility(GONE);
				if (mState == STATE_READY) {
					mArrowImageView.startAnimation(mRotateDownAnim);
				}
				if (mState == STATE_REFRESHING) {
					mArrowImageView.clearAnimation();
				}
				mHintTextView.setText(STRING_NORMAL);
				break;
			case STATE_READY:
				mTimeTextView.setVisibility(GONE);
				if (mState != STATE_READY) {
					mArrowImageView.clearAnimation();
					mArrowImageView.startAnimation(mRotateUpAnim);
					mHintTextView.setText(STRING_READY);
				}
				break;
			case STATE_REFRESHING:
				if (mLastTime != -1 && showTime) {					
					mTimeTextView.setVisibility(VISIBLE);
					mTimeTextView.setText(String.format(STRING_REFRESHING_TIME, dateFormat.format(new Date(mLastTime))));
				}
				mLastTime = System.currentTimeMillis();
				mHintTextView.setText(STRING_REFRESHING);
				break;
			default:
			}
			mState = state;
		}
	
		public void setVisiableHeight(int height) {
			if (height < 0)
				height = 0;
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
			lp.height = height;
			mContainer.setLayoutParams(lp);
		}

		public int getVisiableHeight() {
			return mContainer.getHeight();
		}

		public void hide(){
			setVisibility(GONE);
		}
		
		public void show(){
			setVisibility(VISIBLE);
		}
	}
	
	/**
	 * FooterView
	 */
	class FooterView extends LinearLayout {
		
		private final static int STATE_NORMAL = 0;
		private final static int STATE_READY = 1;
		private final static int STATE_LOADING = 2;

		public String STRING_LOAD = "松开加载更多";
		
		public LinearLayout mContainer;
		public RelativeLayout mContent;
		public UILoadingBar mProgressBar;
		public TextView mHintTextView;
		
		public FooterView(Context context) {
			super(context);
			initView();
		}
		
		public FooterView(Context context, AttributeSet attrs) {
			super(context, attrs);
			initView();
		}
		
		private void initView() {
			
			mContainer = new LinearLayout(getContext());
			mContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			mContent = new RelativeLayout(getContext());
			mContent.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			int margin = UEMethod.dp2px(getContext(), 10);
			int size = UEMethod.dp2px(getContext(), 36);
			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(size, size);
			params1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params1.setMargins(margin, margin, margin, margin);
			mProgressBar = new UILoadingBar(getContext());
			mProgressBar.setLayoutParams(params1);
			mProgressBar.setType(UILoadingBar.TYPE_SECTORE_THREE);
			
			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params2.setMargins(margin, margin, margin, margin);
			mHintTextView = new TextView(getContext());
			mHintTextView.setLayoutParams(params2);
			
			mContent.addView(mProgressBar);
			mContent.addView(mHintTextView);
			mContainer.addView(mContent);
			addView(mContainer);
			
			setState(STATE_NORMAL);
		}
		
		public void setState(int state) {
			mHintTextView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
			mHintTextView.setVisibility(View.INVISIBLE);
			if (state == STATE_READY) {
				mHintTextView.setVisibility(View.VISIBLE);
				mHintTextView.setText(STRING_LOAD);
			} else if (state == STATE_LOADING) {
				mProgressBar.setVisibility(View.VISIBLE);
			} else {
				mHintTextView.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.GONE);
			}
		}
		
		public void setBottomMargin(int height) {
			if (height < 0) return ;
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContainer.getLayoutParams();
			lp.bottomMargin = height;
			mContainer.setLayoutParams(lp);
		}
		
		public int getBottomMargin() {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContainer.getLayoutParams();
			return lp.bottomMargin;
		}
		
		public void normal() {
			mHintTextView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}
		
		public void loading() {
			mHintTextView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}
		
		public void hide() {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContainer.getLayoutParams();
			lp.height = 0;
			mContainer.setLayoutParams(lp);
		}
		
		public void show() {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContainer.getLayoutParams();
			lp.height = LayoutParams.WRAP_CONTENT;
			mContainer.setLayoutParams(lp);
		}	
	}
	
	/**
	 *  UIListAdapter
	 */
	protected class UIListAdapter implements WrapperListAdapter, UIListViewItem.OnItemClickListener{

		private ListAdapter mAdapter;
		private OnMenuItemClickListener onMenuItemClickListener;
		
		public UIListAdapter(ListAdapter adapter) {
			mAdapter = adapter;
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return mAdapter.areAllItemsEnabled();
		}

		@Override
		public boolean isEnabled(int position) {
			return mAdapter.isEnabled(position);
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			mAdapter.registerDataSetObserver(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			mAdapter.unregisterDataSetObserver(observer);
		}

		@Override
		public int getCount() {
			return mAdapter.getCount();
		}

		@Override
		public Object getItem(int position) {
			return mAdapter.getItem(position);
		}

		@Override
		public long getItemId(int position) {
			return mAdapter.getItemId(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MenuLayout menuLayout;
			if (convertView == null) {
				View contentView = mAdapter.getView(position, convertView, parent);
				Menu menu = new Menu(getContext());
				createMenu(menu);
				MenuView menuView = new MenuView(menu);
				menuView.setOnItemClickListener(this);
				menuLayout = new MenuLayout(contentView, menuView);
			} else {
				menuLayout = (MenuLayout) convertView;
				menuLayout.hideMenu();
				mAdapter.getView(position, menuLayout.getContentView(), parent);
			}
			menuLayout.setPosition(position);
			return menuLayout;
		}

		public void createMenu(Menu menu) {
			menu.addMenuItem(new Item("Item 1"));
			menu.addMenuItem(new Item("Item 2"));
		}

		@Override
		public void onItemClick(MenuView view, Menu menu, int index) {
			if (onMenuItemClickListener != null) {
				onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
			}
		}
		
		public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
			this.onMenuItemClickListener = onMenuItemClickListener;
		}

		@Override
		public boolean hasStableIds() {
			return mAdapter.hasStableIds();
		}

		@Override
		public int getItemViewType(int position) {
			return mAdapter.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			return mAdapter.getViewTypeCount();
		}

		@Override
		public boolean isEmpty() {
			return mAdapter.isEmpty();
		}

		@Override
		public ListAdapter getWrappedAdapter() {
			return mAdapter;
		}
		
	}
}
