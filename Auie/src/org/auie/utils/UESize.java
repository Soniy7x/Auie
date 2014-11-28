package org.auie.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

public final class UESize {
	
	private UESize(){}
	
	public static void autoTextSize(Context context, Object object, int device){
		Class<?> clazz = object.getClass();
		float baseSize;
		try {
			Method method = clazz.getMethod("getTextSize", new Class[]{});
			baseSize = (float) method.invoke(object, new Object[]{});
			if (baseSize < 0) {
				return;
			}
			int screen = UEDevice.getDeviceScreen(context);
			if (screen < 0) {
				return;
			}
			method.setAccessible(true);
			float over = screen/(float)device * 4;
			if (screen >  device) {
				baseSize += over;
			}else if (screen < device) {
				baseSize -= over;
			}
			method = clazz.getMethod("setTextSize", new Class[]{int.class,float.class});
			method.invoke(object, new Object[]{TypedValue.COMPLEX_UNIT_PX, baseSize});
		} catch (Exception e) {
			Log.w(UE.TAG, "该参数不支持此方法" + e.toString());
		} 
	}
	
}
