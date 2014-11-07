package org.auie.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.auie.image.UEImage;
import org.auie.image.UEImageManager.Image;
import org.auie.utils.UEImageNotByteException;
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
	
	private List<View> indexViews = new ArrayList<View>();
	private List<View> imageViews = new ArrayList<View>();
	private ImageAdapter imageAdapter = new ImageAdapter();
	private OnDismissListener onDismissListener;
	
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	
	private int WIDTH = 0;
	private int HEIGHT = 0;
	private int DP = 1;
	
	private int currentIndex = 0;
	
	public UIImagePager(Context context, List<Image> images) throws IOException, UEImageNotByteException{
		init(context);
		transformBitmap(images);
	}
	
	private void transformBitmap(List<Image> images) throws IOException, UEImageNotByteException{
		bitmaps.clear();
		for (Image image : images) {
			bitmaps.add(new UEImage(image.path, true).toBitmap());
		}
		if (bitmaps.size() > 0) {
			createIndexs();
		}
	}
	
	private void createIndexs() {
		if (indexContainer == null) {
			return;
		}
		indexViews.clear();
		indexContainer.removeAllViews();
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(6 * DP, 6 * DP);
		params1.setMargins(6 * DP, 0, 0, 0);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(WIDTH, LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < bitmaps.size(); i++) {
			View view = new View(context);
			view.setLayoutParams(params1);
			if (i == 0) {
				currentIndex = 0;
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
	}
	
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

	private void createView() {
		setBackgroundDrawable(new BitmapDrawable());
		setContentView(createContentView());
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setFocusable(true);
		super.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				if (onDismissListener != null) {
					onDismissListener.onDismiss();
				}
			}
		});
	}
	
	public void setOnDismissListener(OnDismissListener listener){
		this.onDismissListener = listener;
	}

	private View createContentView() {
		
		rootContainer = new RelativeLayout(context);
		rootContainer.setBackgroundColor(Color.parseColor("#000000"));
		rootContainer.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
		
		LayoutParams contentParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		contentParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		contentContainer = new ViewPager(context);
		contentContainer.setLayoutParams(contentParams);
		contentContainer.setAdapter(imageAdapter);
		contentContainer.setOnPageChangeListener(new OnPageChangeListener() {
			
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
		});
		
		LayoutParams params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		indexContainer = new LinearLayout(context);
		indexContainer.setLayoutParams(params);
		indexContainer.setPadding(0, 0, 0, DP * 20);
		indexContainer.setGravity(Gravity.CENTER);
		indexContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		rootContainer.addView(contentContainer);
		rootContainer.addView(indexContainer);
		
		return rootContainer;
	}
	
	public UIImagePager show(){
		showAtLocation(((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0), Gravity.BOTTOM, 0, 0);
		return this;
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
			((ViewPager) container).removeView(imageViews.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(imageViews.get(position));
			return imageViews.get(position);
		}
	}
}
