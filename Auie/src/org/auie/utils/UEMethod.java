package org.auie.utils;

import android.content.Context;

/**
 * 
 * 通用方法类(General Method Class)
 * 
 * @author Soniy7x
 *
 */
public final class UEMethod {

	/**
	 * DP 转化 PX (Convert DP to PX)
	 * @param context 上下文(Context or Activity)
	 * @param dp DP大小(number of DP)
	 * @return int PX大小(number of PX)
	 */
	public static int dp2px(Context context, float dp){
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
	
	/**
	 * DP 转化 PX (Convert DP to PX)
	 * @param context 上下文(Context or Activity)
	 * @param dp DP大小(number of DP)
	 * @return int PX大小(number of PX)
	 */
	public static float dp2pxReturnFloat(Context context, float dp){
        return dp * context.getResources().getDisplayMetrics().density + 0.5f;
    }
}
