package org.auie.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.auie.utils.UEAdapter;
import org.auie.utils.UEMethod;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class UIDataSelector extends PopupWindow{
	
	public static final int MODE_ONE = 1;
    public static final int MODE_TWO = 2;
    public static final int MODE_MAX = 3;
	
	private int DP = 0;
	private int WIDTH = 0;
	private int HEIGHT = 180;
	private int[] SELECTED_INDEX = {-1, -1, -1};
	
	private float topLineHeight = 0.5f;
	private float leftLineWidth = 0.5f;
	private float rightLineWidth = 0.5f;
	private float padding = 0;
	private int backgroundColor = Color.parseColor("#FFF3F3F3");
	private int topLineColor = Color.parseColor("#33444444");
	private int leftLineColor = Color.parseColor("#33444444");
	private int rightLineColor = Color.parseColor("#33444444");
	private int itemTextColor = Color.parseColor("#cc444444");
	private int itemTextSelectColor = Color.parseColor("#FFFFFF");
	private int itemSelectBackgroundColor = Color.parseColor("#D8D8D8");
	private int itemTextSize = 14;
	private Typeface typeface;
	
	private int mode = MODE_ONE;
	private Context context;
	private LinearLayout rootContainer;
	private View topLineView;
	private View leftLineView;
	private View rightLineView;
	private LinearLayout dataContainer;
	private ListView fristListView;
	private ListView secondListView;
	private ListView thirdListView;
	private boolean auto = true;
	private boolean relation = true;
	
	private UIDataSelector selector = this;
	
	private OnItemSelectListener onItemSelectListener;
	private List<DataSelectorAdpater> dataAdapters = new ArrayList<DataSelectorAdpater>();
	
	/**
	 * 构造方法
	 */
	public UIDataSelector(Context context, OnItemSelectListener listener){
		this(context, MODE_ONE, listener);
	}
	
	/**
	 * 构造方法
	 * ((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0).getHeight()
	 */
	@SuppressWarnings("deprecation")
	public UIDataSelector(Context context, int mode, OnItemSelectListener listener){
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		this.context = context;
		this.mode = mode;
		this.DP = UEMethod.dp2px(context, 1);
		this.WIDTH = manager.getDefaultDisplay().getWidth();
		this.onItemSelectListener = listener;
		init();
	}
	
	/**
	 * 初始化数据集
	 */
	private void init() {
		if (mode > MODE_MAX) {
			mode = MODE_MAX;
		}
		dataAdapters.clear();
		for (int i = 0; i < mode; i++) {
			dataAdapters.add(getDefaultAdapter(i));
		}
	}
	
	/**
	 * 刷新适配器数据集
	 * @param index 数据集索引
	 * @param data 数组数据集
	 */
	public UIDataSelector refreshAdapterData(int index, String[] data){
		return refreshAdapterData(index, Arrays.asList(data));
	}
	
	/**
	 * 刷新适配器数据集
	 * @param index 数据集索引
	 * @param data 列表数据集
	 */
	public UIDataSelector refreshAdapterData(int index, List<String> data){
		if (index < dataAdapters.size() && index >= 0 && data != null) {
			dataAdapters.get(index).refresh(data);
		}
		return this;
	}
	
	/**
	 * 构建内容
	 */
	private void builder(){
		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(createContentView());
		setWidth(WIDTH);
		setHeight(HEIGHT * DP);
		setFocusable(true);
	}
	
	/**
	 * 构建内容视图
	 * @return 内容视图
	 */
	private View createContentView(){
		rootContainer = new LinearLayout(context);
		rootContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		rootContainer.setBackgroundColor(backgroundColor);
		rootContainer.setOrientation(LinearLayout.VERTICAL);
		topLineView = new View(context);
		topLineView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int)(topLineHeight * DP)));
		topLineView.setBackgroundColor(topLineColor);
		dataContainer = new LinearLayout(context);
		dataContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		dataContainer.setOrientation(LinearLayout.HORIZONTAL);
		dataContainer.setGravity(Gravity.CENTER);
		int paddingPixel = (int)(padding * DP);
		switch (mode) {
		case 3:
			thirdListView = new ListView(context);
			thirdListView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			thirdListView.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
			thirdListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			thirdListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
			thirdListView.setOnItemClickListener(itemClickListener);
			thirdListView.setAdapter(dataAdapters.get(2));
			thirdListView.setTag(dataAdapters.get(2));
		case 2:
			secondListView = new ListView(context);
			secondListView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			secondListView.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
			secondListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			secondListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
			secondListView.setOnItemClickListener(itemClickListener);
			secondListView.setAdapter(dataAdapters.get(1));
			secondListView.setTag(dataAdapters.get(1));
		case 1:
			fristListView = new ListView(context);
			fristListView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			fristListView.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
			fristListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			fristListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
			fristListView.setOnItemClickListener(itemClickListener);
			fristListView.setAdapter(dataAdapters.get(0));
			fristListView.setTag(dataAdapters.get(0));
			break;
		default:
			break;
		}
		
		dataContainer.addView(fristListView);
		if (secondListView != null) {
			leftLineView = new View(context);
			leftLineView.setLayoutParams(new LayoutParams((int)(leftLineWidth * DP), LayoutParams.MATCH_PARENT));
			leftLineView.setBackgroundColor(leftLineColor);
			dataContainer.addView(leftLineView);
			dataContainer.addView(secondListView);
		}
		if (thirdListView != null) {
			rightLineView = new View(context);
			rightLineView.setLayoutParams(new LayoutParams((int)(rightLineWidth * DP), LayoutParams.MATCH_PARENT));
			rightLineView.setBackgroundColor(rightLineColor);
			dataContainer.addView(rightLineView);
			dataContainer.addView(thirdListView);
		}
		rootContainer.addView(topLineView);
		rootContainer.addView(dataContainer);
		
		return rootContainer;
	}
	
	/**
	 * 默认展示方法
	 */
	public void show(){
		builder();
		showAtLocation(((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0), Gravity.BOTTOM, 0, 0);
	}

	/**
	 * 是否自动关闭
	 * @return true自动模式 false手动模式
	 */
	public boolean isAuto() {
		return auto;
	}

	/**
	 * 设置是否自动关闭
	 * @param auto true自动模式 false手动模式
	 */
	public UIDataSelector setAuto(boolean auto) {
		this.auto = auto;
		return this;
	}

	/**
	 * 是否使用关联检查
	 * @return true是 false否
	 */
	public boolean isRelation() {
		return relation;
	}

	/**
	 * 设置是否使用关联检查
	 * @param relation true是 false否
	 */
	public UIDataSelector setRelation(boolean relation) {
		this.relation = relation;
		return this;
	}

	/**
	 * 设置顶线高度
	 * @param topLineHeight
	 */
	public UIDataSelector setTopLineHeight(float topLineHeight) {
		this.topLineHeight = topLineHeight;
		return this;
	}

	/**
	 * 设置左线宽度
	 * @param leftLineHeight
	 */
	public UIDataSelector setLeftLineWidth(float leftLineWidth) {
		this.leftLineWidth = leftLineWidth;
		return this;
	}

	/**
	 * 设置右线宽度
	 * @param rightLineHeight
	 */
	public UIDataSelector setRightLineWidth(float rightLineWidth) {
		this.rightLineWidth = rightLineWidth;
		return this;
	}
	
	/**
	 * 设置内容边距
	 * @param padding
	 */
	public UIDataSelector setPadding(float padding) {
		this.padding = padding;
		return this;
	}

	/**
	 * 设置控件背景颜色
	 * @param backgroundColor
	 */
	public UIDataSelector setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	/**
	 * 设置顶线颜色
	 * @param topLineColor
	 */
	public UIDataSelector setTopLineColor(int topLineColor) {
		this.topLineColor = topLineColor;
		return this;
	}

	/**
	 * 设置左线颜色
	 * @param leftLineColor
	 */
	public UIDataSelector setLeftLineColor(int leftLineColor) {
		this.leftLineColor = leftLineColor;
		return this;
	}

	/**
	 * 设置右线颜色
	 * @param rightLineColor
	 */
	public UIDataSelector setRightLineColor(int rightLineColor) {
		this.rightLineColor = rightLineColor;
		return this;
	}

	/**
	 * 设置数据项默认字体颜色
	 * @param itemTextColor
	 */
	public UIDataSelector setItemTextColor(int itemTextColor) {
		this.itemTextColor = itemTextColor;
		return this;
	}

	/**
	 * 设置数据项被选择字体颜色
	 * @param itemTextSelectColor
	 */
	public UIDataSelector setItemTextSelectColor(int itemTextSelectColor) {
		this.itemTextSelectColor = itemTextSelectColor;
		return this;
	}

	/**
	 * 设置数据项背景颜色
	 * @param itemSelectBackgroundColor
	 */
	public UIDataSelector setItemSelectBackgroundColor(int itemSelectBackgroundColor) {
		this.itemSelectBackgroundColor = itemSelectBackgroundColor;
		return this;
	}

	/**
	 * 设置数据项字体大小
	 * @param itemTextSize
	 */
	public UIDataSelector setItemTextSize(int itemTextSize) {
		this.itemTextSize = itemTextSize;
		return this;
	}

	/**
	 * 设置数据项字体
	 * @param typeface
	 */
	public UIDataSelector setTypeface(Typeface typeface) {
		this.typeface = typeface;
		return this;
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
			DataSelectorAdpater adpater = (DataSelectorAdpater) adapterView.getTag();
			int index = dataAdapters.indexOf(adpater);
			if (relation) {
				if (index != 0 && SELECTED_INDEX[index - 1] == -1) {
					UIToast.show(context, "请从左至右依次选择");
					return;
				}
			}
			List<?> datas = adpater.getDatas();
			if (onItemSelectListener == null) {
				UIToast.show(context, "未设置OnItemClickListener");
			}else {		
				onItemSelectListener.OnItemSelect(selector, position, (String)datas.get(position), datas, index);
			}
			if (auto && index == dataAdapters.size() - 1) {
				dismiss();
			}else {
				SELECTED_INDEX[index] = position;
				adpater.notifyDataSetChanged();
			}
		}
	};
	
	private DataSelectorAdpater getDefaultAdapter(int index){
		return new DataSelectorAdpater(Arrays.asList(new String[]{"无数据"}), index);
	}

	public interface OnItemSelectListener{
		public void OnItemSelect(UIDataSelector selector, int position, String value, List<?> datas, int index);
	}
	
	class DataSelectorAdpater extends UEAdapter{

		private int index;
		
		public DataSelectorAdpater(List<?> datas, int index) {
			super(datas);
			this.index = index;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView;
			if (convertView == null) {
				textView = new TextView(context);
				textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 40 * DP));
				textView.setTextSize(itemTextSize);
				textView.setGravity(Gravity.CENTER);
				if (typeface != null) {
					textView.setTypeface(typeface);
				}
				convertView = textView;
			}else {
				textView = (TextView) convertView;
			}
			if (position == SELECTED_INDEX[index]) {
				textView.setBackgroundColor(itemSelectBackgroundColor);
				textView.setTextColor(itemTextSelectColor);
			}else {
				textView.setBackgroundColor(Color.TRANSPARENT);
				textView.setTextColor(itemTextColor);
			}
			textView.setText((CharSequence) getItem(position));
			return convertView;
		}
		
	}
	
}
