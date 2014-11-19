package org.auie.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public final class UEDevice {

	private UEDevice(){}
	
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
	public static String getOSVerisonName(){
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获取系统版本号
	 */
	public static int getOSVerisonCode(){
		return android.os.Build.VERSION.SDK_INT;
	}
}
