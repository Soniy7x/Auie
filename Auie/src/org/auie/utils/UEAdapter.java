package org.auie.utils;

import java.util.ArrayList;
import java.util.List;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

public abstract class UEAdapter implements ListAdapter, SpinnerAdapter {

	private final DataSetObservable mDataSetObservable = new DataSetObservable();
	private Class<?> className;
	
	protected List<Object> bckupDatas;
	protected List<Object> datas;
	
	public UEAdapter(List<?> datas){
		this.datas = changeObject(datas);
		this.bckupDatas = this.datas;
		if (datas != null && datas.size() > 0) {
			className = datas.get(0).getClass();
		}
	}
	
	public List<?> getDatas(){
		return datas;
	}
	
	@Override
	public int getCount() {
		if (datas == null) {
			return 0;
		}
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void refresh(List<?> datas){
		if (datas.size() > 0) {
			className = datas.get(0).getClass();
		}
		this.datas = changeObject(datas);
		notifyDataSetChanged();
	}
	
	public void refresh(int position, Object data){
		this.datas.set(position, data);
		notifyDataSetChanged();
	}
	
	public void reset(){
		this.datas.clear();
		this.datas = bckupDatas;
		notifyDataSetChanged();
	}
	
	private List<Object> changeObject(List<?> datas){
		if (datas == null) {
			return null;
		}
		List<Object> objects = new ArrayList<Object>();
		if (datas.size() > 0 ) {
			for (int i = 0; i < datas.size(); i++) {
				objects.add(datas.get(i));
			}
		}
		return objects;
	}
	
	public boolean addItem(Object data){
		if (className != null) {
			if (data.getClass() != className) {
				return false;
			}
		}else {
			className = data.getClass();
		}
		datas.add(data);
		notifyDataSetChanged();	
		return true;
	}
	
	public boolean addItems(List<?> datas){
		if (datas.size() < 0) {
			return true;
		}
		if (className != null) {
			if (className != datas.get(0).getClass()) {
				return false;
			}
		}else {
			className = datas.get(0).getClass();
		}
		this.datas.addAll(changeObject(datas));
		notifyDataSetChanged();
		return true;
	}
	
	public boolean removeItem(int position){
		if (datas.size() > position) {
			datas.remove(position);
			notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	public boolean removeItem(Object data){
		if (datas.contains(data)) {
			datas.remove(data);
			notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.registerObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.unregisterObserver(observer);
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }
	
	public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }
    
    public boolean isEmpty() {
        return getCount() == 0;
    }

}
