package org.auie.base;

import java.util.Locale;

import org.auie.annotation.UEAnnotation.UELayout;
import org.auie.annotation.UEAnnotationManager;

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

/**
 * 基础类
 * 新建Fragment必须继承于UEFragment且实现abstract方法
 */
public abstract class UEFragment extends Fragment implements OnClickListener{

	public Activity activity = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
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
			if (initializtion.value() != -1) {
				view = inflater.inflate(initializtion.value(), null);
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
		UEAnnotationManager.getInstance().initializeFont(this, typeface);
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
	 * 自定义菜单点击事件
	 */
	public void onMenuClick(int which){}

	/**
	 * 简单跳转Activity方法
	 * @param intent 将要跳转的目的地
	 * @param isClose 完成后是否关闭此类
	 */
	public void startActivity(Intent intent, boolean isClose){
		startActivity(intent);
		if (isClose) {
			getActivity().finish();
		}
	}
	
}
