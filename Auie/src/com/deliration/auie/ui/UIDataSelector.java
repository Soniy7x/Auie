package com.deliration.auie.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class UIDataSelector extends PopupWindow{

	public final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
	public final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	public static final int MODE_AUTOCLOSE = 99;
    public static final int MODE_NO_AUTOCLOSE = 98;
	
    public static final int MODE_ONE = 1;
    public static final int MODE_TWO = 2;
    public static final int MODE_ALL = 3;
    
	private int WIDTH = 0;
	private int HEIGHT = 180;
	private Context context;
	private Typeface typeface;
    private float scale;
    private float padding = 0;
	private int backgroundColor = Color.parseColor("#FFF3F3F3");
	private int topLineColor = Color.parseColor("#33444444");
	private int leftLineColor = Color.parseColor("#33444444");
	private int rightLineColor = Color.parseColor("#33444444");
	private int itemTextColor = Color.parseColor("#cc444444");
//	private int itemSelectTextColor = Color.parseColor("#FF3DB399");
	private ListView listView1;
	private ListView listView2;
	private ListView listView3;
	private int mode = MODE_ONE;
	private int autoMode = MODE_AUTOCLOSE;
	private int itemTextSize = 14;
	private List<DataExtraAdpater> dataAdapters = new ArrayList<DataExtraAdpater>();
	
	public UIDataSelector(Context context, int height){
		this(context);
		this.HEIGHT = height;
	}
	
	@SuppressWarnings("deprecation")
	public UIDataSelector(Context context){
		this.context = context;
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		this.WIDTH = manager.getDefaultDisplay().getWidth();
        this.scale = context.getResources().getDisplayMetrics().density;
	}
	
	public UIDataSelector setMode(int mode){
		this.mode = mode;
		return this;
	}
	
	public UIDataSelector setAutoMode(int autoMode){
		this.autoMode = autoMode;
		return this;
	}
	
	public UIDataSelector setPadding(int padding){
		this.padding = padding;
		return this;
	}
	
	public UIDataSelector setTypeface(Typeface typeface){
		this.typeface = typeface;
		return this;
	}
	
	public UIDataSelector setItemTextColor(int itemTextColor){
		this.itemTextColor = itemTextColor;
		return this;
	}
	
//	public UIDataSelector setItemSelectTextColor(int itemSelectTextColor){
//		this.itemSelectTextColor = itemSelectTextColor;
//		return this;
//	}
	
	public UIDataSelector setItemTextSize(int itemTextSize){
		this.itemTextSize = itemTextSize;
		return this;
	}
	
	public void changAdapterData(int index, String[] newData){
		if (dataAdapters.size() > index) {
			dataAdapters.get(index).adpater.datas = newData;
			dataAdapters.get(index).adpater.notifyDataSetChanged();
		}else {
			Log.e("Deliration", "无此项数据源");
		}
	}
	
	public void addAdapterData(List<String> data, OnItemSelectListener listener){
		addAdapterData((String[])data.toArray(), listener);
	}
	
	public void addAdapterData(String[] data, OnItemSelectListener listener){
		dataAdapters.add(new DataExtraAdpater(new DataSelectorAdpater(data), listener));
	}
	
	public void removeAdapterData(int index){
		dataAdapters.remove(index);
	}
	
	public void clearAdapterDatas(){
		dataAdapters.clear();
	}
	
	private int dp2px(float dp){
        return (int) (dp * scale + 0.5f);
    }
	
	private LinearLayout.LayoutParams getParams(int width, int height){
        return new LinearLayout.LayoutParams(width, height);
    }
	
	private LinearLayout.LayoutParams getParams(int width, int height, int weight){
        return new LinearLayout.LayoutParams(width, height, weight);
    }
	
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams") 
	public UIDataSelector builder(){
//		selector = new PopupWindow(context);
		setBackgroundDrawable(new BitmapDrawable());
		setContentView(createContentView());
		setWidth(WIDTH);
		setHeight(dp2px(HEIGHT));
		setFocusable(true);
		return this;
	}
	
	@SuppressWarnings("deprecation")
	private View createContentView(){
		LinearLayout rootView = new LinearLayout(context);
		rootView.setLayoutParams(getParams(MATCH_PARENT, dp2px(HEIGHT)));
		rootView.setBackgroundColor(backgroundColor);
		rootView.setOrientation(LinearLayout.VERTICAL);
		View topLineView = new View(context);
		topLineView.setLayoutParams(getParams(MATCH_PARENT, dp2px(0.5f)));
		topLineView.setBackgroundColor(topLineColor);
		LinearLayout contentView = new LinearLayout(context);
		contentView.setLayoutParams(getParams(MATCH_PARENT, MATCH_PARENT));
		contentView.setOrientation(LinearLayout.HORIZONTAL);
		switch (mode) {
		case 3:
			listView3 = new ListView(context);
			listView3.setLayoutParams(getParams(0, MATCH_PARENT, 1));
			listView3.setPadding(dp2px(padding), dp2px(padding), dp2px(padding), dp2px(padding));
			listView3.setVerticalScrollBarEnabled(false);
			listView3.setSelector(new BitmapDrawable());
			listView3.setDivider(context.getResources().getDrawable(android.R.color.transparent));
			listView3.setOnItemClickListener(itemClickListener);
			if (dataAdapters.size() > 2) {
				listView3.setAdapter(dataAdapters.get(2).adpater);
				listView3.setTag(dataAdapters.get(2));
			}else{
				listView3.setAdapter(getDefaultAdapter());
			}
		case 2:
			listView2 = new ListView(context);
			listView2.setLayoutParams(getParams(0, MATCH_PARENT, 1));
			listView2.setPadding(dp2px(padding), dp2px(padding), dp2px(padding), dp2px(padding));
			listView2.setVerticalScrollBarEnabled(false);
			listView2.setSelector(new BitmapDrawable());
			listView2.setDivider(context.getResources().getDrawable(android.R.color.transparent));
			listView2.setOnItemClickListener(itemClickListener);
			if (dataAdapters.size() > 1) {
				listView2.setAdapter(dataAdapters.get(1).adpater);
				listView2.setTag(dataAdapters.get(1));
			}else{
				listView2.setAdapter(getDefaultAdapter());
			}
		default:
			listView1 = new ListView(context);
			listView1.setLayoutParams(getParams(0, MATCH_PARENT, 1));
			listView1.setPadding(dp2px(padding), dp2px(padding), dp2px(padding), dp2px(padding));
			listView1.setVerticalScrollBarEnabled(false);
			listView1.setSelector(new BitmapDrawable());
			listView1.setDivider(context.getResources().getDrawable(android.R.color.transparent));
			listView1.setOnItemClickListener(itemClickListener);
			if (dataAdapters.size() > 0) {
				listView1.setAdapter(dataAdapters.get(0).adpater);
				listView1.setTag(dataAdapters.get(0));
			}else{
				listView1.setAdapter(getDefaultAdapter());
			}
			break;
		}
		contentView.addView(listView1);
		if (listView2 != null) {
			View leftLineView = new View(context);
			leftLineView.setLayoutParams(getParams(dp2px(0.5f), MATCH_PARENT));
			leftLineView.setBackgroundColor(leftLineColor);
			contentView.addView(leftLineView);
			contentView.addView(listView2);
		}
		if (listView3 != null) {
			View rightLineView = new View(context);
			rightLineView.setLayoutParams(getParams(dp2px(0.5f), MATCH_PARENT));
			rightLineView.setBackgroundColor(rightLineColor);
			contentView.addView(rightLineView);
			contentView.addView(listView3);
		}
		rootView.addView(topLineView);
		rootView.addView(contentView);
		return rootView;
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
			DataExtraAdpater adpater = (DataExtraAdpater) adapterView.getTag();
			adpater.itemSelectListener.OnItemSelect(adapterView, adpater.adpater.datas, position, adpater.adpater.datas[position]);
			if (autoMode == MODE_AUTOCLOSE) {
				close();
			}
		}
	};
	
	private ListAdapter getDefaultAdapter(){
		return new DataSelectorAdpater(new String[]{"无数据源"});
	}
	
	public UIDataSelector show(){
		showAtLocation(((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0), Gravity.BOTTOM, 0, 0);
		return this;
	}
	
	public UIDataSelector show(View parentView){
		showAsDropDown(parentView);
		return this;
	}
	
	public UIDataSelector show(View parentView, int x, int y){
		showAsDropDown(parentView, x, y);
		return this;
	}
	
	public UIDataSelector show(View parentView,int gravity, int x, int y){
		showAtLocation(parentView, gravity, x, y);
		return this;
	}
	
	public interface OnItemSelectListener{
		public void OnItemSelect(View view, String[] datas, int position, String value);
	}
	
	private void close(){
		dismiss();
	}
	
	class DataExtraAdpater{
		public DataSelectorAdpater adpater;
		public OnItemSelectListener itemSelectListener;
		
		public DataExtraAdpater(DataSelectorAdpater adpater, OnItemSelectListener itemSelectListener){
			this.adpater = adpater;
			this.itemSelectListener = itemSelectListener;
		}
	}
	
	class DataSelectorAdpater extends BaseAdapter{
		
		private String[] datas;
		
		public DataSelectorAdpater(String[] datas){
			this.datas = datas;
		}
		
		@Override
		public int getCount() {
			return datas.length;
		}

		@Override
		public Object getItem(int position) {
			return datas[position];
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			if (view == null) {
				TextView textView = new TextView(context);
				textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, dp2px(36)));
				textView.setTextSize(itemTextSize);
				textView.setTextColor(itemTextColor);
				textView.setGravity(Gravity.CENTER);
				if (typeface != null) {
					textView.setTypeface(typeface);
				}
				view = textView;
			}
			((TextView)view).setText(datas[position]);
			return view;
		}
		
	}
}
