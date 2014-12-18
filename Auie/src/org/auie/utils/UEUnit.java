package org.auie.utils;

import android.content.Context;

public final class UEUnit {

	private static UEUnit instance;
	private static float scale;
	
	private UEUnit(){}
	
	public static UEUnit getInstance(Context context){
		if (instance == null) {
			instance = new UEUnit();
			scale = context.getResources().getDisplayMetrics().density;
		}
		return instance;
	}
	
	public int translatePX(float dp){
		return (int)(dp * scale + 0.5F);
	}
	
	public int translateDP(float px){
		return (int)(px / scale + 0.5F);
	}
}
