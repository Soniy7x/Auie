package org.auie.base;

import java.util.Locale;

import org.auie.annotation.UEAnnotation.UEConfig;
import org.auie.annotation.UEAnnotation.UELayout;
import org.auie.annotation.UEAnnotationManager;
import org.auie.ui.UIBatteryView;
import org.auie.ui.UINavigationView;
import org.auie.ui.UISingalView;
import org.auie.utils.UE;
import org.auie.utils.UEDevice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public abstract class UENavigationActivity extends Activity implements OnClickListener{

	private IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	private IntentFilter wifiFilter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
	
	//全局Activity对象，可当作context使用
	//(Global Activity Object, can be used as Context)
	public final Activity activity = this;
	
	private SingalListener mSingalListener = new SingalListener();
	private UINavigationView mNavigationView;
	private TelephonyManager mTelephonyManager;
	
	/**
	 * 复写onCreat方法，调用初始化入口方法，严禁复写
	 * (calling initialization entrance method, it's strictly prohibited to override)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializePrepare();	//初始化准备
		initializeBegin(); 		//初始化开始
		initializeFinish();		//初始化结束
	}
	
	/**
	 * 初始化开始方法
	 * (Initialization begins)
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initializeBegin() {
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mNavigationView = new UINavigationView(this);
		mNavigationView.setNetworkText(UEDevice.getNetworkType(activity));
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mSingalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		setContentView(mNavigationView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		if (getClass().isAnnotationPresent(UELayout.class)) {
			int layout = -1;
			UELayout initialization = getClass().getAnnotation(UELayout.class);
			if (initialization.value() != -1) {
				layout = initialization.value();
			} else {
				try{
					layout = getResources().getIdentifier(getClass().getSimpleName().toLowerCase(Locale.getDefault()), "layout", getPackageName());
					if (layout == 0) {
						Log.e("AUIE", "资源错误 - 未找到名为" + getClass().getSimpleName().toLowerCase(Locale.getDefault()) + ".xml的布局文件");
					}
				} catch (Exception e){
					Log.d("AUIE", e.getMessage());
				}
			}
			View view = LayoutInflater.from(activity).inflate(layout, mNavigationView, false);
			mNavigationView.addView(view);
		}
		if (getClass().isAnnotationPresent(UEConfig.class)) {
			int config = getClass().getAnnotation(UEConfig.class).value();
			if (config != -1) {
				readXML(config);
			}
		}
		UEAnnotationManager.getInstance().initialize(this, false);
	}
	
	/**
	 * 读取XML配置文件
	 * @param id 配置文件索引
	 */
	private void readXML(int id){
		String name;
		String className = getClass().getSimpleName();
		XmlResourceParser xrp = getResources().getXml(id);
		try {
			while(xrp.getEventType() != XmlResourceParser.END_DOCUMENT){
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					name = xrp.getName();
					if (name.equals("BackgroundColor")) {
						mNavigationView.setBackgroundColor(xrp.getAttributeIntValue(0, mNavigationView.getBackgroundColor()));
					}
					if (name.equals("StatusType")) {
						mNavigationView.setStatusType(xrp.getAttributeIntValue(0, mNavigationView.getStatusType()));
					}
					if (name.equals("TitleColor")) {
						mNavigationView.setTitleColor(xrp.getAttributeIntValue(0, mNavigationView.getTitleColor()));
					}
					if (name.equals("LineBackgroundColor")) {
						mNavigationView.setLineBackgroundColor(xrp.getAttributeIntValue(0, mNavigationView.getTitleColor()));
					}
					if (name.equals("NavigationTextColor")) {
						mNavigationView.setNavigationTextColor(xrp.getAttributeIntValue(0, mNavigationView.getNavigationTextColor()));
					}
					if (name.equals("Title") && xrp.getAttributeValue(0).equals(className)) {
						mNavigationView.setTitle(xrp.getAttributeValue(1));
					}
				}
				xrp.next();
			}
		} catch (Exception e) {
			Log.d(UE.TAG, "UEConfig配置出错"+e.toString());
		}
	}
	
	/**
	 * 初始化准备方法
	 * (Initialization prepares, here you can perform some method)
	 */
	protected void initializePrepare() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
	}
	
	/**
	 * 实现OnClickListener接口
	 * (Perform onClickListener)
	 */
	@Override
	public void onClick(View v) {}

	public void setLeftImageResource(int resId){
		mNavigationView.setLeftImageResource(resId);
	}
	
	public void setLeftText(String text){
		mNavigationView.setLeftText(text);
	}
	
	@Override
	public void setTitle(int titleId) {
		mNavigationView.setTitle(getResources().getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {
		mNavigationView.setTitle(title.toString());
	}

	public void setTitle(String title){
		mNavigationView.setTitle(title);
	}
	
	public void setLeftOnClickListener(OnClickListener mListener){
		mNavigationView.setLeftOnClickListener(mListener);
	}
	
	public void setLeftDefaultOnClickListener(Activity activity){
		mNavigationView.setLeftDefaultOnClickListener(activity);
	}
	
	public void setRightOnClickListener(OnClickListener mListener){
		mNavigationView.setRightOnClickListener(mListener);
	}
	
	public void setRightImageResource(int resId) {
		mNavigationView.setRightImageResource(resId);
	}
	
	public void setRightText(String text){
		mNavigationView.setRightText(text);
	}
	
	public void setStatusBarBackgroundColor(int statusBarBackgroundColor) {
		mNavigationView.setStatusBarBackgroundColor(statusBarBackgroundColor);
	}

	public void setNavigationBarBackgroundColor(int navigationBarBackgroundColor) {
		mNavigationView.setNavigationBarBackgroundColor(navigationBarBackgroundColor);
	}
	
	public void setBackgroundColor(int backgroundColor) {
		mNavigationView.setBackgroundColor(backgroundColor);
	}
	
	public void setLineBackgroundColor(int lineBackgroundColor) {
		mNavigationView.setLineBackgroundColor(lineBackgroundColor);
	}
	
	public void setStatusType(int statusType) {
		mNavigationView.setStatusType(statusType);
	}

	public void setNavigationTextColor(int navigationTextColor) {
		mNavigationView.setNavigationTextColor(navigationTextColor);
	}

	public void setTitleColor(int titleColor) {
		mNavigationView.setTitleColor(titleColor);
	}
	
	public void showNavigationBar(){
		showStatusBar();
		showActionBar();
	}
	
	public void hideNavigationBar(){
		hideStatusBar();
		hideActionBar();
	}
	
	public void hideStatusBar(){
		mNavigationView.hideStatusBar();
	}
	
	public void hideActionBar(){
		mNavigationView.hideActionBar();
	}
	
	public void showStatusBar(){
		mNavigationView.showStatusBar();
	}
	
	public void showActionBar(){
		mNavigationView.showActionBar();
	}
	
	public void addNotice(String content){
		mNavigationView.addNotice(content);
	}
	
	public void addNotice(String content, long time){
		mNavigationView.addNotice(content, time);
	}

	public void clearNotice(){
		mNavigationView.clearNotice();
	}
	
	protected abstract void initializeFinish();
	
	private BroadcastReceiver wifiReceiver  = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			WifiInfo info = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
			if (info.getBSSID() == null) {
				mNavigationView.hideWIFI();
			}else {
				mNavigationView.showWIFI();
				mNavigationView.setWIFI(WifiManager.calculateSignalLevel(info.getRssi(), 5));
			}
		}
		
	};
	
	private BroadcastReceiver batteryReceiver  = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra("level", -1);
            int status = intent.getIntExtra("status", -1);
            switch (status) {
			case BatteryManager.BATTERY_STATUS_FULL:
				mNavigationView.setStatus(UIBatteryView.STATUS_COMLETED);
				mNavigationView.setBatteryText("已充满");
				break;
			case BatteryManager.BATTERY_STATUS_CHARGING:
				mNavigationView.setStatus(UIBatteryView.STATUS_CHARGED);
				mNavigationView.setBatteryText("充电中");
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				mNavigationView.setLevel(level * 0.01f);
				mNavigationView.setBatteryText(level + "%");
				break;
			default:
				mNavigationView.setLevel(0);
				mNavigationView.setBatteryText("无电池");
				break;
			}
		}
	};

	class SingalListener extends PhoneStateListener{

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);
			String sim = UEDevice.getSimType(activity);
			mNavigationView.setSingalText(sim);
			if (sim.equals("无服务")) {
				mNavigationView.setSingalStatus(UISingalView.STATUS_NONE);
				return;
			}
			mNavigationView.setSingalStatus(UISingalView.STATUS_NORMAL);
			int level = 0;
			if (signalStrength.isGsm()) {
				level = signalStrength.getGsmSignalStrength();
			}else {
				if (sim.equals("中国电信")) {
					level = signalStrength.getCdmaDbm();
				}else {
					level = signalStrength.getEvdoDbm();
				}
			}
			if (level == 0) {
				mNavigationView.setSignal(5);
				return;
			}
			if (level < -112) {
				mNavigationView.setSignal(0);
			}else if (level >= -111 && level < -104) {
				mNavigationView.setSignal(1);
			}else if (level >= -104 && level < -97) {
				mNavigationView.setSignal(2);
			}else if (level >= -97 && level < -90) {
				mNavigationView.setSignal(3);
			}else if (level >= -90 && level < -83) {
				mNavigationView.setSignal(4);
			}else {
				mNavigationView.setSignal(5);
			}
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(batteryReceiver, batteryFilter);
		registerReceiver(wifiReceiver, wifiFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(batteryReceiver);
		unregisterReceiver(wifiReceiver);
	}
	

	/**
	 * 无传递参数简单跳转Activity方法
	 * (Form this Activity to other Activity, it's not incidental parameters)
	 * @param clazz 将要跳转的目的地(Other Activity - Destination)
	 * @param isClose 完成后是否关闭此类(Whether to close the current Activity)
	 */
	public void startActivity(Class<?> clazz, boolean isClose){
		startActivity(new Intent(this, clazz));
		if (isClose) {
			finish();
		}
	}
	
	/**
	 * 自定义字体方法
	 * @param typeface 自定义字体
	 */
	protected void initializeFont(Typeface typeface) {
		UEAnnotationManager.getInstance().initializeFont(activity, typeface);
	}

	/**
	 * 简单跳转Activity方法
	 * @param intent 将要跳转的目的地
	 * @param isClose 完成后是否关闭此类
	 */
	public void startActivity(Intent intent, boolean isClose){
		startActivity(intent);
		if (isClose) {
			finish();
		}
	}
}
