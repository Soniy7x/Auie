package org.auie.base;

import org.auie.annotation.UEAnnotationManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 
 * 基础类(Base Class)
 * 
 * AUIE框架入口，自定义的Activity继承UEActivity后，才可以使用UEAnnotation注释自动初始化布局、控件和对象
 * (If you want to use "AUIE", then a custom Activity must extend from UEActivity, it's the 
 * only entrance frame, it can only be used within the framework of the custom annotations, 
 * realize the automatic layout controls the initialization)
 * 
 * @author Soniy7x
 * 
 */
public abstract class UEActivity extends Activity implements OnClickListener{

	//全局Activity对象，可当作context使用
	//(Global Activity Object, can be used as Context)
	public final Activity activity = this;
	
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
		UEAnnotationManager.getInstance().initialize(this);
	}
	
	/**
	 * 初始化准备方法
	 * (Initialization prepares, here you can perform some method)
	 */
	protected void initializePrepare() {
		
	}
	
	/**
	 * 自定义字体方法
	 * (Use this method that set widget's font)
	 * @param typeface 自定义字体(customer font)
	 */
	protected void initializeFont(Typeface typeface) {
		UEAnnotationManager.getInstance().initializeFont(activity, typeface);
	}
	
	/**
	 * 实现OnClickListener接口
	 * (Perform onClickListener)
	 */
	@Override
	public void onClick(View v) {}
	
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
	 * 初始化完成方法，必须实现
	 * (Initialization is complete method that must be implemented)
	 */
	protected abstract void initializeFinish();
	
}
