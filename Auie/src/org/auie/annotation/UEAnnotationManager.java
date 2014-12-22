package org.auie.annotation;

import java.lang.reflect.Field;
import java.util.Locale;

import org.auie.annotation.UEAnnotation.UEAlpha;
import org.auie.annotation.UEAnnotation.UEBackgroundColor;
import org.auie.annotation.UEAnnotation.UEBackgroundResource;
import org.auie.annotation.UEAnnotation.UEClickable;
import org.auie.annotation.UEAnnotation.UEEnabled;
import org.auie.annotation.UEAnnotation.UEID;
import org.auie.annotation.UEAnnotation.UELayout;
import org.auie.annotation.UEAnnotation.UENew;
import org.auie.annotation.UEAnnotation.UEOnClick;
import org.auie.annotation.UEAnnotation.UEText;
import org.auie.annotation.UEAnnotation.UETextColor;
import org.auie.annotation.UEAnnotation.UETextSize;
import org.auie.annotation.UEAnnotation.UEVisibility;
import org.auie.ui.UIButton;
import org.auie.ui.UIEditText;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * UEAnnotationManager
 * 注释管理类 · 提供自定义注释解析以及控件初始化
 * 
 * @author Soniy7x
 */
public final class UEAnnotationManager {
 
	//单一实例
	private static UEAnnotationManager instance = null;

	/**
	 * 私有化构造方法
	 */
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
	 * 初始化Activity字体
	 */
	public void initializeFont(Activity context, Typeface typeface){
		Field[] fields = context.getClass().getDeclaredFields();
		for (Field field : fields) {
			//判断是否含有UEID注释
			if (field.isAnnotationPresent(UEID.class)) {
				setFont(context, field, typeface);
			}
		}
	}
	
	/**
	 * 初始化Fragment字体
	 */
	public void initializeFont(Fragment fragment, Typeface typeface){
		Field[] fields = fragment.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(UEID.class)) {
				setFont(fragment, field, typeface);
			}
		}
	}
	
	/**
	 * 初始化Fragment字体
	 */
	private void setFont(Fragment fragment, Field field, Typeface typeface){
		field.setAccessible(true);
		try {
			Object object = field.get(fragment);
			if (object instanceof TextView || object instanceof EditText || object instanceof Button) {
				//反射机制调用方法设置参数
				field.getType().getMethod("setTypeface", Typeface.class).invoke(object, typeface);
			}
		}catch (Exception e) {
			Log.e("Deliration", e.toString());
			Log.e("Deliration", "资源错误 - 字体设置失败");
		}
	}
	
	/**
	 * 初始化Activity字体
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
	public void initialize(Activity context, boolean layout){
		if (layout && context.getClass().isAnnotationPresent(UELayout.class)) {
			initializeLayout(context);
		}
		Field[] fields = context.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(UEID.class)) {
				initializeWidgets(field, context);
			} else if (field.isAnnotationPresent(UENew.class)) {
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
			if (field.isAnnotationPresent(UEID.class)) {
				initializeWidgets(field, fragment, view);
			} else if (field.isAnnotationPresent(UENew.class)) {
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
		if (initialization.value() != -1) {
			context.setContentView(initialization.value());
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
		if (!field.isAnnotationPresent(UEID.class)) {
			return;
		}
		final UEID initialization = field.getAnnotation(UEID.class);
		int ID = initialization.value();
		try{
			field.setAccessible(true);
			if (ID == -1) {
				ID = context.getResources().getIdentifier(field.getName(), "id", context.getPackageName());
				if (ID == 0) {
					Log.e("Deliration", "资源错误 - 未找到ID为R.id." + field.getName() + "的控件");
					return;
				}
			}
			View view = context.findViewById(ID);
			Class<?> clazz = field.getType();
			field.set(context, view);
			if (field.isAnnotationPresent(UEVisibility.class)) {
				clazz.getMethod("setVisibility", int.class).invoke(view, context);
			}
			if (field.isAnnotationPresent(UEBackgroundColor.class)) {
				clazz.getMethod("setBackgroundColor", int.class).invoke(view, Color.parseColor(field.getAnnotation(UEBackgroundColor.class).value()));
			}
			if (field.isAnnotationPresent(UEAlpha.class)) {
				clazz.getMethod("setAlpha", float.class).invoke(view, field.getAnnotation(UEAlpha.class).value());
			}
			if (field.isAnnotationPresent(UEOnClick.class)) {
				clazz.getMethod("setOnClickListener", OnClickListener.class).invoke(view, context);
			}
			if (field.isAnnotationPresent(UEClickable.class)) {
				clazz.getMethod("setClickable", boolean.class).invoke(view, field.getAnnotation(UEClickable.class).value());
			}
			if (field.isAnnotationPresent(UEEnabled.class)) {
				clazz.getMethod("setEnabled", boolean.class).invoke(view, field.getAnnotation(UEEnabled.class).value());
			}
			if (field.isAnnotationPresent(UEBackgroundResource.class)) {
				int resId = field.getAnnotation(UEBackgroundResource.class).value();
				if (resId == -1) {
					resId = context.getResources().getIdentifier(field.getName(), "drawable", context.getPackageName());
				}
				if (resId < 1) {
					Log.e("Deliration", "资源错误 - 未找到ID为R.drawable." + field.getName() + "的图片");
				}else {					
					clazz.getMethod("setBackgroundResource", OnClickListener.class).invoke(view, resId);
				}
			}
			if (!isExpress(field.getType())) {
				return;
			}
			if (field.isAnnotationPresent(UEText.class)) {
				clazz.getMethod("setText", CharSequence.class).invoke(view, field.getAnnotation(UEText.class).value());
			}
			if (field.isAnnotationPresent(UETextSize.class)) {
				clazz.getMethod("setTextSize", new Class<?>[]{int.class, float.class}).invoke(view, new Object[]{TypedValue.COMPLEX_UNIT_SP, field.getAnnotation(UETextSize.class).value()});
			}
			if (field.isAnnotationPresent(UETextColor.class)) {
				clazz.getMethod("setTextColor", int.class).invoke(view, Color.parseColor(field.getAnnotation(UETextColor.class).value()));
			}
		}catch(Exception e){
			Log.d("initializeWidgets", e.toString());
		}
	}
	
	/**
	 * 初始化Object对象
	 * @param field
	 * @param context
	 */
	private void initializeObjects(Field field, Activity context){
		if (field.isAnnotationPresent(UENew.class)) {
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
		if (field.isAnnotationPresent(UEID.class)) {
			final UEID initialization = field.getAnnotation(UEID.class);
			int ID = initialization.value();
			try{
				field.setAccessible(true);
				if (ID == -1) {
					ID = fragment.getResources().getIdentifier(field.getName(), "id", fragment.getActivity().getPackageName());
					if (ID == 0) {
						Log.e("Deliration", "资源错误 - 未找到ID为R.id." + field.getName() + "的控件");
					}
				}
				field.set(fragment, view.findViewById(ID));
				if (field.isAnnotationPresent(UEOnClick.class)) {
					field.getType().getMethod("setOnClickListener", OnClickListener.class)
					.invoke(view.findViewById(ID), fragment);
				}
				if (field.isAnnotationPresent(UETextColor.class)) {
					field.getType().getMethod("setTextColor", int.class)
					.invoke(view.findViewById(ID), Color.parseColor(field.getAnnotation(UETextColor.class).value()));
				}
				if (field.isAnnotationPresent(UETextSize.class)) {
					field.getType().getMethod("setTextSize", float.class)
					.invoke(view.findViewById(ID), field.getAnnotation(UETextSize.class).value());
				}
				if (field.isAnnotationPresent(UEBackgroundColor.class)) {
					field.getType().getMethod("setBackgroundColor", int.class)
					.invoke(view.findViewById(ID), Color.parseColor(field.getAnnotation(UEBackgroundColor.class).value()));
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
		if (field.isAnnotationPresent(UENew.class)) {
			try {
				field.setAccessible(true);
				field.set(fragment, field.getType().newInstance());
			} catch (Exception e) {
				Log.d("initializeObjects", e.getMessage());
			}
		}
	}
	
	private static final Class<?>[] EXPRESS_CLASSES = {
		TextView.class,
		EditText.class,
		Button.class,
		UIButton.class,
		UIEditText.class,
	};
	
	private boolean isExpress(Class<?> clazz){
		for (Class<?> express : EXPRESS_CLASSES) {
			if (clazz == express) {
				return true;
			}
		}
		return false;
	}
}
