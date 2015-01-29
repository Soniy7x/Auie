package org.auie.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.auie.ui.UIIndexBar.OnUIIndexItemOnTouchListener;
import org.auie.utils.UEException.UEIndexNotFoundException;
import org.auie.utils.UEMethod;
import org.auie.utils.UEPinyin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

@SuppressLint("NewApi")
public class UILetterIndexView extends RelativeLayout {
	
	private ListView mListView;
	private TextView mTextView;
	private UIIndexBar mIndexBar;
	
	private UILetterIndexAdapter adapter;
	private List<String> indexDatas;
	private List<ItemModel> models;
	private OnItemClickListener onItemClickListener;
	private PinyinComparator comparator = new PinyinComparator();
	
	public UILetterIndexView(Context context) {
		super(context);
		createView();
	}

	public UILetterIndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		createView();
	}

	public UILetterIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		createView();
	}

	public UILetterIndexView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		createView();
	}

	private void createView(){
		
		LayoutParams params1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params1.addRule(BELOW, 27);
		mListView = new ListView(getContext());
		mListView.setFadingEdgeLength(0);
		mListView.setDivider(null);
		mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setLayoutParams(params1);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(parent, view, position, id);
				}
			}
		});
		
		int textViewSize = UEMethod.dp2px(getContext(), 80);
		LayoutParams params = new LayoutParams(textViewSize, textViewSize);
		params.addRule(CENTER_IN_PARENT, TRUE);
		mTextView = new TextView(getContext());
		mTextView.setLayoutParams(params);
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextSize(30);
		mTextView.setTextColor(Color.WHITE);
		mTextView.setBackground(createBackground(Color.BLACK, 200));
		mTextView.setVisibility(INVISIBLE);
		
		LayoutParams params2 = new LayoutParams(UEMethod.dp2px(getContext(), 30), LayoutParams.MATCH_PARENT);
		params2.addRule(ALIGN_PARENT_RIGHT, TRUE);
		params2.addRule(BELOW, 27);
		mIndexBar = new UIIndexBar(getContext());
		mIndexBar.setLayoutParams(params2);
		mIndexBar.setTextView(mTextView);
		mIndexBar.setItemOnTouchListener(new OnUIIndexItemOnTouchListener() {
			
			@Override
			public void onIndexItemChanged(String item) {
				int position = adapter.getPositionForSection(item.charAt(0));
				if(position != -1){
					mListView.setSelection(position);
				}
			}
		});
		
		addView(mListView);
		addView(mTextView);
		addView(mIndexBar);
		
	}
	
	private ShapeDrawable createBackground(int color, int alpha){
		int radius = UEMethod.dp2px(getContext(), 8);
		float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setAlpha(alpha);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        return shapeDrawable;
	}

	public void setOnItemClickListener(OnItemClickListener listener){
		this.onItemClickListener = listener;
	}
	
	public List<String> getIndexDatas() {
		return indexDatas;
	}
	
	public List<ItemModel> getModelDatas() {
		return models;
	}

	public List<Model> itemModelToModel(List<String> indexDatas){
		List<Model> models = new ArrayList<>();
		for (String data : indexDatas) {
			models.add(new Model(data, data));
		}
		return models;
	}
	
	public void setAdapter(String[] indexDatas) throws UEIndexNotFoundException{
		setAdapter(java.util.Arrays.asList(indexDatas));
	}
	
	public void setAdapter(List<String> indexDatas) throws UEIndexNotFoundException{
		if (indexDatas == null || indexDatas.size() < 1) {
			throw new UEIndexNotFoundException("this index datas is null or size < 1");
		}
		this.adapter =  new UILetterIndexDefaultAdapter(itemModelToModel(indexDatas));
		this.models = adapter.getDatas();
		this.mListView.setAdapter(adapter);
	}
	
	public void setAdapter(UILetterIndexAdapter adapter) throws UEIndexNotFoundException{
		this.adapter = adapter;
		this.models = adapter.getDatas();
		this.mListView.setAdapter(adapter);
	}
	
	private List<ItemModel> transfromDatas(List<Model> models) throws UEIndexNotFoundException{
		List<ItemModel> mItemModels = new ArrayList<>();
		if (models == null || models.size() < 1) {
			throw new UEIndexNotFoundException("this index datas is null or size < 1");
		}
		for (Model model : models) {
			ItemModel mItemModel = new ItemModel();
			mItemModel.content = model.content;
			mItemModel.indexName = model.index;
			mItemModel.letters = UEPinyin.transformPinYin(mItemModel.indexName);
			if (model.extra) {
				mItemModel.index = "*";
			}else {
				String pinyin = mItemModel.letters.substring(0, 1);
				if (pinyin.matches("[A-Z]")) {
					mItemModel.index = pinyin;
				}else {
					mItemModel.index = "#";
				}
			}
			mItemModels.add(mItemModel);
		}
		return mItemModels;
	}
	
	public class ItemModel extends Model{
		public String index;
		public String indexName;
		public String letters;
		public Object content;
	}
	
	public static class Model{
		public String index;
		public boolean extra;
		public Object content;
		
		public Model(){}
		
		public Model(String index, Object content){
			this(index, content, false);
		}
		
		public Model(String index, Object content, boolean extra){
			this.index = index;
			this.extra = extra;
			this.content = content;
		}
	}
	
	class PinyinComparator implements Comparator<ItemModel> {

		public int compare(ItemModel o1, ItemModel o2) {
			if (o1.index.equals("*")) {
				return -1;
			} else if (o1.index.equals("#")) {
				return 1;
			} else {
				return o1.letters.compareTo(o2.letters);
			}
		}
	}
	
	class UILetterIndexDefaultAdapter extends UILetterIndexAdapter{

		private final int D05 = UEMethod.dp2px(getContext(), 5);
		private final int D10 = UEMethod.dp2px(getContext(), 10);
		
		public UILetterIndexDefaultAdapter(List<Model> models) throws UEIndexNotFoundException {
			super(models);
		}

		@Override
		public View getContentView(int position, View contentView, ViewGroup parent) {
			TextView content;
			if (contentView == null) {
				content = new TextView(getContext());
				content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
				content.setPadding(D10 + D05, D10, 0, D10);
				content.setGravity(Gravity.CENTER_VERTICAL);
				content.setTextColor(Color.parseColor("#666666"));
				contentView = content;
			}else {
				content = (TextView)contentView;
			}
			content.setText(getItem(position).toString());
			return contentView;
		}
		
	}
	
	public abstract class UILetterIndexAdapter extends BaseAdapter implements SectionIndexer{
		
		private final int D05 = UEMethod.dp2px(getContext(), 5);
		
		private List<ItemModel> datas;
		
		public UILetterIndexAdapter(List<Model> models) throws UEIndexNotFoundException{
			this.datas = transfromDatas(models);
			Collections.sort(this.datas, comparator);
		}
		
		public void updateListView(List<ItemModel> models){
			this.datas = models;
			notifyDataSetChanged();
		}
		
		public void updateListData(List<Model> models) throws UEIndexNotFoundException{
			this.datas = transfromDatas(models);
			Collections.sort(this.datas, comparator);
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			View contentView = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LinearLayout mContainer = new LinearLayout(getContext());
				mContainer.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
				mContainer.setGravity(Gravity.CENTER_VERTICAL);
				mContainer.setOrientation(LinearLayout.VERTICAL);
				TextView index = new TextView(getContext());
				index.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
				index.setPadding(D05, D05, 0, D05);
				index.setBackgroundColor(Color.parseColor("#E8E8E8"));
				index.setTextColor(Color.parseColor("#888888"));
				View view = new View(getContext());
				view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UEMethod.dp2px(getContext(), 0.5f)));
				view.setBackgroundColor(Color.parseColor("#D8D8D8"));
				view.setTag(viewHolder);
				contentView = getContentView(position, contentView, parent);
				mContainer.addView(index);
				mContainer.addView(view);
				if (contentView != null) {
					mContainer.addView(contentView);
				}
				viewHolder.index = index;
				viewHolder.line = view;
				convertView = mContainer;
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
				getContentView(position, ((LinearLayout)convertView).getChildAt(2), parent);
			}
			ItemModel model = datas.get(position);
			int section = getSectionForPosition(position);
			if (position == getPositionForSection(section) && !model.index.equals("*")) {
				viewHolder.index.setVisibility(VISIBLE);
				viewHolder.index.setText(model.index);
				viewHolder.line.setVisibility(GONE);
			}else {
				viewHolder.line.setVisibility(VISIBLE);
				viewHolder.index.setVisibility(GONE);
			}
			return convertView;
		}
		
		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position).content;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public Object[] getSections() {
			return null;
		}

		@Override
		public int getPositionForSection(int sectionIndex) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = datas.get(i).index;
				char firstChar = sortStr.toUpperCase(Locale.getDefault()).charAt(0);
				if (firstChar == sectionIndex) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return datas.get(position).index.charAt(0);
		}
		
		public List<ItemModel> getDatas() {
			return datas;
		}

		public void setDatas(List<ItemModel> datas) {
			this.datas = datas;
			notifyDataSetChanged();
		}

		public abstract View getContentView(int position, View contentView, ViewGroup parent);
		
		final class ViewHolder {
			TextView index;
			View line;
		}
	}
}
