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
 * 基础类
 * 
 * AUIE框架入口，自定义的Activity继承UEActivity后，才可以使用UEAnnotation注释自动初始化布局、控件和对象=
 * 
 * @author Soniy7x
 * 
 */
public abstract class UEActivity extends Activity implements OnClickListener{

	//全局Activity对象，可当作context使用
	public final Activity activity = this;
	
	/**
	 * 复写onCreat方法，调用初始化入口方法，严禁复写
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
	 */
	private void initializeBegin() {
		UEAnnotationManager.getInstance().initialize(this, true);
	}
	
	/**
	 * 初始化准备方法
	 */
	protected void initializePrepare() {}
	
	/**
	 * 自定义字体方法
	 * @param typeface 自定义字体
	 */
	protected void initializeFont(Typeface typeface) {
		UEAnnotationManager.getInstance().initializeFont(activity, typeface);
	}
	
	/**
	 * 实现OnClickListener接口
	 */
	@Override
	public void onClick(View v) {}
	
	/**
	 * 无传递参数简单跳转Activity方法
	 * @param clazz 将要跳转的目的地
	 * @param isClose 完成后是否关闭此类
	 */
	public void startActivity(Class<?> clazz, boolean isClose){
		startActivity(new Intent(this, clazz));
		if (isClose) {
			finish();
		}
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
	
	/**
	 * 初始化完成方法，必须实现
	 */
	protected abstract void initializeFinish();
	
}
