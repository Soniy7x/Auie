package org.auie.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.auie.image.UEImageLoader;
import org.auie.image.UEImageLoader.OnUEImageLoadListener;
import org.auie.image.UEImageManager;
import org.auie.image.UEImageManager.Bucket;
import org.auie.image.UEImageManager.Image;
import org.auie.utils.UEAdapter;
import org.auie.utils.UEException.UEImageNotByteException;
import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class UIPhotoSelector extends PopupWindow {
	
	private static final int MATCH_PARENT1 = LinearLayout.LayoutParams.MATCH_PARENT;
	private static final int MATCH_PARENT2 = RelativeLayout.LayoutParams.MATCH_PARENT;
	
	public static final int PHOTO_CAMERA = 1992;
	
	private Context context;
	private UIGridView photoGridView;
	private ListView bucketListView;
	private LinearLayout rootContainer;
	private RelativeLayout topContainer;
	private UIButton topLeftButton;
	private UIButton topRightButton;
	private RelativeLayout bottomContainer;
	private UIButton bottomLeftButton;
	private UIButton bottomRightButton;
	private View topLineView;
	
	private int WIDTH = 0;
	private int HEIGHT = 0;
	private int IMAGE_SIZE = 0;
	private int DP = 1;
	private int COUNT_MAX = 99;
	
	private List<Image> selectImages = new ArrayList<>();
	private List<Image> images;
	private List<Bucket> buckets;
	private UEImageManager manager;
	private ImageAdapter adapter;
	private BucketAdapter bucketAdapter;
	
	private UEImageLoader mImageLoader;
	
	private OnUIPhotoSelectorListener selectorListener;
	
	public UIPhotoSelector(Context context, OnUIPhotoSelectorListener listener) {
		this(context, 99, listener);
	}
	
	public UIPhotoSelector(Context context, int count, OnUIPhotoSelectorListener listener) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams attrs = ((Activity) context).getWindow().getAttributes();
		this.context = context;
		this.selectorListener = listener;
		this.COUNT_MAX = count;
		this.DP = UEMethod.dp2px(context, 1);
		this.mImageLoader = UEImageLoader.getInstance(context);
		if((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN){
			this.HEIGHT = manager.getDefaultDisplay().getHeight();
		}else {
			this.HEIGHT = manager.getDefaultDisplay().getHeight() - 24 * DP;
		}
		this.WIDTH = manager.getDefaultDisplay().getWidth();
		this.IMAGE_SIZE = (WIDTH - 4 * DP) / 3;
		createView();
	}
	
	private void createView(){
		setBackgroundDrawable(new BitmapDrawable());
		setContentView(createContentView());
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setFocusable(true);
		initDatas();
	}
	
	private void initDatas(){
		manager = UEImageManager.getInstance(context);
		buckets = manager.getTempBuckets(true);
		bucketAdapter = new BucketAdapter(buckets);
		bucketListView.setAdapter(bucketAdapter);
		for (Bucket bucket : buckets) {
			if (bucket.name.toLowerCase(Locale.getDefault()).contains("camera") || bucket.name.contains("相机")) {
				images = bucket.images;
			}
		}
		if (images == null && buckets.size() > 0) {	
			images = buckets.get(buckets.size() - 1).images;
		}
		adapter = new ImageAdapter(images);
		photoGridView.setAdapter(adapter);
	}
	
	private OnClickListener topLeftClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
            ((Activity)context).startActivityForResult(intent, PHOTO_CAMERA);
		}
	};
	
	private OnClickListener topRightClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (selectImages.size() > 0) {
				try {
					new UIImagePager(context, selectImages).show();
				} catch (IOException | UEImageNotByteException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private void showOrHideBuckets(){
		if (bucketListView.getVisibility() == View.GONE) {
			TranslateAnimation animation = new TranslateAnimation(0, 0, 278 * DP, 0);
			animation.setDuration(280);
			bottomContainer.setAnimation(animation);
			bucketListView.setAnimation(animation);
			bucketListView.setVisibility(View.VISIBLE);
		}else {
			TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 278 * DP);
			animation.setDuration(120);
			bottomContainer.setAnimation(animation);
			bucketListView.setAnimation(animation);
			bucketListView.setVisibility(View.GONE);
		}
	}
	
	private OnClickListener bottomLeftClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			showOrHideBuckets();
		}
	};
	
	private float y = 0;
	private boolean isTouching = false;
	
	private OnTouchListener onTouchListener = new OnTouchListener() {		
		
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				y = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				isTouching = false;
				break;
			default:
				if (Math.abs(y - event.getY()) > 10 * DP && !isTouching) {
					isTouching = true;
					showOrHideBuckets();
				}
				break;
			}
			return true;
		}
	};
	
	private OnClickListener bottomRightClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (selectImages.size() > 0 && selectorListener != null) {
				selectorListener.onSelectCompleted(selectImages);
				dismiss();
			}
		}
	};
	
	private OnItemClickListener bucketOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectImages.clear();
			images = buckets.get(position).images;
			adapter.refresh(images);
			bucketListView.setVisibility(View.GONE);
		}
	};
	
	private OnItemClickListener photoOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ImageView imageView = (ImageView) ((RelativeLayout)view).getChildAt(1);
			if (imageView.getVisibility() == View.INVISIBLE) {
				if (selectImages.size() >= COUNT_MAX) {
					UIToast.show(context, "只能选择" + COUNT_MAX + "照片");
					return;
				}
				imageView.setVisibility(View.VISIBLE);
				selectImages.add(images.get(position));
			}else {
				imageView.setVisibility(View.INVISIBLE);
				selectImages.remove(images.get(position));
			}
			topRightButton.setText("预览(" + selectImages.size() + ")");
			if (selectImages.size() > 0) {
				topRightButton.setTextColor(Color.parseColor("#EFEFEF"));
				bottomRightButton.setVisibility(View.VISIBLE);
			}else {
				topRightButton.setTextColor(Color.parseColor("#5F5F5F"));
				bottomRightButton.setVisibility(View.GONE);
			}
		}
	};
	
	private LinearLayout.LayoutParams getParams(int width, int height, int weight){
        return new LinearLayout.LayoutParams(width, height, weight);
    }
	
	private LinearLayout.LayoutParams getParams1(int width, int height){
        return new LinearLayout.LayoutParams(width, height);
    }
	
	private RelativeLayout.LayoutParams getParams2(int width, int height){
        return new RelativeLayout.LayoutParams(width, height);
    }
	
	private View createContentView(){
		/**
		 * 根视图容器
		 */
		rootContainer = new LinearLayout(context);
		rootContainer.setLayoutParams(getParams1(MATCH_PARENT1, MATCH_PARENT1));
		rootContainer.setBackgroundColor(Color.parseColor("#191919"));
		rootContainer.setOrientation(LinearLayout.VERTICAL);
		
		/**
		 * 顶栏
		 */
		topContainer = new RelativeLayout(context);
		topContainer.setLayoutParams(getParams1(MATCH_PARENT1, 48 * DP));
		topContainer.setBackgroundColor(Color.parseColor("#2e3334"));
		
		LayoutParams topLeftParams = (LayoutParams) getParams2(66 * DP, MATCH_PARENT2);
		topLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		topLeftParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		topLeftButton = new UIButton(context);
		topLeftButton.setLayoutParams(topLeftParams);
		topLeftButton.setTextSize(34);
		topLeftButton.setImageResource(android.R.drawable.ic_menu_camera);
		topLeftButton.setBackgroundColor(Color.parseColor("#2e3334"));
		topLeftButton.setOnClickListener(topLeftClickListener);
		
		LayoutParams topRightParams = (LayoutParams) getParams2(80 * DP, 30 * DP);
		topRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		topRightParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		topRightButton = new UIButton(context);
		topRightButton.setId(1992);
		topRightButton.setText("预览");
		topRightButton.setTextColor(Color.parseColor("#5F5F5F"));
		topRightButton.setBackgroundColor(Color.parseColor("#2e3334"));
		topRightButton.setLayoutParams(topRightParams);
		topRightButton.setOnClickListener(topRightClickListener);
		
		LayoutParams topLineParams = (LayoutParams) getParams2((int)(0.8 * DP), MATCH_PARENT2);
		topLineParams.addRule(RelativeLayout.LEFT_OF, 1992);
		topLineParams.setMargins(0, 8 * DP, 0, 8 * DP);
		topLineView = new View(context);
		topLineView.setLayoutParams(topLineParams);
		topLineView.setBackgroundColor(Color.parseColor("#555555"));
		
		topContainer.addView(topLeftButton);
		topContainer.addView(topRightButton);
		topContainer.addView(topLineView);
		
		/**
		 * 相片视图
		 */
		LinearLayout.LayoutParams photoParams = getParams(MATCH_PARENT1, 0, 1);
		photoParams.setMargins(0, (int)(0.8 * DP), 0, (int)(0.8 * DP));
		photoGridView = new UIGridView(context);
		photoGridView.setLayoutParams(photoParams);
		photoGridView.setBackgroundColor(Color.parseColor("#242424"));
		photoGridView.setHorizontalSpacing(2 * DP);
		photoGridView.setVerticalSpacing(2 * DP);
		photoGridView.setNumColumns(3);
		photoGridView.setVerticalScrollBarEnabled(false);
		photoGridView.setOnItemClickListener(photoOnItemClickListener);
		
		/**
		 * 底栏
		 */
		bottomContainer = new RelativeLayout(context);
		bottomContainer.setLayoutParams(getParams1(MATCH_PARENT1, 48 * DP));
		bottomContainer.setBackgroundColor(Color.parseColor("#2e3334"));
		bottomContainer.setOnTouchListener(onTouchListener);
		
		LayoutParams bottomLeftParams = (LayoutParams) getParams2(90 * DP, MATCH_PARENT2);
		bottomLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		bottomLeftParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		bottomLeftButton = new UIButton(context);
		bottomLeftButton.setLayoutParams(bottomLeftParams);
		bottomLeftButton.setText("切换相册");
		bottomLeftButton.setTextColor(Color.parseColor("#B8B8B8"));
		bottomLeftButton.setBackgroundColor(Color.parseColor("#2e3334"));
		bottomLeftButton.setOnClickListener(bottomLeftClickListener);

		LayoutParams bottomRightParams = (LayoutParams) getParams2(60 * DP, 30 * DP);
		bottomRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		bottomRightParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		bottomRightParams.setMargins(0, 8 * DP, 8 * DP, 0);
		bottomRightButton = new UIButton(context);
		bottomRightButton.setLayoutParams(bottomRightParams);
		bottomRightButton.setTextSize(12);
		bottomRightButton.setVisibility(View.GONE);
		bottomRightButton.setText("完成");
		bottomRightButton.setTextColor(Color.parseColor("#FFFFFF"));
		bottomRightButton.setBackgroundColor(Color.parseColor("#00b58a"));
		bottomRightButton.setOnClickListener(bottomRightClickListener);
		
		
		bottomContainer.addView(bottomLeftButton);
		bottomContainer.addView(bottomRightButton);
		
		/**
		 * 相册视图
		 */
		LinearLayout.LayoutParams bucketParams = getParams1(MATCH_PARENT1, 278 * DP);
		bucketParams.setMargins(0, (int)(0.8 * DP), 0, 0);
		bucketListView = new ListView(context);
		bucketListView.setLayoutParams(bucketParams);
		bucketListView.setBackgroundColor(Color.parseColor("#F8F8F8"));
		bucketListView.setVisibility(View.GONE);
		bucketListView.setOnItemClickListener(bucketOnItemClickListener);
		
		rootContainer.addView(topContainer);
		rootContainer.addView(photoGridView);
		rootContainer.addView(bottomContainer);
		rootContainer.addView(bucketListView);
		
		return rootContainer;
	}
	
	public UIPhotoSelector show(){
		showAtLocation(((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0), Gravity.BOTTOM, 0, 0);
		return this;
	}
	
	class BucketAdapter extends UEAdapter{

		public BucketAdapter(List<?> datas) {
			super(datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				LinearLayout mContainerLayout = new LinearLayout(context);
				mContainerLayout.setLayoutParams((new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,AbsListView.LayoutParams.WRAP_CONTENT)));
				mContainerLayout.setPadding(16 * DP,10 * DP, 16 * DP, 10 * DP);
				mContainerLayout.setGravity(Gravity.CENTER_VERTICAL);
				mContainerLayout.setOrientation(LinearLayout.HORIZONTAL);
				holder.icon = new ImageView(context);
				holder.icon.setLayoutParams(getParams1(72 * DP, 72 * DP));
				holder.icon.setScaleType(ScaleType.CENTER_CROP);
				LinearLayout.LayoutParams params = getParams1(MATCH_PARENT1, MATCH_PARENT1);
				params.setMargins(16 * DP, 0, 0, 0);
				holder.name = new TextView(context);
				holder.name.setLayoutParams(params);
				holder.name.setEllipsize(TruncateAt.END);
				holder.name.setSingleLine(true);
				holder.name.setTextSize(16);
				holder.name.setTextColor(Color.parseColor("#888888"));
				holder.name.setGravity(Gravity.CENTER_VERTICAL);
				mContainerLayout.addView(holder.icon);
				mContainerLayout.addView(holder.name);
				convertView = mContainerLayout;
				convertView.setTag(holder);
			}else {
				holder = (Holder) convertView.getTag();
			}
			Bucket bucket = (Bucket) getItem(position);
			holder.name.setText(bucket.name + "(" + bucket.count +")");
			Image image = bucket.images.get(0);
			final String thumbPath = image.thumbnail;
			final String path = image.path;
			if (thumbPath == null && path == null) {
				return convertView;
			}
			final ImageView ImageView = holder.icon;
			mImageLoader.loadBitmapByFile(thumbPath == null ? path : thumbPath, new OnUEImageLoadListener() {
				
				@Override
				public void onImageLoadCompleted(Bitmap bitmap, String imageUrl) {
					ImageView.setImageBitmap(bitmap);
				}
			});
			return convertView;
		}
		
		class Holder {
			private ImageView icon;
			private TextView name;
		}
	}
	
	class ImageAdapter extends UEAdapter{

		public ImageAdapter(List<?> datas) {
			super(datas);
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//判断GirdView状态，如果处于Measure直接返回convertView，不进行数据处理
			if (photoGridView.isMeasure() && convertView != null) {
				return convertView;
			}
			ImageView imageView;
			RelativeLayout mContainer = new RelativeLayout(context);
			mContainer.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT,AbsListView.LayoutParams.WRAP_CONTENT));
			imageView = new ImageView(context);
			imageView.setLayoutParams(getParams2(IMAGE_SIZE, IMAGE_SIZE));
			imageView.setScaleType(ScaleType.CENTER_CROP);
			RelativeLayout.LayoutParams params = getParams2(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(4 * DP, 4 * DP, 4 * DP, 4 * DP);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			ImageView selectImageView = new ImageView(context);
			selectImageView.setLayoutParams(params);
			selectImageView.setVisibility(View.INVISIBLE);
			selectImageView.setImageResource(android.R.drawable.presence_online);
			mContainer.addView(imageView);
			mContainer.addView(selectImageView);
			convertView = mContainer;
			convertView.setTag(imageView);
			if (photoGridView.isMeasure()) {
				return convertView;
			}
			String path = ((Image)getItem(position)).thumbnail;
			if (path == null) {
				path = ((Image)getItem(position)).path;
			}
			if (path == null) {
				return convertView;
			}
			final ImageView iv = imageView;
			mImageLoader.loadBitmapByFile(path, new OnUEImageLoadListener() {
				@Override
				public void onImageLoadCompleted(Bitmap bitmap, String imageUrl) {
					iv.setImageBitmap(bitmap);
				}
			});
			return convertView;
		}
		
	}
	
	public void setTopLeftButtonImageResources(int resId){
		topLeftButton.setImageResource(resId);
	}
	
	public interface OnUIPhotoSelectorListener{
		public void onSelectCompleted(List<Image> images);
		public void onSelectCancel();
	}
}
