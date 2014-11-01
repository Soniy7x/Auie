package org.auie.ui;

import java.util.ArrayList;
import java.util.List;

import org.auie.utils.UEMethod;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class UIListViewItem {

	public interface ItemControl{
		void createMenu(Menu menu);
	}

	protected interface OnItemClickListener {
		void onItemClick(MenuView view, Menu menu, int index);
	}
	
	protected static class MenuView extends LinearLayout implements OnClickListener {
		
		private MenuLayout mMenuLayout;
		private Menu menu;
		private OnItemClickListener onItemClickListener;
		
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
			if (onItemClickListener != null && mMenuLayout.isShow()) {
				onItemClickListener.onItemClick(this, menu, v.getId());
			}
		}

		public OnItemClickListener getOnItemClickListener() {
			return onItemClickListener;
		}

		public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
			this.onItemClickListener = onItemClickListener;
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
