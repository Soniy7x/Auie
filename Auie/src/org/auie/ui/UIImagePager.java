package org.auie.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.auie.image.UEImage;
import org.auie.image.UEImageManager.Image;
import org.auie.utils.UEException.UEImageNotByteException;
import org.auie.utils.UEMethod;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

@SuppressWarnings("deprecation")
public class UIImagePager extends PopupWindow {
	
	public static final int MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
	public static final int WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
	
	public Drawable NO_SELECTED_DRAWABLE;
	public Drawable SELECTED_DRAWABLE;
	
	private Context context;
	private RelativeLayout rootContainer;
	private ViewPager contentContainer;
	private LinearLayout indexContainer;
	private UIButton actionButton;
	
	private List<View> indexViews = new ArrayList<View>();
	private List<View> imageViews = new ArrayList<View>();
	private ImageAdapter imageAdapter = new ImageAdapter();
	private OnDismissListener onDismissListener;
	private OnActionClickListener onActionClickListener;
	
	private List<?> datas;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	
	private int WIDTH = 0;
	private int HEIGHT = 0;
	private int DP = 1;
	
	private boolean saveFullScreen = true;
	
	private int currentIndex = 0;
	
	/**
	 * 初始化
	 * @param context
	 * @param images
	 * @throws IOException
	 * @throws UEImageNotByteException
	 */
	public UIImagePager(Context context, List<Image> images) throws IOException, UEImageNotByteException{
		init(context);
		this.datas = images;
		transformBitmap(images);
	}
	
	public UIImagePager(Context context, ArrayList<Bitmap> bitmaps, boolean noScreen){
		init(context);
		this.saveFullScreen = noScreen;
		this.datas = bitmaps;
		this.bitmaps = bitmaps;
		if (bitmaps.size() > 0) {
			createIndexs();
		}
	}
	
	/**
	 * Image转Bitmap方法
	 * @param images
	 * @throws IOException
	 * @throws UEImageNotByteException
	 */
	private void transformBitmap(List<Image> images) throws IOException, UEImageNotByteException{
		bitmaps.clear();
		for (Image image : images) {
			bitmaps.add(new UEImage(image.path, true).toBitmap());
		}
		if (bitmaps.size() > 0) {
			//判断图片数量，如果大于0生成索引
			createIndexs();
		}
	}
	
	/**
	 * 外部调用根据position删除视图
	 * @param position
	 */
	public void romoveView(int position){
		bitmaps.remove(position);
		if (bitmaps.size() == 0) {
			dismiss();
		}
		if (currentIndex >= bitmaps.size()) {
			currentIndex = bitmaps.size() - 1;
		}
		contentContainer.removeAllViews();
		createIndexs();
	}
	
	/**
	 * 根据图片数量生成索引项且将当前索引置为0
	 */
	private void createIndexs() {
		if (indexContainer == null) {
			return;
		}
		indexViews.clear();
		imageViews.clear();
		indexContainer.removeAllViews();
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(6 * DP, 6 * DP);
		params1.setMargins(6 * DP, 0, 0, 0);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(WIDTH, LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < bitmaps.size(); i++) {
			View view = new View(context);
			view.setLayoutParams(params1);
			if (i == currentIndex) {
				view.setBackgroundDrawable(SELECTED_DRAWABLE);				
			}else {
				view.setBackgroundDrawable(NO_SELECTED_DRAWABLE);	
			}
			indexViews.add(view);
			indexContainer.addView(view);
			ImageView imageView = new ImageView(context);
			imageView.setLayoutParams(params2);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageView.setImageBitmap(bitmaps.get(i));
			imageViews.add(imageView);
			imageAdapter.notifyDataSetChanged();
		}
		contentContainer.setAdapter(imageAdapter);
		contentContainer.setCurrentItem(currentIndex);
	}
	
	/**
	 * 初始化数据
	 * @param context
	 */
	private void init(Context context){
		((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		this.context = context;
		this.DP = UEMethod.dp2px(context, 1);
		this.NO_SELECTED_DRAWABLE = UEImage.createBackground(Color.parseColor("#FFFFFF"), 50, 6 * DP);
		this.SELECTED_DRAWABLE = UEImage.createBackground(Color.parseColor("#EEEEEE"), 255, 6 * DP);
		this.HEIGHT = manager.getDefaultDisplay().getHeight();
		this.WIDTH = manager.getDefaultDisplay().getWidth();
		createView();
	}

	/**
	 * 初始化视图
	 */
	private void createView() {
		setBackgroundDrawable(new BitmapDrawable());
		setContentView(createContentView());
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setFocusable(true);
		super.setOnDismissListener(dismissListener);
	}
	
	/**
	 * 内部销毁监听器
	 */
	private OnDismissListener dismissListener = new OnDismissListener() {
		
		@Override
		public void onDismiss() {
			if (!saveFullScreen) {				
				((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
			if (onDismissListener != null) {
				//传递至外部
				onDismissListener.onDismiss();
			}
		}
	};
	
	/**
	 * 设置dismiss监听器，供外部调用
	 */
	public void setOnDismissListener(OnDismissListener listener){
		this.onDismissListener = listener;
	}

	/**
	 * 初始化控件
	 */
	private View createContentView() {
		
		rootContainer = new RelativeLayout(context);
		rootContainer.setBackgroundColor(Color.parseColor("#000000"));
		rootContainer.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
		
		LayoutParams contentParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		contentParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		contentContainer = new ViewPager(context);
		contentContainer.setAdapter(imageAdapter);
		contentContainer.setLayoutParams(contentParams);
		contentContainer.setOnPageChangeListener(onPageChangeListener);
		
		LayoutParams params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		indexContainer = new LinearLayout(context);
		indexContainer.setLayoutParams(params);
		indexContainer.setPadding(0, 0, 0, DP * 20);
		indexContainer.setGravity(Gravity.CENTER);
		indexContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutParams params2 = new LayoutParams(60 * DP, 32 * DP);
		params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params2.setMargins(10 * DP, 0, 0, 10 * DP);
		actionButton = new UIButton(context);
		actionButton.setLayoutParams(params2);
		actionButton.setText("删除");
		actionButton.setTextSize(14);
		actionButton.setVisibility(View.GONE);
		actionButton.setTextColor(Color.WHITE);
		actionButton.setBackgroundColor(Color.RED);
		actionButton.setOnClickListener(onClickListener);
		
		rootContainer.addView(contentContainer);
		rootContainer.addView(indexContainer);
		rootContainer.addView(actionButton);
		
		return rootContainer;
	}
	
	/**
	 * 视图切换监听器
	 */
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int index) {
			indexViews.get(currentIndex).setBackgroundDrawable(NO_SELECTED_DRAWABLE);
			indexViews.get(index).setBackgroundDrawable(SELECTED_DRAWABLE);
			currentIndex = index;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		
		@Override
		public void onPageScrollStateChanged(int index) {
			
		}
	};
	
	/**
	 * 操作按钮点击事件
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (onActionClickListener != null) {
				//回传给外部使用
				onActionClickListener.onActionClicked(currentIndex, datas.get(currentIndex));
			}
		}
	};
	
	/**
	 * 控件默认展示
	 * @return
	 */
	public UIImagePager show(){
		showAtLocation(((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0), Gravity.BOTTOM, 0, 0);
		return this;
	}
	
	/**
	 * 显示按钮且添加按钮点击事件处理
	 * @param onActionClickListener
	 */
	public void showAction(OnActionClickListener onActionClickListener){
		actionButton.setVisibility(View.VISIBLE);
		this.onActionClickListener = onActionClickListener;
	}
	
	/**
	 * 隐藏操作按钮
	 */
	public void hideAction(){
		actionButton.setVisibility(View.GONE);
		this.onActionClickListener = null;
	}
	
	/**
	 * 操作按钮点击监听器
	 */
	public interface OnActionClickListener{
		public void onActionClicked(int position, Object object);
	}
	
	class ImageAdapter extends PagerAdapter{
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			if (position < imageViews.size()) {
				((ViewGroup) container).removeView(imageViews.get(position));				
			}
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewGroup) container).addView(imageViews.get(position));
			return imageViews.get(position);
		}
	}
}
