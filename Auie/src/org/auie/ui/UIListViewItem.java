package org.auie.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class UIListViewItem extends RelativeLayout implements OnGestureListener,OnTouchListener{
	
	public static final int ACTION_DELETE = 1;
	public static final int ACTION_DELETE_ONE = 2;
	public static final int ACTION_DELETE_TWO = 3;
	public static final int ACTION_CUSTOMER = 9;
	
	private String actionString = "按钮1";
	private String otherString = "按钮2";
	private int actionColor = Color.parseColor("#009EFC");
	private int otherColor = Color.parseColor("#BDBFBE");
	private int type = ACTION_DELETE;
	
	private LinearLayout actionView;
	private LinearLayout contentView;
	
	private GestureDetector gesture;
	
	private UIButton deleteButton;
	private UIButton actionButton;
	private UIButton otherButton;
	
	private View customerView;
	
	private boolean showAction = false;
	private boolean showActionEnable = true;
	
	public UIListViewItem(Context context) {
		super(context);
		createView();
	}
	
	public UIListViewItem(Context context, View view) {
		super(context);
		createView();
		setContentView(view);
	}
	
	public UIListViewItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		createView();
	}
	
	public UIListViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}
	
	public UIListViewItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		createView();
	}
	
	private void createView(){
		
		gesture = new GestureDetector(getContext(), this);
		
		contentView = new LinearLayout(getContext());
		contentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		actionView = new LinearLayout(getContext());
		actionView.setOrientation(LinearLayout.HORIZONTAL);
		actionView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				actionView.getViewTreeObserver().removeOnPreDrawListener(this);
				setType(type);
				return false;
			}
		});
		
		addView(contentView);
		addView(actionView);
	}

	private void createActionView(){
		
		actionView.removeAllViews();
		
		
		int width = getHeight() * (type < 4 ? type : 3);
		
		LayoutParams params = new LayoutParams(width, getHeight());
		params.addRule(ALIGN_PARENT_RIGHT, TRUE);
		params.setMargins(0, 0,  -width, 0);
		actionView.setLayoutParams(params);
		switch (type) {
		case ACTION_CUSTOMER:
			customerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
			actionView.addView(customerView);
			break;
		case ACTION_DELETE_TWO:
			otherButton = new UIButton(getContext());
			otherButton.setRadius(0);
			otherButton.setText(otherString);
			otherButton.setTextColor(Color.WHITE);
			otherButton.setBackgroundColor(otherColor);
			otherButton.setLayoutParams(new LayoutParams(getHeight(), getHeight()));
		case ACTION_DELETE_ONE:
			actionButton = new UIButton(getContext());
			actionButton.setRadius(0);
			actionButton.setText(actionString);
			actionButton.setTextColor(Color.WHITE);
			actionButton.setBackgroundColor(actionColor);
			actionButton.setLayoutParams(new LayoutParams(getHeight(), getHeight()));
			
		default:
			deleteButton = new UIButton(getContext());
			deleteButton.setRadius(0);
			deleteButton.setText("删除");
			deleteButton.setTextColor(Color.WHITE);
			deleteButton.setBackgroundColor(Color.parseColor("#F24535"));
			deleteButton.setLayoutParams(new LayoutParams(getHeight(), getHeight()));
			break;
		}
		
		if (otherButton != null) {
			actionView.addView(otherButton);
		}
		if (actionButton != null) {
			actionView.addView(actionButton);
		}
		if (deleteButton != null) {
			actionView.addView(deleteButton);
		}
	}
	
	public void setContentView(View view){
		view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		contentView.addView(view);
		contentView.setOnTouchListener(this);
	}
	
	public View getContentView(){
		return contentView.getChildAt(0);
	}

	public View getCustomerView() {
		return customerView;
	}

	public void setCustomerView(View customerView) {
		this.customerView = customerView;
		this.type = ACTION_CUSTOMER;
	}

	public boolean isShowActionEnable() {
		return showActionEnable;
	}

	public void setShowActionEnable(boolean showActionEnable) {
		this.showActionEnable = showActionEnable;
	}

	
	public String getActionString() {
		return actionString;
	}

	public void setActionString(String actionString) {
		this.actionString = actionString;
	}

	public String getOtherString() {
		return otherString;
	}

	public void setOtherString(String otherString) {
		this.otherString = otherString;
	}

	public int getActionColor() {
		return actionColor;
	}

	public void setActionColor(int actionColor) {
		this.actionColor = actionColor;
	}

	public int getOtherColor() {
		return otherColor;
	}

	public void setOtherColor(int otherColor) {
		this.otherColor = otherColor;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
		createActionView();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gesture.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		View view = (View) getParent();
		int x = (int) e.getRawX();
		int y = (int) e.getRawY();
		if (showAction && view instanceof UIListView) {
			UIListView listView = (UIListView) view;
			int[] location = new  int[2] ;
			final float rawX = x+ actionView.getWidth();
			final UIListViewAdpater adpater = listView.getUIListViewAdpater();
			final int position = listView.pointToPosition(x, y) - listView.getHeaderViewsCount();
			if (adpater == null) {
				return false;
			}
			if (deleteButton != null) {
				deleteButton.getLocationInWindow(location);
				if (rawX > location[0] && rawX < location[0] + deleteButton.getWidth()) {
					AnimationListener animationListener = new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {}
						
						@Override
						public void onAnimationRepeat(Animation animation) {}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							adpater.deleteClick(position - 1);
						}
					};
					deleteAction(animationListener);
					return false;
				}
			}
			if (otherButton != null) {
				otherButton.getLocationInWindow(location);
				if (rawX > location[0] && rawX < location[0] + otherButton.getWidth()) {
					adpater.otherClick(position - 1);
					hideAction();
					return false;
				}
			}
			if (actionButton != null) {
				actionButton.getLocationInWindow(location);
				if (rawX > location[0] && rawX < location[0] + actionButton.getWidth()) {
					adpater.actionClick(position - 1);
					hideAction();
					return false;
				}
			}
			if (customerView != null) {
				customerView.getLocationInWindow(location);
				if (rawX > location[0] && rawX < location[0] + customerView.getWidth()) {
					adpater.customerClick(position - 1, customerView);
					hideAction();
					return false;
				}
			}
		}
		if (view instanceof UIListView) {
			UIListView listView = (UIListView) view;
			int position = listView.pointToPosition(x, y) - listView.getHeaderViewsCount();
			listView.getOnItemClickListener().onItemClick(listView, listView.getChildAt(position), position , position);
		}else if (view instanceof ListView) {
			ListView listView = (ListView) view;
			int position = listView.pointToPosition(x, y);
			listView.getOnItemClickListener().onItemClick(listView, listView.getChildAt(position), position, position);
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (!showActionEnable) {
			return false;
		}
		if (e1.getX() - e2.getX() > 10) {
			showAction();
		}else if(e2.getX() - e1.getX() > 10){
			hideAction();
		}
		return false;
	}

	private void showAction(){
		TranslateAnimation animation = new TranslateAnimation(0, -actionView.getWidth(), 0, 0);
		animation.setDuration(400);
		animation.setFillAfter(true);
		
		contentView.startAnimation(animation);
		actionView.startAnimation(animation);
		
		this.showAction = true;
	}

	private void hideAction(){
		TranslateAnimation animation = new TranslateAnimation(-actionView.getWidth(), 0, 0, 0);
		animation.setDuration(400);
		animation.setFillAfter(true);
		
		contentView.startAnimation(animation);
		actionView.startAnimation(animation);
		
		this.showAction = false;
	}
	
	private void deleteAction(AnimationListener animationListener){
		TranslateAnimation animation = new TranslateAnimation(0, getWidth(), 0, 0);
		animation.setDuration(600);
		animation.setFillBefore(true);
		animation.setAnimationListener(animationListener);
		startAnimation(animation);
		hideAction();
	}
}
