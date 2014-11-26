package org.auie.ui;

import org.auie.utils.UEMethod;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

@TargetApi(Build.VERSION_CODES.L)
public class UIHumanEdittText extends LinearLayout {
	
	private UIEditText mEditText;
	private UIButton mButton;
	
	private int padding  = UEMethod.dp2px(getContext(), 6);
	private int buttonColor = Color.parseColor("#FA6E86");
	private int buttonTextColor = Color.parseColor("#FFFFFF");
	private int strokeColor = Color.parseColor("#A8A8A8");
	private int textColor = Color.parseColor("#666666");
	private int textHintColor = Color.parseColor("#888888");
	private int buttonTextSize = 12;
	private int textSize = 14;
	private float strokeWidth = 0.8f;
	private String buttonText = "取消";
	private String hintText = "请输入检索内容";
	
	private InputMethodManager imm;
	private OnUIHumanEditTextInputCompleteListener inputCompleteListener;
	private TextWatcher textWatcher;
	
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mEditText.clearFocus();
			mEditText.getText().clear();
			if (imm != null) {
				imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			}
		}
	};
	
	public UIHumanEdittText(Context context) {
		super(context);
		createView();
	}

	public UIHumanEdittText(Context context, AttributeSet attrs) {
		super(context, attrs);
		createView();
	}

	public UIHumanEdittText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		createView();
	}

	public UIHumanEdittText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		createView();
	}

	private void createView(){
		
		imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, UEMethod.dp2px(getContext(), 48)));
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		setPadding(padding * 2 , padding, padding * 2, padding);
		setFocusable(true);
		setFocusableInTouchMode(true);
		
		mEditText = new UIEditText(getContext());
		mEditText.setHint(hintText);
		mEditText.setType(UIEditText.TYPE_CIRCLE);
		mEditText.setTextSize(textSize);
		mEditText.setTextColor(textColor);
		mEditText.setGravity(Gravity.CENTER);
		mEditText.setStrokeColor(strokeColor);
		mEditText.setHintTextColor(textHintColor);
		mEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mButton.setVisibility(VISIBLE);
				}else {
					mButton.setVisibility(GONE);
				}
			}
		});
		mEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					if (inputCompleteListener != null) {						
						inputCompleteListener.onInputComplete(mEditText.getText().toString());
					}
					mEditText.clearFocus();
					imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					if (inputCompleteListener != null) {						
						inputCompleteListener.onInputComplete(mEditText.getText().toString());
					}
					mEditText.clearFocus();
					imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
		mEditText.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				int size = getHeight() - padding * 2;
				if (size <  40) {
					size = 40;
				}
				LayoutParams params = new LayoutParams(0, size, 1);
				mEditText.setLayoutParams(params);
				mEditText.getViewTreeObserver().removeOnPreDrawListener(this);
				return false;
			}
		});
		if (textWatcher != null) {
			mEditText.addTextChangedListener(textWatcher);
		}
		
		mButton = new UIButton(getContext());
		mButton.setBackgroundColor(buttonColor);
		mButton.setTextColor(buttonTextColor);
		mButton.setTextSize(buttonTextSize);
		mButton.setOnClickListener(onClickListener);
		mButton.setText(buttonText);
		mButton.setVisibility(GONE);
		mButton.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				int size = getHeight() - padding * 2 - 4;
				LayoutParams params = new LayoutParams((int) (size * 1.6), size);
				params.setMargins(padding * 2, 0, 0, 0);
				mButton.setLayoutParams(params);
				mButton.getViewTreeObserver().removeOnPreDrawListener(this);
				return false;
			}
		});
		
		addView(mEditText);
		addView(mButton);
	}
	
	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getButtonColor() {
		return buttonColor;
	}

	public void setButtonColor(int buttonColor) {
		this.buttonColor = buttonColor;
	}

	public int getButtonTextColor() {
		return buttonTextColor;
	}

	public void setButtonTextColor(int buttonTextColor) {
		this.buttonTextColor = buttonTextColor;
	}

	public int getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getTextHintColor() {
		return textHintColor;
	}

	public void setTextHintColor(int textHintColor) {
		this.textHintColor = textHintColor;
	}

	public int getButtonTextSize() {
		return buttonTextSize;
	}

	public void setButtonTextSize(int buttonTextSize) {
		this.buttonTextSize = buttonTextSize;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public String getHintText() {
		return hintText;
	}

	public void setHintText(String hintText) {
		this.hintText = hintText;
	}

	public void setOnInputCompleteListener( OnUIHumanEditTextInputCompleteListener inputCompleteListener) {
		this.inputCompleteListener = inputCompleteListener;
	}

	public void addTextChangedListener(TextWatcher textWatcher) {
		this.textWatcher = textWatcher;
		mEditText.addTextChangedListener(textWatcher);
	}

	public interface OnUIHumanEditTextInputCompleteListener{
		void onInputComplete(String text);
	}
}
