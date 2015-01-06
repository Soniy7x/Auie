package org.auie.ui;

import org.auie.utils.UEMethod;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class UIAlertnateTextDialog extends PopupWindow{

	private final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
	
	private Context context;
	private onInputCompletedListener listener;
	
	private int backgroundColor = Color.parseColor("#FFE4E4E4");
	private int topLineColor = Color.parseColor("#33444444");
	private int middleLineColor = Color.parseColor("#33666666");
	private int titleColor = Color.parseColor("#444444");
	private int contentColor = Color.parseColor("#444444");
	private int contentHintColor = Color.parseColor("#55444444");
	private int submitButtonColor = Color.parseColor("#444444");
	private int cancelButtonColor = Color.parseColor("#444444");
	private int titleSize = 16;
	private int buttonSize = 14;
	private int contentSize = 16;
	private String title = "";
	private String defaultContent = "";
	private String contentHint = "";
	private boolean allowNull = true;
	
	public UIAlertnateTextDialog(Context context, onInputCompletedListener listener){
		this.context = context;
		this.listener = listener;
	}
	
	@SuppressWarnings("deprecation")
	private UIAlertnateTextDialog builder(){
		setBackgroundDrawable(new BitmapDrawable());
		setContentView(createContentView());
		setWidth(MATCH_PARENT);
		setHeight(UEMethod.dp2px(context, 200));
		setFocusable(true);
		return this;
	}
	
	private View createContentView(){
		
		ScrollView scrollView = new ScrollView(context);
		scrollView.setBackgroundColor(backgroundColor);
		
		LinearLayout rootLayout = new LinearLayout(context);
		rootLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
		rootLayout.setBackgroundColor(backgroundColor);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		
		View topLineView = new View(context);
		topLineView.setLayoutParams(new LayoutParams(MATCH_PARENT, UEMethod.dp2px(context, 0.5f)));
		topLineView.setBackgroundColor(topLineColor);
		
		TextView titleTextView = new TextView(context);
		titleTextView.setLayoutParams(new LayoutParams(MATCH_PARENT, UEMethod.dp2px(context, 36f)));
		titleTextView.setText(title);
		titleTextView.setTextSize(titleSize);
		titleTextView.setTextColor(titleColor);
		titleTextView.setPadding(UEMethod.dp2px(context, 10), 0, 0, 0);
		titleTextView.setSingleLine(true);
		titleTextView.setGravity(Gravity.CENTER_VERTICAL);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, UEMethod.dp2px(context, 48f));
		params.setMargins(UEMethod.dp2px(context, 26), UEMethod.dp2px(context, 24), UEMethod.dp2px(context, 26), 0);
		final EditText editText = new EditText(context);
		editText.setLayoutParams(params);
		editText.setTextSize(contentSize);
		editText.setTextColor(contentColor);
		editText.setHint(contentHint);
		editText.setText(defaultContent);
		editText.setHintTextColor(contentHintColor);
		editText.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
		editText.setBackgroundColor(Color.TRANSPARENT);
		editText.setImeOptions(EditorInfo.IME_ACTION_GO);
		editText.setSingleLine(true);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		editText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					if (!allowNull && editText.getText().toString().trim().length() < 1) {
						UIToast.show(context, "未填写内容");
						return false;
					}
					listener.onInputCompleted(editText.getText().toString().trim());
					dismiss();
				}
				return false;
			}
		});
		
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(MATCH_PARENT, UEMethod.dp2px(context, 1f));
		params2.setMargins(UEMethod.dp2px(context, 36), 0, UEMethod.dp2px(context, 36), 0);
		View middleLineView = new View(context);
		middleLineView.setLayoutParams(params2);
		middleLineView.setBackgroundColor(middleLineColor);
		
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(MATCH_PARENT, UEMethod.dp2px(context, 80f));
		params3.setMargins(0, UEMethod.dp2px(context, 10), 0, 0);
		LinearLayout buttonLayout = new LinearLayout(context);
		buttonLayout.setLayoutParams(params3);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(0, MATCH_PARENT, 1);
		UIButton submitButton = new UIButton(context);
		submitButton.setBackgroundColor(backgroundColor);
		submitButton.setLayoutParams(params4);
		submitButton.setTextColor(submitButtonColor);
		submitButton.setTextSize(buttonSize);
		submitButton.setText("确定");
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!allowNull && editText.getText().toString().trim().length() < 1) {
					UIToast.show(context, "未填写内容");
					return;
				}
				listener.onInputCompleted(editText.getText().toString().trim());
				dismiss();
			}
		});
		UIButton cancelButton = new UIButton(context);
		cancelButton.setLayoutParams(params4);
		cancelButton.setBackgroundColor(backgroundColor);
		cancelButton.setTextColor(cancelButtonColor);
		cancelButton.setTextSize(buttonSize);
		cancelButton.setText("取消");
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		buttonLayout.addView(cancelButton);
		buttonLayout.addView(submitButton);
		
		rootLayout.addView(topLineView);
		rootLayout.addView(titleTextView);
		rootLayout.addView(editText);
		rootLayout.addView(middleLineView);
		rootLayout.addView(buttonLayout);
		
		scrollView.addView(rootLayout);
		
		return scrollView;
	}
	
	public void show(){
		TranslateAnimation animation = new TranslateAnimation(0, 0, UEMethod.dp2px(context, 200), 0);
		animation.setDuration(280);
		builder().showAtLocation(((ViewGroup)(((Activity) context).findViewById(android.R.id.content))).getChildAt(0), Gravity.BOTTOM, 0, 0);
		getContentView().startAnimation(animation);
	}
	
	public interface onInputCompletedListener{
		public void onInputCompleted(CharSequence content);
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getTopLineColor() {
		return topLineColor;
	}

	public void setTopLineColor(int topLineColor) {
		this.topLineColor = topLineColor;
	}

	public int getMiddleLineColor() {
		return middleLineColor;
	}

	public void setMiddleLineColor(int middleLineColor) {
		this.middleLineColor = middleLineColor;
	}

	public int getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
	}

	public int getContentColor() {
		return contentColor;
	}

	public void setContentColor(int contentColor) {
		this.contentColor = contentColor;
	}

	public int getContentHintColor() {
		return contentHintColor;
	}

	public void setContentHintColor(int contentHintColor) {
		this.contentHintColor = contentHintColor;
	}

	public int getSubmitButtonColor() {
		return submitButtonColor;
	}

	public void setSubmitButtonColor(int submitButtonColor) {
		this.submitButtonColor = submitButtonColor;
	}

	public int getCancelButtonColor() {
		return cancelButtonColor;
	}

	public void setCancelButtonColor(int cancelButtonColor) {
		this.cancelButtonColor = cancelButtonColor;
	}

	public int getTitleSize() {
		return titleSize;
	}

	public void setTitleSize(int titleSize) {
		this.titleSize = titleSize;
	}

	public int getButtonSize() {
		return buttonSize;
	}

	public void setButtonSize(int buttonSize) {
		this.buttonSize = buttonSize;
	}

	public int getContentSize() {
		return contentSize;
	}

	public void setContentSize(int contentSize) {
		this.contentSize = contentSize;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContentHint() {
		return contentHint;
	}

	public void setContentHint(String contentHint) {
		this.contentHint = contentHint;
	}

	public String getDefaultContent() {
		return defaultContent;
	}

	public void setDefaultContent(String defaultContent) {
		this.defaultContent = defaultContent;
	}

	public boolean isAllowNull() {
		return allowNull;
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}
	
}
