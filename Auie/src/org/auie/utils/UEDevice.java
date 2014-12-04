package org.auie.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public final class UEDevice {

	public static final int SCREEN_240P = 240;
	public static final int SCREEN_360P = 360;
	public static final int SCREEN_480P = 480;
	public static final int SCREEN_720P = 720;
	public static final int SCREEN_1080P = 1080;
	public static final int SCREEN_1280P = 1280;
	
	private UEDevice(){}
	
	/**
	 * 获取屏幕高度
	 */
	public static int getScreenHeight(Context context){
		DisplayMetrics dm = null;
		dm = context.getResources().getDisplayMetrics();
		if (dm == null) {
			return -1;
		}
		return dm.heightPixels;
	}
	
	/**
	 * 获取屏幕分辨率
	 */
	public static int getDeviceScreen(Context context){
		DisplayMetrics dm = null;
		dm = context.getResources().getDisplayMetrics();
		if (dm == null) {
			return -1;
		}
		int screenWidth = dm.widthPixels;
		if (screenWidth <= SCREEN_240P) {
			return SCREEN_240P;
		}else if (screenWidth > SCREEN_240P && screenWidth <= SCREEN_360P) {
			return SCREEN_360P;
		}else if (screenWidth > SCREEN_360P && screenWidth <= SCREEN_480P) {
			return SCREEN_480P;
		}else if (screenWidth > SCREEN_480P && screenWidth <= SCREEN_720P) {
			return SCREEN_720P;
		}else if (screenWidth > SCREEN_720P && screenWidth <= SCREEN_1080P) {
			return SCREEN_1080P;
		}else {
			return SCREEN_1280P;
		}
	}
	
	/**
	 * 获取网络类型
	 */
	public static String getNetworkType(Context context){
		NetworkInfo info = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info == null) {
			return "无网络";
		}
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return "WIFI";
		}
		switch (info.getSubtype()) {
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return "3G";
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return "2G";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "4G";
		default:
			return "";
		}
	}
	
	/**
	 * 获取SIM卡运营商
	 */
	public static String getSimType(Context context){
		String IMSI = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
		if (TextUtils.isEmpty(IMSI)) {
			return "无服务";
		}
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			return "中国移动";
		}else if (IMSI.startsWith("46001")) {
			return "中国联通";
		}else if (IMSI.startsWith("46003")) {
			return "中国电信";
		}else {
			return "未知运营商";
		}
	}
	
	/**
	 * 获取IEMI
	 */
	public static String getDeviceIEMI(Context context){
		return String.valueOf(((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
	}
	
	/**
	 * 获取设备型号
	 */
	public static String getDeviceModel(){
		return android.os.Build.MODEL;
	}
	
	/**
	 * 获取系统版本名
	 */
	public static String getOSVersionName(){
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获取系统版本号
	 */
	public static int getOSVersionCode(){
		return android.os.Build.VERSION.SDK_INT;
	}
}
