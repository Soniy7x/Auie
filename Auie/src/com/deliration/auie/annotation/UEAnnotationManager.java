package com.deliration.auie.annotation;

import java.lang.reflect.Field;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UEAnnotationManager {
 
	private static UEAnnotationManager instance = null;

	private UEAnnotationManager() {}

	/**
	 * 获取AnnotationManager单一实例
	 * @return 单一实例
	 */
	public static UEAnnotationManager getInstance() {
		if (instance == null) {
			instance = new UEAnnotationManager();
		}
		return instance;
	}
	
	/**
	 * 初始化字体
	 */
	public void initializeFont(Activity context, Typeface typeface){
		Field[] fields = context.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(UEWidget.class)) {
				setFont(context, field, typeface);
			}
		}
	}
	
	/**
	 * 初始化字体
	 */
	private void setFont(Context context, Field field, Typeface typeface){
		field.setAccessible(true);
		try {
			Object object = field.get(context);
			if (object instanceof TextView || object instanceof EditText || object instanceof Button) {
				field.getType().getMethod("setTypeface", Typeface.class).invoke(object, typeface);
			}
		}catch (Exception e) {
			Log.e("Deliration", e.toString());
			Log.e("Deliration", "资源错误 - 字体设置失败");
		}
	}
	
	/**
	 * 初始化方法
	 * @param context 上下文
	 */
	public void initialize(Activity context){
		if (context.getClass().isAnnotationPresent(UELayout.class)) {
			initializeLayout(context);
		}
		Field[] fields = context.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(UEWidget.class)) {
				initializeWidgets(field, context);
			} else if (field.isAnnotationPresent(UEObject.class)) {
				initializeObjects(field, context);
			}
		}
	}
	
	/**
	 * 初始化方法
	 * @param fragment
	 * @param view
	 */
	public void initialize(Fragment fragment, View view) {
		Field[] fields = fragment.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(UEWidget.class)) {
				initializeWidgets(field, fragment, view);
			} else if (field.isAnnotationPresent(UEObject.class)) {
				initializeObjects(field, fragment, view);
			}
		}
	}
	
	/**
	 * 初始化Activity布局
	 * @param context
	 */
	private void initializeLayout(Activity context){
		UELayout initialization = context.getClass().getAnnotation(UELayout.class);
		if (initialization.ID() != -1) {
			context.setContentView(initialization.ID());
		} else {
			try{
				int layout = context.getResources().getIdentifier(context.getClass().getSimpleName().toLowerCase(Locale.getDefault()), "layout", context.getPackageName());
				if (layout != 0) {
					context.setContentView(layout);					
				}else{
					Log.e("Deliration", "资源错误 - 未找到名为" + context.getClass().getSimpleName().toLowerCase(Locale.getDefault()) + ".xml的布局文件");
				}
			} catch (Exception e){
				Log.d("Deliration", e.getMessage());
			}
		}
	}
	
	/**
	 * 初始化UI控件
	 * @param field
	 * @param context
	 */
	private void initializeWidgets(Field field, Activity context){
		if (field.isAnnotationPresent(UEWidget.class)) {
			final UEWidget initialization = field.getAnnotation(UEWidget.class);
			int ID = initialization.ID();
			try{
				field.setAccessible(true);
				if (ID == -1) {
					ID = context.getResources().getIdentifier(field.getName(), "id", context.getPackageName());
					if (ID == 0) {
						Log.e("Deliration", "资源错误 - 未找到ID为R.id." + field.getName() + "的控件");
					}
				}
				field.set(context, context.findViewById(ID));
				if (initialization.onClick()) {
					field.getType().getMethod("setOnClickListener", OnClickListener.class).invoke(context.findViewById(ID), context);
				}
			}catch(Exception e){
				Log.d("initializeWidgets", e.getMessage());
			}
		}
	}
	
	/**
	 * 初始化Object对象
	 * @param field
	 * @param context
	 */
	private void initializeObjects(Field field, Activity context){
		if (field.isAnnotationPresent(UEObject.class)) {
			try {
				field.setAccessible(true);
				field.set(context, field.getType().newInstance());
			} catch (Exception e) {
				Log.d("initializeObjects", e.getMessage());
			}
		}
	}
	
	/**
	 * 初始化UI控件
	 * @param field
	 * @param context
	 */
	private void initializeWidgets(Field field, Fragment fragment, View view){
		if (field.isAnnotationPresent(UEWidget.class)) {
			final UEWidget initialization = field.getAnnotation(UEWidget.class);
			int ID = initialization.ID();
			try{
				field.setAccessible(true);
				if (ID == -1) {
					ID = fragment.getResources().getIdentifier(field.getName(), "id", fragment.getActivity().getPackageName());
					if (ID == 0) {
						Log.e("Deliration", "资源错误 - 未找到ID为R.id." + field.getName() + "的控件");
					}
				}
				field.set(fragment, view.findViewById(ID));
				if (initialization.onClick()) {
					field.getType().getMethod("setOnClickListener", OnClickListener.class).invoke(view.findViewById(ID), fragment);
				}
			}catch(Exception e){
				Log.d("initializeWidgets", e.getMessage());
			}
		}
	}
	
	/**
	 * 初始化Object对象
	 * @param field
	 * @param context
	 */
	private void initializeObjects(Field field, Fragment fragment, View view){
		if (field.isAnnotationPresent(UEObject.class)) {
			try {
				field.setAccessible(true);
				field.set(fragment, field.getType().newInstance());
			} catch (Exception e) {
				Log.d("initializeObjects", e.getMessage());
			}
		}
	}
}
