package com.deliration.auie.base;

import com.deliration.auie.annotation.UEAnnotationManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * 基础类
 * 新建Activity必须继承于UEActivity且实现abstract方法
 */
public abstract class UEActivity extends Activity implements OnClickListener{

	public final Activity activity = this;
	
	/**
	 * 重载onCreat方法，调用初始化方法
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
		UEAnnotationManager.getInstance().initialize(this);
	}
	
	/**
	 * 初始化准备方法
	 */
	protected void initializePrepare() {
		
	}
	
	/**
	 * 自定义字体
	 */
	protected void initializeFont(Typeface typeface) {
		UEAnnotationManager.getInstance().initializeFont(activity, typeface);
	}
	
	/**
	 * 重装onClick方法
	 */
	@Override
	public void onClick(View v) {
	}
	
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
	 * 弹出消息-持续时间短
	 * @param text 消息文本
	 */
	public void showMessageShort(String text){
		Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 弹出消息-持续时间长
	 * @param text 消息文本
	 */
	public void showMessageLong(String text){
		Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 弹出消息-自定义时间
	 * @param text 消息文本
	 * @param time 展示时长
	 */
	public void showMessage(String text, int time){
		Toast.makeText(activity, text, time).show();
	}
	
	/**
	 * 初始化完成方法，必须实现
	 */
	protected abstract void initializeFinish();
	
}
