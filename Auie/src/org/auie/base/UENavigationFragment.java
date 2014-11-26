package org.auie.base;

import android.view.View.OnClickListener;

public abstract class UENavigationFragment extends UEFragment {
	
	private UENavigationFragmentActivity parentActivity;
	private boolean checked = false;
	
	@Override
	protected void initializePrepare() {
		super.initializePrepare();
		try {
			parentActivity = (UENavigationFragmentActivity) getActivity();
			checked = true;
		} catch (Exception e) {
			checked = false;
		}
	}
	
	protected void setLeftImageResource(int resId){
		if (checked) {
			parentActivity.setLeftImageResource(resId);
		}
	}
	
	protected void setLeftText(String text){
		if (checked) {
			parentActivity.setLeftText(text);
		}
	}
	
	protected void setTitle(String title){
		if (checked) {
			parentActivity.setTitle(title);
		}
	}
	
	public void setLeftImageOnClickListener(OnClickListener mListener){
		if (checked) {
			parentActivity.setLeftImageOnClickListener(mListener);
		}
	}
	
	public void setLeftTextOnClickListener(OnClickListener mListener){
		if (checked) {
			parentActivity.setLeftTextOnClickListener(mListener);
		}
	}
	
	public void setLeftOnClickListener(OnClickListener mListener){
		if (checked) {
			parentActivity.setLeftOnClickListener(mListener);
		}
	}
	
	public void setRightOnClickListener(OnClickListener mListener){
		if (checked) {
			parentActivity.setRightOnClickListener(mListener);
		}
	}
	
	public void setRightImageResource(int resId) {
		if (checked) {
			parentActivity.setRightImageResource(resId);
		}
	}
	
	public void setRightText(String text){
		if (checked) {
			parentActivity.setRightText(text);
		}
	}
	
	public void setStatusBarBackgroundColor(int statusBarBackgroundColor) {
		if (checked) {
			parentActivity.setStatusBarBackgroundColor(statusBarBackgroundColor);
		}
	}

	public void setNavigationBarBackgroundColor(int navigationBarBackgroundColor) {
		if (checked) {
			parentActivity.setNavigationBarBackgroundColor(navigationBarBackgroundColor);
		}
	}
	
	public void setBackgroundColor(int backgroundColor) {
		if (checked) {
			parentActivity.setBackgroundColor(backgroundColor);
		}
	}
	
	public void setLineBackgroundColor(int lineBackgroundColor) {
		if (checked) {
			parentActivity.setLineBackgroundColor(lineBackgroundColor);
		}
	}
	
	public void setStatusType(int statusType) {
		if (checked) {
			parentActivity.setStatusType(statusType);
		}
	}

	public void setNavigationTextColor(int navigationTextColor) {
		if (checked) {
			parentActivity.setNavigationTextColor(navigationTextColor);
		}
	}

	public void setTitleColor(int titleColor) {
		if (checked) {
			parentActivity.setTitleColor(titleColor);
		}
	}
	
	public void showNavigationBar(){
		showStatusBar();
		showActionBar();
	}
	
	public void hideNavigationBar(){
		hideStatusBar();
		hideActionBar();
	}
	
	public void hideStatusBar(){
		if (checked) {
			parentActivity.hideStatusBar();
		}
	}
	
	public void hideActionBar(){
		if (checked) {
			parentActivity.hideActionBar();
		}
	}
	
	public void showStatusBar(){
		if (checked) {
			parentActivity.showStatusBar();
		}
	}
	
	public void showActionBar(){
		if (checked) {
			parentActivity.showActionBar();
		}
	}
	
	public void addNotice(String content){
		if (checked) {
			parentActivity.addNotice(content);
		}
	}
	
	public void addNotice(String content, long time){
		if (checked) {
			parentActivity.addNotice(content, time);
		}
	}

	public void clearNotice(){
		if (checked) {
			parentActivity.clearNotice();
		}
	}
}
