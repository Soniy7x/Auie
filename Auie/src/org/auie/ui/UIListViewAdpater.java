package org.auie.ui;


import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.widget.ListAdapter;

public abstract class UIListViewAdpater implements ListAdapter {
	
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public boolean hasStableIds() {
        return false;
    }
    
    public abstract void deleteClick(int position);
    
    public abstract void actionClick(int position);

    public abstract void otherClick(int position);
    
    public abstract void customerClick(int position, View v);
    
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
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
