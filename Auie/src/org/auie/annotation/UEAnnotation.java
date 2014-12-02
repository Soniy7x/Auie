package org.auie.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * UEAnnotation
 * 注释汇总 · 提供初始化注释标签
 * 
 * @author Soniy7x
 */
public final class UEAnnotation {

	/**
	 * 布局 · 解析R.layout.xxx布局索引
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UELayout {
		int value() default -1;
	}
	
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEConfig {
		int value() default -1;
	}

	/**
	 * 类 · 初始化对象
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UENew {
		boolean value() default true;
	}

	/**
	 * View及子类通用 · 解析R.id.xxx控件索引
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEID {
		int value() default -1;
	}

	/**
	 * View及子类通用 · 绑定控件点击事件
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEOnClick {
		boolean value() default true;
	}

	/**
	 * View及子类通用 · 解析16进制字串符背景颜色
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEBackgroundColor {
		String value() default "#00000000";
	}

	/**
	 * View及子类通用 · 控制控件是否可视
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEVisibility {
		int value() default 0x00000000;
	}

	/**
	 * View及子类通用 · 设置控件透明度(0 - 1f)
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEAlpha {
		float value() default 0.5f;
	}

	/**
	 * View及子类通用 · 解析R.drawable.xxx背景图片索引
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEBackgroundResource {
		int value() default -1;
	}

	/**
	 * View及子类通用 · 控制控件是否允许响应点击
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEClickable {
		boolean value() default false;
	}

	/**
	 * View及子类通用 · 控制控件是否可用
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEEnabled {
		boolean value() default false;
	}

	/**
	 * 特殊View类 · 解析16进制字符串字体颜色
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UETextColor {
		String value() default "#444444";
	}

	/**
	 * 特殊View类 · 设置字体大小SP
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UETextSize {
		int value() default 14;
	}

	/**
	 * 特殊View类 · 设置文本内容
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UEText {
		String value() default "";
	}
}
