package org.auie.utils;

import android.util.SparseArray;
import android.view.View;

public final class UEViewHolder {

	private UEViewHolder(){}
	
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> holder = (SparseArray<View>)view.getTag();	
		if (holder == null) {
			holder = new SparseArray<View>();
			view.setTag(holder);
		}
		View childView = holder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			holder.put(id, childView);
		}
		return (T) childView;
	}
}
