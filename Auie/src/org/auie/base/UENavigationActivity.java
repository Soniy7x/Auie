package org.auie.base;

import java.util.Locale;

import org.auie.annotation.UEAnnotation.UELayout;
import org.auie.annotation.UEAnnotationManager;
import org.auie.ui.UIBatteryView;
import org.auie.ui.UINavigationView;
import org.auie.ui.UISingalView;
import org.auie.utils.UEDevice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
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
	private void initializeBegin() {
		wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mNavigationView = new UINavigationView(this);
		mNavigationView.setNetworkText(UEDevice.getNetworkType(activity));
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mSingalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		addContentView(mNavigationView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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
			mNavigationView.addView(LayoutInflater.from(activity).inflate(layout, null));
		}
		UEAnnotationManager.getInstance().initialize(this, false);
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

	protected void setLeftImageResource(int resId){
		mNavigationView.setLeftImageResource(resId);
	}
	
	protected void setLeftText(String text){
		mNavigationView.setLeftText(text);
	}
	
	protected void setTitle(String title){
		mNavigationView.setTitle(title);
	}
	
	public void setLeftImageOnClickListener(OnClickListener mListener){
		mNavigationView.setLeftImageOnClickListener(mListener);
	}
	
	public void setLeftTextOnClickListener(OnClickListener mListener){
		mNavigationView.setLeftTextOnClickListener(mListener);
	}
	
	public void setLeftOnClickListener(OnClickListener mListener){
		mNavigationView.setLeftOnClickListener(mListener);
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
	
	
}
