package org.auie.base;

import org.auie.utils.UEDevice;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.WindowManager;

/**
 * 
 * 基础类
 * 
 * AUIE框架入口，自定义的Activity继承UEActivity后，才可以使用UEAnnotation注释自动初始化布局、控件和对象=
 * 
 * @author Soniy7x
 * 
 */
public abstract class UEKitKatFragmentActivity extends UEFragmentActivity{

	/**
	 * 初始化准备方法
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected void initializePrepare() {
		if (UEDevice.getOSVersionCode() >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}
}
