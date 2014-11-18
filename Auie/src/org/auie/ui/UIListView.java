package org.auie.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ScrollerCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
	private boolean measure = false;
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
	private OnUITouchListener onTouchListener;
	private UIListViewListener listViewListener;
	private int type = TYPE_BOTH;
	
	private int totalItemCount;
	private int scrollBack;
	
	private float pressX;
	private float pressY;
	private int mTouchState;
	private int mTouchPosition;
	private MenuLayout mTouchView;
	private ItemControl mItemControl;
	private OnMenuClickListener mOnMenuClickListener;
	
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
		if (scrollListener instanceof OnUIScrollListener) {
			OnUIScrollListener listener = (OnUIScrollListener) scrollListener;
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
	
	public ItemControl getItemControl() {
		return mItemControl;
	}

	public void setItemControl(ItemControl itemControl) {
		this.mItemControl = itemControl;
	}

	//true说明ListView处于尺寸探测中, false说明尺寸探测完成
	public boolean isMeasure() {
		return measure;
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
				if (mOnMenuClickListener != null) {
					flag = mOnMenuClickListener.onMenuClick(view.getPosition(), menu, index);
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
				onTouchHorizontal(ev);
				ev.setAction(MotionEvent.ACTION_CANCEL);
				super.onTouchEvent(ev);
				return true;
			}
		default:
			//TODO
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
	
	private void onTouchHorizontal(MotionEvent event){
		if (mTouchView != null) {
			mTouchView.onSwipe(event);
			if (!mTouchView.isShow()) {
				mTouchPosition = -1;
				mTouchView = null;
			}
		}
		if (onTouchListener == null) {
			return;
		}
		if (pressX - event.getX() > DISTANCE_X) {
			onTouchListener.onTouchLeft();
		}else {
			onTouchListener.onTouchRight();
		}
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
	
	public void setOnMenuClickListener(OnMenuClickListener mOnMenuItemClickListener) {
		this.mOnMenuClickListener = mOnMenuItemClickListener;
	}

	public OnUITouchListener getOnUITouchListener() {
		return onTouchListener;
	}

	public void setOnUITouchListener(OnUITouchListener onTouchListener) {
		this.onTouchListener = onTouchListener;
	}

	/**
	 * interface
	 */
	public interface OnMenuClickListener {
		boolean onMenuClick(int position, Menu menu, int index);
	}
	
	public interface OnUIScrollListener extends OnScrollListener {
		void onScrolling(View view);
	}

	public interface UIListViewListener {
		void onRefresh();
		void onLoadMore();
	}
	
	public interface OnUITouchListener{
		void onTouchLeft();
		void onTouchRight();
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
	protected class UIListAdapter implements WrapperListAdapter, OnMenuItemClickListener{

		private ListAdapter mAdapter;
		private OnMenuClickListener onMenuClickListener;
		
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
			if (onMenuClickListener != null) {
				onMenuClickListener.onMenuClick(view.getPosition(), menu, index);
			}
		}
		
		public void setOnMenuItemClickListener(OnMenuClickListener onMenuItemClickListener) {
			this.onMenuClickListener = onMenuItemClickListener;
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
	
	/**
	 * UIListViewItem
	 */
	
	public interface ItemControl{
		void createMenu(Menu menu);
	}

	protected interface OnMenuItemClickListener {
		void onItemClick(MenuView view, Menu menu, int index);
	}
	
	protected static class MenuView extends LinearLayout implements OnClickListener {
		
		private MenuLayout mMenuLayout;
		private Menu menu;
		private OnMenuItemClickListener onMenuItemClickListener;
		
		private ItemWidthType widthType = ItemWidthType.ITEM_WIDTH_DEFAULT;
		private int position;
		private int id = 0;
		
		public MenuView(Context context) {
			super(context);
		}
		
		public MenuView(Menu menu) {
			super(menu.getContext());
			this.menu = menu;
			for (Item item : menu.getMenuItems()) {
				addItem(item, id++);
			}
		}
		
		public void setItemWidth(ItemWidthType widthType){
			this.widthType = widthType;
		}
		
		private void addItem(Item item, int id) {
			LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
			final LinearLayout parent = new LinearLayout(getContext());
			parent.setId(id);
			parent.setGravity(Gravity.CENTER);
			parent.setOrientation(LinearLayout.VERTICAL);
			parent.setLayoutParams(params);
			parent.setBackgroundColor(item.backgroundColor);
			parent.setOnClickListener(this);
			parent.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
				
				@Override
				public boolean onPreDraw() {
					parent.getViewTreeObserver().removeOnPreDrawListener(this);
					LayoutParams params = new LayoutParams((int) (getHeight() * widthType.type), LayoutParams.MATCH_PARENT);
					parent.setLayoutParams(params);
					return false;
				}
			});
			addView(parent);
			if (item.icon != null) {
				ImageView imageView = new ImageView(getContext());
				imageView.setImageDrawable(item.icon);
				parent.addView(imageView);
				return;
			}
			if (!TextUtils.isEmpty(item.title)) {
				TextView textView = new TextView(getContext());
				textView.setText(item.title);
				textView.setTextSize(item.titleSize);
				textView.setTextColor(item.titleColor);
				textView.setGravity(Gravity.CENTER);
				textView.setTypeface(item.typeface);
				parent.addView(textView);
			}
			
		}
		
		@Override
		public void onClick(View v) {
			if (onMenuItemClickListener != null && mMenuLayout.isShow()) {
				onMenuItemClickListener.onItemClick(this, menu, v.getId());
			}
		}

		public OnMenuItemClickListener getOnItemClickListener() {
			return onMenuItemClickListener;
		}

		public void setOnItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
			this.onMenuItemClickListener = onMenuItemClickListener;
		}

		public MenuLayout getMenuLayout() {
			return mMenuLayout;
		}

		public void setMenuLayout(MenuLayout mMenuLayout) {
			this.mMenuLayout = mMenuLayout;
		}
		
		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}
		
		public enum ItemWidthType {
			
			ITEM_WIDTH_DEFAULT(1.3F), ITEM_WIDTH_MIN(1F), ITEM_WIDTH_MAX(1.6F);

			private float type;

			private ItemWidthType(float type) {
				this.type = type;
			}

			public float getName() {
				return type;
			}
		}
	}
	
	protected static class MenuLayout extends FrameLayout{

		private static final int CONTENT_VIEW_ID = 1;
		private static final int MENU_VIEW_ID = 2;
		private static final int STATE_HIDE = 0;
		private static final int STATE_SHOW = 1;
		
		private int MIN_FLING = UEMethod.dp2px(getContext(), 15);
		private int MAX_VELOCITYX = UEMethod.dp2px(getContext(), 500);
		
		private GestureDetectorCompat mGestureDetector;
		private View mContentView;
		private MenuView mMenuView;
		private ScrollerCompat mShowScroller;
		private ScrollerCompat mHideScroller;
		
		private boolean isFling;
		private int baseX;
		private int pressX;
		private int position;
		private int state = STATE_HIDE;
		
		private MenuLayout(Context context) {
			super(context);
		}
		
		private MenuLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		public MenuLayout(View contentView, MenuView menuView) {
			super(contentView.getContext());
			this.mContentView = contentView;
			this.mMenuView = menuView;
			this.mMenuView.setMenuLayout(this);
			setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			mGestureDetector = new GestureDetectorCompat(getContext(), mGestureListener);
			LayoutParams contentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mContentView.setLayoutParams(contentParams);
			if (mContentView.getId() < 1) {
				mContentView.setId(CONTENT_VIEW_ID);
			}
			mMenuView.setId(MENU_VIEW_ID);
			mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(mContentView);
			addView(mMenuView);
			mShowScroller = ScrollerCompat.create(getContext());
			mHideScroller = ScrollerCompat.create(getContext());
		}
		
		public boolean isShow() {
			return state == STATE_SHOW;
		}
		
		private void swipe(int dis) {
			if (dis > mMenuView.getWidth()) {
				dis = mMenuView.getWidth();
			}
			if (dis < 0) {
				dis = 0;
			}
			mContentView.layout(-dis, mContentView.getTop(), mContentView.getWidth() - dis, getMeasuredHeight());
			mMenuView.layout(mContentView.getWidth() - dis, mMenuView.getTop(), mContentView.getWidth() + mMenuView.getWidth() - dis, mMenuView.getBottom());
		}

		public boolean onSwipe(MotionEvent event) {
			mGestureDetector.onTouchEvent(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				pressX = (int) event.getX();
				isFling = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int dis = (int) (pressX - event.getX());
				if (state == STATE_SHOW) {
					dis += mMenuView.getWidth();
				}
				swipe(dis);
				break;
			case MotionEvent.ACTION_UP:
				if (isFling || (pressX - event.getX()) > (mMenuView.getWidth() / 2)) {
					smoothOpenMenu();
				} else {
					smoothCloseMenu();
					return false;
				}
				break;
			}
			return true;
		}
		
		@Override
		public void computeScroll() {
			if (state == STATE_SHOW) {
				if (mShowScroller.computeScrollOffset()) {
					swipe(mShowScroller.getCurrX());
					postInvalidate();
				}
			} else {
				if (mHideScroller.computeScrollOffset()) {
					swipe(baseX - mHideScroller.getCurrX());
					postInvalidate();
				}
			}
		}
		
		public void smoothCloseMenu() {
			state = STATE_HIDE;
			baseX = -mContentView.getLeft();
			mHideScroller.startScroll(0, 0, baseX, 0, 350);
			postInvalidate();
		}
		
		public void smoothOpenMenu() {
			state = STATE_SHOW;
			mShowScroller.startScroll(-mContentView.getLeft(), 0, mMenuView.getWidth(), 0, 350);
			postInvalidate();
		}
		
		public void hideMenu() {
			if (mHideScroller.computeScrollOffset()) {
				mHideScroller.abortAnimation();
			}
			if (state == STATE_SHOW) {
				state = STATE_HIDE;
				swipe(0);
			}
		}
		
		public void showMenu() {
			if (state == STATE_HIDE) {
				state = STATE_SHOW;
				swipe(mMenuView.getWidth());
			}
		}
		
		public View getContentView() {
			return mContentView;
		}

		public MenuView getMenuView() {
			return mMenuView;
		}

		public void setMenuHeight(int measuredHeight) {
			LayoutParams params = (LayoutParams) mMenuView.getLayoutParams();
			if (params.height != measuredHeight) {
				params.height = measuredHeight;
				mMenuView.setLayoutParams(mMenuView.getLayoutParams());
			}
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			mMenuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
		}
		
		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			mContentView.layout(0, 0, getMeasuredWidth(), mContentView.getMeasuredHeight());
			mMenuView.layout(getMeasuredWidth(), 0, getMeasuredWidth() + mMenuView.getMeasuredWidth(), mContentView.getMeasuredHeight());
		}
		
		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
			mMenuView.setPosition(position);
		}

		private OnGestureListener mGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				isFling = false;
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				if ((e1.getX() - e2.getX()) > MIN_FLING && velocityX < MAX_VELOCITYX) {
					isFling = true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		};
	}
	
	public static class Menu{
		
		private Context context;
		private List<Item> mItems = new ArrayList<Item>();

		public Menu(Context context){
			this.context = context;
		}
		
		public Context getContext(){
			return this.context;
		}
		
		public void addMenuItem(Item item) {
			mItems.add(item);
		}

		public void removeMenuItem(Item item) {
			mItems.remove(item);
		}

		public List<Item> getMenuItems() {
			return mItems;
		}

		public Item getMenuItem(int index) {
			return mItems.get(index);
		}

	}
	
	public static class Item{
		
		public static final int COLOR_WHITE = Color.parseColor("#FFFFFF");
		public static final int COLOR_BLACK = Color.parseColor("#000000");
		public static final int COLOR_LTGRAY = Color.parseColor("#BDBFBE");
		public static final int COLOR_RED = Color.parseColor("#F24535");
		public static final int COLOR_YELLOW = Color.parseColor("#F2D750");
		public static final int COLOR_GREEN = Color.parseColor("#00B58A");
		public static final int COLOR_BLUE = Color.parseColor("#009EFC");
		public static final int COLOR_PURPLE = Color.parseColor("#772999");
		
		private String title;
		private Drawable icon;
		private int backgroundColor;
		private int titleColor = Color.parseColor("#777777");
		private int titleSize = 14;
		private Typeface typeface = Typeface.DEFAULT;
		
		public Item(String title){
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}

		public Drawable getIcon() {
			return icon;
		}

		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

		public int getTitleColor() {
			return titleColor;
		}

		public void setTitleColor(int titleColor) {
			this.titleColor = titleColor;
		}

		public int getTitleSize() {
			return titleSize;
		}

		public void setTitleSize(int titleSize) {
			this.titleSize = titleSize;
		}

		public int getBackgroundColor() {
			return backgroundColor;
		}

		public void setBackgroundColor(int backgroundColor) {
			this.backgroundColor = backgroundColor;
			this.titleColor = COLOR_WHITE;
		}

		public Typeface getTypeface() {
			return typeface;
		}

		public void setTypeface(Typeface typeface) {
			this.typeface = typeface;
		}
	
	}
}
