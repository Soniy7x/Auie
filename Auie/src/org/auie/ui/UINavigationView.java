package org.auie.ui;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.auie.utils.UEDevice;
import org.auie.utils.UEMethod;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.L)
public class UINavigationView extends LinearLayout {
	
	public static final int STATUSBAR_LIGHT = Color.parseColor("#CCFFFFFF");
	public static final int STATUSBAR_DARK = Color.parseColor("#CC222222");
	
	private RelativeLayout mStatus;
	private RelativeLayout mActionBar;
	
	private TextView mTimeTextView;
	private TextView mTitleTextView;
	private TextView mLeftTextView;
	private TextView mRightTextView;
	private TextView mBatteryTextView;
	private TextView mSingalTextView;
	private TextView mNetworkTextView;
	private ImageView mLeftImageView;
	private ImageView mRightImageView;
	private UIBatteryView mBatteryView;
	private UISingalView mSingalView;
	private UIWIFIView mWIFIView;
	private View mLine;
	
	private int statusBarBackgroundColor = Color.parseColor("#F3F3F3");
	private int navigationBarBackgroundColor = Color.parseColor("#F3F3F3");
	private int lineBackgroundColor = Color.parseColor("#44444444");
	private int navigationTextColor = Color.parseColor("#007aff");
	private int titleColor = Color.parseColor("#CC222222");
	private int statusType = STATUSBAR_DARK;
	
	private int DP = UEMethod.dp2px(getContext(), 1);
	
	public UINavigationView(Context context) {
		super(context);
		init();
	}

	public UINavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UINavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public UINavigationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init(){
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setOrientation(VERTICAL);
		
		mStatus = new RelativeLayout(getContext());
		mStatus.setBackgroundColor(statusBarBackgroundColor);
		mStatus.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 24 * DP));
		
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		mTimeTextView = new TextView(getContext());
		mTimeTextView.setLayoutParams(params1);
		mTimeTextView.setTextSize(14);
		mTimeTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		RelativeLayout.LayoutParams params6 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params6.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params6.addRule(RelativeLayout.LEFT_OF, 1993);
		params6.setMargins(0, 0, 2 * DP, 0);
		mBatteryTextView = new TextView(getContext());
		mBatteryTextView.setLayoutParams(params6);
		mBatteryTextView.setTextSize(12);
		mBatteryTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(24 * DP, 12 * DP);
		params5.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		params5.setMargins(0, 0, 4 * DP, 0);
		mBatteryView = new UIBatteryView(getContext());
		mBatteryView.setLayoutParams(params5);
		mBatteryView.setId(1993);
		
		RelativeLayout.LayoutParams params7 = new RelativeLayout.LayoutParams(44 * DP, 8 * DP);
		params7.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params7.setMargins(4 * DP, 0, 0, 0);
		mSingalView = new UISingalView(getContext());
		mSingalView.setLayoutParams(params7);
		mSingalView.setId(1994);
		
		RelativeLayout.LayoutParams params8 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params8.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params8.addRule(RelativeLayout.RIGHT_OF, 1994);
		params8.setMargins(4 * DP, 0, 0, 0);
		mSingalTextView = new TextView(getContext());
		mSingalTextView.setLayoutParams(params8);
		mSingalTextView.setTextSize(12);
		mSingalTextView.setId(1995);
		mSingalTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		RelativeLayout.LayoutParams params9 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params9.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params9.addRule(RelativeLayout.RIGHT_OF, 1995);
		params9.setMargins(2 * DP, 0, 0, 0);
		mNetworkTextView = new TextView(getContext());
		mNetworkTextView.setLayoutParams(params9);
		mNetworkTextView.setTextSize(12);
		mNetworkTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		
		RelativeLayout.LayoutParams params10 = new RelativeLayout.LayoutParams(22 * DP, 22 * DP);
		params10.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params10.addRule(RelativeLayout.RIGHT_OF, 1995);
		mWIFIView = new UIWIFIView(getContext());
		mWIFIView.setLayoutParams(params10);
		
		mStatus.addView(mSingalView);
		mStatus.addView(mSingalTextView);
		mStatus.addView(mNetworkTextView);
		mStatus.addView(mWIFIView);
		mStatus.addView(mTimeTextView);
		mStatus.addView(mBatteryView);
		mStatus.addView(mBatteryTextView);
		
		mActionBar = new RelativeLayout(getContext());
		mActionBar.setBackgroundColor(navigationBarBackgroundColor);
		mActionBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
				UEDevice.getDeviceScreen(getContext()) < UEDevice.SCREEN_720P ? 38 * DP : 48 * DP));
		
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 24 * DP);
		params2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params2.setMargins(7 * DP, 0, 0, 0);
		mLeftImageView = new ImageView(getContext());
		mLeftImageView.setLayoutParams(params2);
		mLeftImageView.setId(1992);
		mLeftImageView.setScaleType(ScaleType.FIT_CENTER);
		
		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params3.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params3.addRule(RelativeLayout.RIGHT_OF, 1992);
		mLeftTextView = new TextView(getContext());
		mLeftTextView.setLayoutParams(params3);
		mLeftTextView.setTextSize(18);
		mLeftTextView.setTextColor(navigationTextColor);
		
		RelativeLayout.LayoutParams params12 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 24 * DP);
		params12.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params12.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		params12.setMargins(0, 0, 7 * DP, 0);
		mRightImageView = new ImageView(getContext());
		mRightImageView.setLayoutParams(params12);
		mRightImageView.setId(1996);
		mRightImageView.setScaleType(ScaleType.FIT_CENTER);
		
		RelativeLayout.LayoutParams params11 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params11.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		params11.addRule(RelativeLayout.LEFT_OF, 1996);
		mRightTextView = new TextView(getContext());
		mRightTextView.setLayoutParams(params11);
		mRightTextView.setTextSize(18);
		mRightTextView.setTextColor(navigationTextColor);
		
		RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params4.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		mTitleTextView = new TextView(getContext());
		mTitleTextView.setLayoutParams(params4);
		mTitleTextView.setTextSize(20);
		mTitleTextView.setTextColor(titleColor);
		
		mActionBar.addView(mLeftImageView);
		mActionBar.addView(mRightImageView);
		mActionBar.addView(mLeftTextView);
		mActionBar.addView(mTitleTextView);
		mActionBar.addView(mRightTextView);

		mLine = new View(getContext());
		mLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)(DP * 0.8)));
		mLine.setBackgroundColor(lineBackgroundColor);
		
		addView(mStatus);
		addView(mActionBar);
		addView(mLine);
		
		setStatusType(statusType);
		
		handler.post(runnable);
	}
	
	public void setLeftImageOnClickListener(OnClickListener mListener){
		mLeftImageView.setOnClickListener(mListener);
	}
	
	public void setLeftTextOnClickListener(OnClickListener mListener){
		mLeftTextView.setOnClickListener(mListener);
	}
	
	public void setLeftOnClickListener(OnClickListener mListener){
		mLeftImageView.setOnClickListener(mListener);
		mLeftTextView.setOnClickListener(mListener);
	}
	
	public void setLeftImageResource(int resId) {
		mLeftImageView.setImageResource(resId);
	}

	public void setLeftText(String text){
		this.mLeftTextView.setText(text);
	}
	
	public void setRightOnClickListener(OnClickListener mListener){
		mRightImageView.setOnClickListener(mListener);
		mRightTextView.setOnClickListener(mListener);
	}
	
	public void setRightImageResource(int resId) {
		mRightImageView.setImageResource(resId);
	}
	
	public void setRightText(String text){
		this.mRightTextView.setText(text);
	}
	
	public void setTitle(String title){
		this.mTitleTextView.setText(title);
	}
	
	public void setLevel(float level){
		this.mBatteryView.setLevel(level);
	}
	
	public void setStatus(int status){
		this.mBatteryView.setStatus(status);
	}
	
	public void setSignal(int level){
		this.mSingalView.setLevel(level);
	}

	public void setBatteryText(String text){
		this.mBatteryTextView.setText(text);
	}
	
	public void setSingalText(String text){
		this.mSingalTextView.setText(text);
	}
	
	public void setSingalStatus(int status){
		this.mSingalView.setStatus(status);
	}
	
	public void setWIFI(int level){
		this.mWIFIView.setLevel(level);
	}
	
	public void setNetworkText(String text){
		this.mNetworkTextView.setText(text);
	}
	
	public void showWIFI(){
		this.mNetworkTextView.setVisibility(View.GONE);
		this.mWIFIView.setStatus(UIWIFIView.STATUS_NORMAL);
	}
	
	public void hideWIFI(){
		this.mNetworkTextView.setVisibility(View.VISIBLE);
		this.mWIFIView.setStatus(UIWIFIView.STATUS_NONE);
	}

	public void setStatusBarBackgroundColor(int statusBarBackgroundColor) {
		this.statusBarBackgroundColor = statusBarBackgroundColor;
		this.mStatus.setBackgroundColor(statusBarBackgroundColor);
	}

	public void setNavigationBarBackgroundColor(int navigationBarBackgroundColor) {
		this.navigationBarBackgroundColor = navigationBarBackgroundColor;
		this.mActionBar.setBackgroundColor(navigationBarBackgroundColor);
	}
	
	public void setBackgroundColor(int backgroundColor) {
		setStatusBarBackgroundColor(backgroundColor);
		setNavigationBarBackgroundColor(backgroundColor);
	}
	
	public void setLineBackgroundColor(int lineBackgroundColor) {
		this.lineBackgroundColor = lineBackgroundColor;
		this.mLine.setBackgroundColor(lineBackgroundColor);
	}

	public void setStatusType(int statusType) {
		this.statusType = statusType;
		this.mSingalTextView.setTextColor(statusType);
		this.mNetworkTextView.setTextColor(statusType);
		this.mTimeTextView.setTextColor(statusType);
		this.mBatteryTextView.setTextColor(statusType);
		this.mSingalView.setPaintColor(statusType);
		this.mWIFIView.setPaintColor(statusType);
		this.mBatteryView.setPaintColor(statusType);
	}

	public void setNavigationTextColor(int navigationTextColor) {
		this.navigationTextColor = navigationTextColor;
		this.mLeftTextView.setTextColor(navigationTextColor);
	}

	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
		this.mTitleTextView.setTextColor(titleColor);
	}

	public void hideStatusBar(){
		mStatus.setVisibility(View.GONE);
	}
	
	public void hideActionBar(){
		mActionBar.setVisibility(View.GONE);
		mLine.setVisibility(View.GONE);
	}
	
	public void showStatusBar(){
		mStatus.setVisibility(View.VISIBLE);
	}
	
	public void showActionBar(){
		mActionBar.setVisibility(View.VISIBLE);
		mLine.setVisibility(View.VISIBLE);
	}
	
	private Handler handler = new Handler();
	private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
	private Runnable runnable = new Runnable() {
		public void run() {
			mTimeTextView.setText(mFormat.format(System.currentTimeMillis()));
			handler.postDelayed(runnable, 1000 * 60);
		}
	};
}
