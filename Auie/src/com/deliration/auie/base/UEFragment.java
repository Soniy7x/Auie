package com.deliration.auie.base;

import java.util.Locale;

import com.deliration.auie.annotation.UEAnnotationManager;
import com.deliration.auie.annotation.UELayout;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 基础类
 * 新建Fragment必须继承于UEFragment且实现abstract方法
 */
public abstract class UEFragment extends Fragment implements OnClickListener{

	public final Activity activity = getActivity();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		initializePrepare();
		return initializeBegin(inflater, container, savedInstanceState);
	}
	
	/**
	 * 初始化开始方法
	 */
	private View initializeBegin(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		if (this.getClass().isAnnotationPresent(UELayout.class)) {
			UELayout initializtion = this.getClass().getAnnotation(UELayout.class);
			if (initializtion.ID() != -1) {
				view = inflater.inflate(initializtion.ID(), null);
			}else{
				try{
					int layout = getActivity().getResources().getIdentifier(getActivity().getClass().getSimpleName().toLowerCase(Locale.getDefault()), "layout", getActivity().getPackageName());
					if (layout != 0) {
						view = inflater.inflate(layout, null);			
					}else{
						Log.e("Deliration", "资源错误 - 未找到名为" + getActivity().getClass().getSimpleName().toLowerCase(Locale.getDefault()) + ".xml的布局文件");
					}
				} catch (Exception e){
					Log.d("Deliration", e.getMessage());
				}
			}
		}		
		UEAnnotationManager.getInstance().initialize(this, view);
		initializeFinish();
		return view;
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
	 * 初始化完成方法，必须实现
	 */
	protected abstract void initializeFinish();
	
	
	/**
	 * 无传递参数简单跳转Activity方法
	 * @param clazz 将要跳转的目的地
	 * @param isClose 完成后是否关闭此类
	 */
	public void startActivity(Class<?> clazz, boolean isClose){
		startActivity(new Intent(getActivity(), clazz));
		if (isClose) {
			getActivity().finish();
		}
	}
	
	/**
	 * 弹出消息-持续时间短
	 * @param text 消息文本
	 */
	public void showMessageShort(String text){
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 弹出消息-持续时间长
	 * @param text 消息文本
	 */
	public void showMessageLong(String text){
		Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 弹出消息-自定义时间
	 * @param text 消息文本
	 * @param time 展示时长
	 */
	public void showMessage(String text, int time){
		Toast.makeText(getActivity(), text, time).show();
	}
}
