package org.auie.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.auie.image.UEImage;
import org.auie.image.UEImageManager.Image;
import org.auie.utils.UEImageNotByteException;
import org.auie.utils.UEMethod;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;;

@SuppressWarnings("deprecation")
public class UIImagePager extends PopupWindow {
	
	public static final int MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
	public static final int WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
	
	public Drawable NO_SELECTED_DRAWABLE;
	public Drawable SELECTED_DRAWABLE;
	
	private Context context;
	private RelativeLayout rootContainer;
	private HorizontalScrollView contentContainer;
	private LinearLayout mContainer;
	private LinearLayout indexContainer;
	
	private List<View> indexViews = new ArrayList<View>();;
	
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	
	private int WIDTH = 0;
	private int HEIGHT = 0;
	private int DP = 1;
	
	private float pressX = 0;
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
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setImageBitmap(bitmaps.get(i));
			mContainer.addView(imageView);
		}
	}

	private void init(Context context){
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams attrs = ((Activity) context).getWindow().getAttributes();
		this.context = context;
		this.DP = UEMethod.dp2px(context, 1);
		this.NO_SELECTED_DRAWABLE = UEImage.createBackground(Color.parseColor("#FFFFFF"), 50, 6 * DP);
		this.SELECTED_DRAWABLE = UEImage.createBackground(Color.parseColor("#EEEEEE"), 255, 6 * DP);
		if((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN){
			this.HEIGHT = manager.getDefaultDisplay().getHeight();
		}else {
			this.HEIGHT = manager.getDefaultDisplay().getHeight() - 24 * DP;
		}
		this.WIDTH = manager.getDefaultDisplay().getWidth();
		createView();
	}

	private void createView() {
		setBackgroundDrawable(new BitmapDrawable());
		setContentView(createContentView());
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setFocusable(true);
	}

	private View createContentView() {
		
		rootContainer = new RelativeLayout(context);
		rootContainer.setBackgroundColor(Color.parseColor("#000000"));
		rootContainer.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
		
		LayoutParams contentParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		contentParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		contentContainer = new HorizontalScrollView(context);
		contentContainer.setLayoutParams(contentParams);
		contentContainer.setVerticalScrollBarEnabled(false);
		contentContainer.setHorizontalScrollBarEnabled(false);
		contentContainer.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					pressX = event.getX();
					break;

				case MotionEvent.ACTION_UP:
					System.out.println(event.getX() + "," + pressX);
					if (pressX - event.getX() > 100) {
						if (currentIndex < bitmaps.size() - 1) {
							indexViews.get(currentIndex).setBackgroundDrawable(NO_SELECTED_DRAWABLE);
							currentIndex++;
							contentContainer.scrollTo((int)(currentIndex * WIDTH), (int)contentContainer.getScrollY());
						}
					}else {
						if (currentIndex > 0) {
							indexViews.get(currentIndex).setBackgroundDrawable(NO_SELECTED_DRAWABLE);
							currentIndex--;
							contentContainer.scrollTo((int)(currentIndex * WIDTH), (int)contentContainer.getScrollY());
						}
					}
					indexViews.get(currentIndex).setBackgroundDrawable(SELECTED_DRAWABLE);
					break;
				default:
					break;
				}
				return true;
			}
		});
		
		mContainer = new LinearLayout(context);
		mContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
		mContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutParams params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		indexContainer = new LinearLayout(context);
		indexContainer.setLayoutParams(params);
		indexContainer.setPadding(0, 0, 0, DP * 20);
		indexContainer.setGravity(Gravity.CENTER);
		indexContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		contentContainer.addView(mContainer);
		rootContainer.addView(contentContainer);
		rootContainer.addView(indexContainer);
		
		return rootContainer;
	}
	
	public UIImagePager show(){
		showAtLocation(((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0), Gravity.BOTTOM, 0, 0);
		return this;
	}
}
