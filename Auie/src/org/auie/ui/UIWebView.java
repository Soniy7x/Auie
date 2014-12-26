package org.auie.ui;

import java.util.HashMap;
import java.util.Map;

import org.auie.utils.UEMethod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class UIWebView extends LinearLayout {

	private static final int LIGHT = Color.parseColor("#C8C8C8");
	private static final int NORMAL = Color.parseColor("#888888");
	private static final int DARK = Color.parseColor("#222222");
	
	private WebView mWebView;
	private LinearLayout mNavigationBar;
	private LinearLayout mToolBar;
	private View mToolBarLine;
	private View mNavigationBarLine;
	private TextView mTitle;
	private ShapeView mNavigationBarRefresh;
	private ShapeView mNavigationBarHome;
	private ShapeView mToolBarBack;
	private ShapeView mToolBarGo;
	private ShapeView mToolBarCustomer;
	
	private OnClickListener onCustomerButtonClickListener;
	
	private Map<String, String> titles = new HashMap<String, String>();
	
	private int DP = 0;
	
	public UIWebView(Context context) {
		super(context);
		init();
	}

	public UIWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UIWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public UIWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init(){
		DP = UEMethod.dp2px(getContext(), 1);
		
		setOrientation(VERTICAL);
		
		mNavigationBar = new LinearLayout(getContext());
		mNavigationBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 48 * DP));
		mNavigationBar.setOrientation(HORIZONTAL);
		mNavigationBar.setGravity(Gravity.CENTER_VERTICAL);
		mNavigationBar.setPadding(10 * DP, 0, 10 * DP, 0);
		mNavigationBar.setBackgroundColor(Color.parseColor("#F3F3F3"));
		
		mTitle = new TextView(getContext());
		mTitle.setTextColor(Color.parseColor("#CC222222"));
		mTitle.setTextSize(18);
		mTitle.setGravity(Gravity.CENTER);
		mTitle.setPadding(10 * DP, 0, 10 * DP, 0);
		mTitle.setLines(1);
		mTitle.setEllipsize(TruncateAt.END);
		mTitle.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));

		mNavigationBarHome = new ShapeView(getContext(), ShapeView.TYPE_LINE);
		mNavigationBarHome.setLayoutParams(new LayoutParams(28 * DP, 26 * DP));
		mNavigationBarHome.setTag("1");
		mNavigationBarHome.setColorful(true);
		mNavigationBarHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.loadUrl(mNavigationBarHome.getTag().toString());
			}
		});
		
		mNavigationBarRefresh = new ShapeView(getContext(), ShapeView.TYPE_CIRCLE);
		mNavigationBarRefresh.setLayoutParams(new LayoutParams(36 * DP, 36 * DP));
		mNavigationBarRefresh.setColorful(true);
		mNavigationBarRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.reload();
			}
		});
		
		mNavigationBar.addView(mNavigationBarHome);
		mNavigationBar.addView(mTitle);
		mNavigationBar.addView(mNavigationBarRefresh);
		
		mNavigationBarLine = new View(getContext());
		mNavigationBarLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)(0.8 * DP)));
		mNavigationBarLine.setBackgroundColor(Color.parseColor("#D8D8D8"));
		
		mWebView = new WebView(getContext());
		mWebView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mTitle.setText(titles.get(url));
				if (mWebView.canGoBack()) {
					mToolBarBack.setColorful(true);
				}else {
					mToolBarBack.setColorful(false);
				}
				if (mWebView.canGoForward()) {
					mToolBarGo.setColorful(true);
				}else {
					mToolBarGo.setColorful(false);
				}
			}
			
		});
		mWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				LayoutParams params = (LinearLayout.LayoutParams) mNavigationBarLine.getLayoutParams();
				params.width = (int) (newProgress / 100f * getWidth());
				mNavigationBarLine.setLayoutParams(params);
				if (newProgress >= 100) {
					mNavigationBarLine.setBackgroundColor(Color.parseColor("#D8D8D8"));
				}else {
					mNavigationBarLine.setBackgroundColor(Color.GREEN);
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				mTitle.setText(title);
				titles.put(mWebView.getUrl(), title);
			}
			
		});
		
		mToolBarLine = new View(getContext());
		mToolBarLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)(0.8 * DP)));
		mToolBarLine.setBackgroundColor(Color.parseColor("#D8D8D8"));
		
		mToolBar = new LinearLayout(getContext());
		mToolBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 48 * DP));
		mToolBar.setOrientation(HORIZONTAL);
		mToolBar.setGravity(Gravity.CENTER_VERTICAL);
		mToolBar.setBackgroundColor(Color.parseColor("#F3F3F3"));
		
		mToolBarBack = new ShapeView(getContext(), ShapeView.TYPE_TRIAGNLE_LEFT);
		mToolBarBack.setLayoutParams(new LayoutParams(16 * DP, 23 * DP));
		mToolBarBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.goBack();
			}
		});
		
		mToolBarGo = new ShapeView(getContext(), ShapeView.TYPE_TRIAGNLE_RIGHT);
		mToolBarGo.setLayoutParams(new LayoutParams(16 * DP, 23 * DP));
		mToolBarGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.goForward();
			}
		});

		mToolBarCustomer = new ShapeView(getContext(), ShapeView.TYPE_SQUARE);
		mToolBarCustomer.setLayoutParams(new LayoutParams(24 * DP, 24 * DP));
		mToolBarCustomer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onCustomerButtonClickListener != null) {
					onCustomerButtonClickListener.onClick(v);
				}
			}
		});
		
		mToolBar.addView(createEmptyView(1));
		mToolBar.addView(mToolBarBack);
		mToolBar.addView(createEmptyView(1.5f));
		mToolBar.addView(mToolBarCustomer);
		mToolBar.addView(createEmptyView(1.5f));
		mToolBar.addView(mToolBarGo);
		mToolBar.addView(createEmptyView(1));
		
		addView(mNavigationBar);
		addView(mNavigationBarLine);
		addView(mWebView);
		addView(mToolBarLine);
		addView(mToolBar);
	}
	
	private View createEmptyView(float weight){
		View view = new View(getContext());
		view.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, weight));
		return view;
	}
	
	public void loadUrl(String url){
		if (mNavigationBarHome.getTag().toString().equals("1")) {
			mNavigationBarHome.setTag(url);
		}
		mWebView.loadUrl(url);
	}

	public void setOnCustomerButtonClickListener(OnClickListener onCustomerButtonClickListener) {
		this.onCustomerButtonClickListener = onCustomerButtonClickListener;
	}

	@SuppressLint("DrawAllocation")
	class ShapeView extends View{

		public static final int TYPE_LINE = 0;
		public static final int TYPE_CIRCLE = 1;
		public static final int TYPE_SQUARE = 2;
		public static final int TYPE_TRIAGNLE_LEFT = 3;
		public static final int TYPE_TRIAGNLE_RIGHT = 4;
		
		private Paint mPaint = new Paint();
		
		private int color = LIGHT;
		private boolean colorful = false;
		private int type = TYPE_CIRCLE;
		
		public ShapeView(Context context) {
			super(context);	
		}
		
		public ShapeView(Context context, int type) {
			super(context);	
			this.type = type;
		}
		
		public void setColorful(boolean colorful){
			this.colorful = colorful;
			if (colorful) {
				color = NORMAL;
			}else {
				color = LIGHT;
			}
			invalidate();
		}
		
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				color = DARK;
				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				if (colorful) {
					color = NORMAL;
				}else {
					color = LIGHT;
				}
				invalidate();
				return false;
			default:
				color = DARK;
				invalidate();
				return false;
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			mPaint.setAntiAlias(true);
			mPaint.setColor(color);
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(DP);
			

			Path path = new Path();
			switch (type) {
			case TYPE_LINE:
				float height = getHeight()/3f + DP * 3f;
				path.moveTo(0, height);
				path.lineTo(getWidth()/2f, 1);
				path.lineTo(getWidth(), height);
				path.moveTo(getWidth() - DP * 4f, height - DP);
				path.lineTo(getWidth() - DP * 4f, getHeight()- DP);
				path.lineTo(DP * 4f, getHeight()- DP);
				path.lineTo(DP * 4f, height - DP);
				canvas.drawPath(path, mPaint);
				break;
			case TYPE_SQUARE:
				canvas.drawRoundRect(new RectF(DP, DP, getWidth() - DP, getHeight() - DP), DP * 1f, DP * 1f, mPaint);
				break;
			case TYPE_TRIAGNLE_LEFT:
				path.moveTo(1, getHeight()/2f);
				path.lineTo(getWidth() - 1, 1);
				path.moveTo(getWidth() - 1, getHeight() - 1);
				path.lineTo(1, getHeight()/2f);
				canvas.drawPath(path, mPaint);
				break;
			case TYPE_TRIAGNLE_RIGHT:
				path.moveTo(getWidth() - 1, getHeight()/2f);
				path.lineTo(1, 1);
				path.moveTo(1, getHeight() - 1);
				path.lineTo(getWidth() - 1, getHeight()/2f);
				canvas.drawPath(path, mPaint);
				break;
			default:
				path.moveTo(getWidth() - DP * 11, getHeight() / 2f - 5.2f * DP);
				path.lineTo(getWidth() - DP * 6, getHeight() / 2f - 2 * DP);
				path.lineTo(getWidth() - DP * 5f, getHeight() / 2f - 8 * DP);
				canvas.drawPath(path, mPaint);
				canvas.drawArc(new RectF(DP * 6, DP * 6, getWidth() - DP * 6, getHeight() - DP * 6), 10, 340, false, mPaint);
				break;
			}
			
		}
	}
}
