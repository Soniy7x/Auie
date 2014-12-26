package org.auie.ui;

import java.util.ArrayList;

import org.auie.image.UEImage;
import org.auie.utils.UEUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class UIImageCarouselView extends RelativeLayout {
	
	private ViewPager mViewPager;
	private LinearLayout mIndexLayout;
	
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private ArrayList<ImageView> imageViews = new ArrayList<>();
	private ArrayList<View> indexViews = new ArrayList<>();
	private ImageCarouselAdapter adapter = new ImageCarouselAdapter();
	private ScaleType scaleType = ScaleType.FIT_XY;
	private LinearLayout.LayoutParams mIndexParams;
	private UEUnit mUnit;
	private int currentIndex = 0;
	private boolean loop = true;
	private boolean auto = true;
	private long carouseTime = 1800;
	private int indexSize = 8;
	private Drawable indexDefaultDrawable;
	private Drawable indexSelectDrawable;
	private int handlerIndex = 0;
	private Handler mHandler = new Handler();
	
	public UIImageCarouselView(Context context) {
		this(context, null);
	}

	public UIImageCarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
	}

	public UIImageCarouselView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UIImageCarouselView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	/**
	 * 设置图片源
	 * @param bitmaps
	 */
	public void setImages(ArrayList<Bitmap> bitmaps){
		this.bitmaps = bitmaps;
		createIndexs();
	}
	
	/**
	 * 设置图片原ID
	 * @param ids
	 */
	public void setImagesResource(ArrayList<Integer> ids){
		bitmaps.clear();
		for(int id :  ids){
			bitmaps.add(new UEImage(getResources(), id).toBitmap());
		}
		createIndexs();
	}
	
	/**
	 * 构造视图并初始化
	 */
	private void init(){
		mUnit = UEUnit.getInstance(getContext());
		mIndexParams = new LinearLayout.LayoutParams(mUnit.translatePX(indexSize), mUnit.translatePX(indexSize));
		mIndexParams.setMargins(mUnit.translatePX(8), 0, 0, 0);
		mViewPager = new ViewPager(getContext());
		mViewPager.setLayoutParams(new LayoutParams(-1, -1));
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
		params.addRule(CENTER_HORIZONTAL, RelativeLayout.TRUE);
		params.addRule(ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.setMargins(0, 0, 0, mUnit.translatePX(10));
		mIndexLayout = new LinearLayout(getContext());
		mIndexLayout.setLayoutParams(params);
		indexDefaultDrawable = UEImage.createBackground(Color.parseColor("#FFFFFF"), 50, mUnit.translatePX(6));
		indexSelectDrawable = UEImage.createBackground(Color.parseColor("#EEEEEE"), 255, mUnit.translatePX(6));
		addView(mViewPager);
		addView(mIndexLayout);
	}
	
	/**
	 * 创建ViewPager填充控件
	 * @return
	 */
	private ImageView createImageView(){
		ImageView iv = new ImageView(getContext());
		iv.setLayoutParams(new LayoutParams(-1, -1));
		iv.setScaleType(scaleType);
		return iv;
	}
	
	/**
	 * 创建索引视图
	 */
	@SuppressWarnings("deprecation")
	public void createIndexs(){
		imageViews.clear();
		indexViews.clear();
		mIndexLayout.removeAllViews();
		int length = bitmaps.size();
		for (int i = 0; i < length; i++) {
			View view = new View(getContext());
			view.setLayoutParams(mIndexParams);
			view.setBackgroundDrawable(indexDefaultDrawable);
			indexViews.add(view);
			mIndexLayout.addView(view);
		}
		length = loop ? bitmaps.size() + 2 : bitmaps.size();
		for (int i = 0; i < length; i++) {
			imageViews.add(createImageView());
		}
		adapter.notifyDataSetChanged();
		currentIndex = loop ? 1 : 0;
		handlerIndex = currentIndex;
		mViewPager.setCurrentItem(currentIndex, false);
		indexViews.get(currentIndex).setBackgroundDrawable(indexSelectDrawable);
		mHandler.removeCallbacks(runnable);
		if (auto) {
			mHandler.post(runnable);
		}
	}
	
	/**
	 * 是否循环
	 * @return
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * 设置是否循环
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
		if (bitmaps.size() > 0) {			
			createIndexs();
		}
	}

	/**
	 * 是否自动
	 * @return
	 */
	public boolean isAuto() {
		return auto;
	}

	/**
	 * 设置是否自动
	 * @param auto
	 */
	public void setAuto(boolean auto) {
		this.auto = auto;
		if (bitmaps.size() > 0) {			
			createIndexs();
		}
	}
	
	/**
	 * 设置图片缩放方式
	 * @param scaleType
	 */
	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
	}

	/**
	 * 设置自动切换时间间隔
	 * @param carouseTime
	 */
	public void setCarouseTime(long carouseTime) {
		this.carouseTime = carouseTime;
	}

	/**
	 * 设置索引大小
	 * @param indexSize
	 */
	public void setIndexSize(int indexSize) {
		this.indexSize = indexSize;
		mIndexParams = new LinearLayout.LayoutParams(mUnit.translatePX(indexSize), mUnit.translatePX(indexSize));
		mIndexParams.setMargins(mUnit.translatePX(8), 0, 0, 0);
	}

	/**
	 * 设置索引默认图片
	 * @param indexDefaultDrawable
	 */
	public void setIndexDefaultDrawable(Drawable indexDefaultDrawable) {
		this.indexDefaultDrawable = indexDefaultDrawable;
	}

	/**
	 * 设置索引选中图片
	 * @param indexSelectDrawable
	 */
	public void setIndexSelectDrawable(Drawable indexSelectDrawable) {
		this.indexSelectDrawable = indexSelectDrawable;
	}
	
	/**
	 * 隐藏索引
	 */
	public void hideIndexs(){
		mIndexLayout.setVisibility(GONE);
	}
	
	/**
	 * 显示索引
	 */
	public void showIndexs(){
		mIndexLayout.setVisibility(VISIBLE);
	}

	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onPageSelected(int position) {
			if (loop) {
				if (position == 0) {
					mViewPager.setCurrentItem(imageViews.size() - 2, false);
				}else if (position == (imageViews.size() - 1)) {
					mViewPager.setCurrentItem(1, false);
				}else {
					indexViews.get(currentIndex).setBackgroundDrawable(indexDefaultDrawable);
					indexViews.get(position - 1).setBackgroundDrawable(indexSelectDrawable);
					currentIndex = position - 1;
				}
			}else {
				indexViews.get(currentIndex).setBackgroundDrawable(indexDefaultDrawable);
				indexViews.get(position).setBackgroundDrawable(indexSelectDrawable);
				currentIndex = position;
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {}
	};
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			mViewPager.setCurrentItem(handlerIndex);
			handlerIndex++;
			if (loop) {
				if (handlerIndex >= imageViews.size()) {
					handlerIndex = 1;
				}
			}else {
				if (handlerIndex >= bitmaps.size()) {
					handlerIndex = 0;
				}
			}
			mHandler.postDelayed(runnable, carouseTime);
		}
	};
	
	class ImageCarouselAdapter extends PagerAdapter{
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {}

		@Override
		public Object instantiateItem(View container, int position) {
			ViewGroup parent = (ViewGroup) imageViews.get(position).getParent();
			if (parent == null) {
				((ViewGroup) container).addView(imageViews.get(position));
			}
			if (!loop) {
				imageViews.get(position).setImageBitmap(bitmaps.get(position));
			}else {
				if (position == 0) {
					imageViews.get(position).setImageBitmap(bitmaps.get(bitmaps.size() - 1));
				}else if (position == imageViews.size() - 1) {
					imageViews.get(position).setImageBitmap(bitmaps.get(0));
				}else {
					imageViews.get(position).setImageBitmap(bitmaps.get(position - 1));
				}
			}
			return imageViews.get(position);
		}
		 
	}
	
}
