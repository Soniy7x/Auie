package org.auie.ui;

import java.util.ArrayList;
import java.util.List;

import org.auie.image.UEImage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.L)
public class UI2048GameView extends GridLayout {
	
	private static final int MOVE_DISTANCE = 5;
	private static final int DEFAULT_BACKGROUND = 0xFFbbada0;
	
	private List<Point> emptyPoints = new ArrayList<>();
	private Card[][] cardMaps = new Card[4][4];
	private OnUIGame2048Listener onGameListener;
	private int cardSize = 0;
	private int totalScore = 0;
	private int maxCardValue = 0;
	private int cardDistance = 10;
	private int textSize = 20;
	private boolean merge = false;
	private boolean gameover = false;
	
	public UI2048GameView(Context context) {
		super(context);
		init();
	}

	public UI2048GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UI2048GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public UI2048GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private void init(){
		setColumnCount(4);
		try{
			setBackground(UEImage.createBackground(((ColorDrawable) getBackground()).getColor(), 5));			
		}catch(Exception e){			
			setBackground(UEImage.createBackground(DEFAULT_BACKGROUND, 5));
		}
		setOnTouchListener(onTouchListener);
		getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				int w = getWidth();
				int h = getHeight();
				ViewGroup.LayoutParams params = getLayoutParams();
				params.height = Math.min(w, h);
				params.width  = Math.min(w, h);
				setLayoutParams(params);
				cardSize = (Math.min(w, h) - cardDistance)/4;
				initCards();
				startGame();
				getViewTreeObserver().removeOnPreDrawListener(this);
				return false;
			}
		});
	}
	
	public void restartGame(){
		if (cardMaps[2][2] != null) {
			startGame();
		}else {
			UIToast.show(getContext(), "游戏界面尚未初始化!");
		}
	}

	private void startGame(){
		totalScore = 0;
		maxCardValue = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				cardMaps[x][y].setNum(0);
			}
		}
		if (onGameListener != null) {
			onGameListener.onGameStart();
		}
		addRandomCard();
		addRandomCard();
	}
	
	private void addRandomCard(){
		emptyPoints.clear();
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (cardMaps[x][y].getNum() == 0) {
					emptyPoints.add(new Point(x, y));
				}
			}
		}
		Point point = emptyPoints.remove((int)(Math.random() * emptyPoints.size()));
		AlphaAnimation animation = new AlphaAnimation(0, 1f);
		animation.setDuration(500);
		cardMaps[point.x][point.y].startAnimation(animation);
		cardMaps[point.x][point.y].setNum(Math.random() > 0.1 ? 2 : 4);
	}
	
	private void checkGameOver(){
		gameover = true;
		ALL:
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (cardMaps[x][y].getNum() <= 0 
						|| (x > 0 && cardMaps[x][y].equals(cardMaps[x - 1][y]))
						|| (x < 3 && cardMaps[x][y].equals(cardMaps[x + 1][y]))
						|| (y > 0 && cardMaps[x][y].equals(cardMaps[x][y - 1]))
						|| (y < 3 && cardMaps[x][y].equals(cardMaps[x][y + 1]))) {
					gameover = false;
					break ALL;
				}
			}
		}
		if (gameover && onGameListener != null) {
			onGameListener.onGameOver();
		}
	}
	
	private void initCards(){
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				Card card = new Card(getContext());
				card.setNum(0);
				cardMaps[x][y] = card;
				addView(card, cardSize, cardSize);
			}
		}
	}
	
	private void moveLeft(){
		merge = false;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				for (int x1 = x+1; x1 < 4; x1++) {
					if (cardMaps[x1][y].getNum() > 0) {
						if (cardMaps[x][y].getNum() <= 0) {
							cardMaps[x][y].startAnimation(moveAnimationX(x1 - x));
							cardMaps[x][y].setNum(cardMaps[x1][y].getNum());
							cardMaps[x1][y].setNum(0);
							x--;
							merge = true;
						}else if (cardMaps[x][y].equals(cardMaps[x1][y])) {
							cardMaps[x][y].startAnimation(moveAnimationX(x1 - x));
							cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
							cardMaps[x1][y].setNum(0);
							totalScore += cardMaps[x][y].getNum();
							maxCardValue = Math.max(maxCardValue, cardMaps[x][y].getNum());
							if (onGameListener != null) {
								onGameListener.onGameScoreChanged(totalScore, maxCardValue);
							}
							merge = true;
						}
						break;
					}
				}
			}
		}
		if (merge) {
			addRandomCard();
			checkGameOver();
		}
	}
	
	private void moveRight(){
		merge = false;
		for (int y = 0; y < 4; y++) {
			for (int x = 3; x >= 0; x--) {
				for (int x1 = x-1; x1 >=0; x1--) {
					if (cardMaps[x1][y].getNum() > 0) {
						if (cardMaps[x][y].getNum() <= 0) {
							cardMaps[x][y].startAnimation(moveAnimationX(x1 - x));
							cardMaps[x][y].setNum(cardMaps[x1][y].getNum());
							cardMaps[x1][y].setNum(0);
							x++;
							merge = true;
						}else if (cardMaps[x][y].equals(cardMaps[x1][y])) {
							cardMaps[x][y].startAnimation(moveAnimationX(x1 - x));
							cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
							cardMaps[x1][y].setNum(0);
							totalScore += cardMaps[x][y].getNum();
							maxCardValue = Math.max(maxCardValue, cardMaps[x][y].getNum());
							if (onGameListener != null) {
								onGameListener.onGameScoreChanged(totalScore, maxCardValue);
							}
							merge = true;
						}
						break;
					}
				}
			}
		}
		if (merge) {
			addRandomCard();
			checkGameOver();
		}
	}
	
	private void moveUp(){
		merge = false;
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				for (int y1 = y+1; y1 < 4; y1++) {
					if (cardMaps[x][y1].getNum() > 0) {
						if (cardMaps[x][y].getNum() <= 0) {
							cardMaps[x][y].startAnimation(moveAnimationY(y1 - y));
							cardMaps[x][y].setNum(cardMaps[x][y1].getNum());
							cardMaps[x][y1].setNum(0);
							y--;
							merge = true;
						}else if (cardMaps[x][y].equals(cardMaps[x][y1])) {;
							cardMaps[x][y].startAnimation(moveAnimationY(y1 - y));
							cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
							cardMaps[x][y1].setNum(0);
							totalScore += cardMaps[x][y].getNum();
							maxCardValue = Math.max(maxCardValue, cardMaps[x][y].getNum());
							if (onGameListener != null) {
								onGameListener.onGameScoreChanged(totalScore, maxCardValue);
							}
							merge = true;
						}
						break;
					}
				}
			}
		}
		if (merge) {
			addRandomCard();
			checkGameOver();
		}
	}

	private void moveDown(){
		merge = false;
		for (int x = 0; x < 4; x++) {
			for (int y = 3; y >= 0; y--) {
				for (int y1 = y-1; y1 >= 0; y1--) {
					if (cardMaps[x][y1].getNum() > 0) {
						if (cardMaps[x][y].getNum() <= 0) {
							cardMaps[x][y].startAnimation(moveAnimationY(y1 - y));
							cardMaps[x][y].setNum(cardMaps[x][y1].getNum());
							cardMaps[x][y1].setNum(0);
							y++;
							merge = true;
						}else if (cardMaps[x][y].equals(cardMaps[x][y1])) {
							cardMaps[x][y].startAnimation(moveAnimationY(y1 - y));
							cardMaps[x][y].setNum(cardMaps[x][y].getNum() * 2);
							cardMaps[x][y1].setNum(0);
							totalScore += cardMaps[x][y].getNum();
							maxCardValue = Math.max(maxCardValue, cardMaps[x][y].getNum());
							if (onGameListener != null) {
								onGameListener.onGameScoreChanged(totalScore, maxCardValue);
							}
							merge = true;
						}
						break;
					}
				}
			}
		}
		if (merge) {
			addRandomCard();
			checkGameOver();
		}
	}
	
	private TranslateAnimation moveAnimationY(int overY){
		TranslateAnimation animation = new TranslateAnimation(0, 0, cardSize * overY, 0);
		animation.setDuration(120 * Math.abs(overY));
		return animation;
	}
	
	private TranslateAnimation moveAnimationX(int overX){
		TranslateAnimation animation = new TranslateAnimation(cardSize * overX, 0, 0, 0);
		animation.setDuration(120 * Math.abs(overX));
		return animation;
	}
	
	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		private float startX;
		private float startY;
		private float offsetX;
		private float offsetY;
		
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				offsetX = event.getX() - startX;
				offsetY = event.getY() - startY;
				if (Math.abs(offsetX) > Math.abs(offsetY)) {
					if (offsetX < -MOVE_DISTANCE) {
						moveLeft();
					}else if (offsetX > MOVE_DISTANCE) {
						moveRight();
					}
				}else {
					if (offsetY < -MOVE_DISTANCE) {
						moveUp();
					}else if (offsetY > MOVE_DISTANCE) {
						moveDown();
					}
				}
				break;
			}
			return true;
		}
	};
	
	public void setOnGameListener(OnUIGame2048Listener onGameListener) {
		this.onGameListener = onGameListener;
	}

	public void setCardDistance(int cardDistance) {
		this.cardDistance = cardDistance;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public interface OnUIGame2048Listener{
		public void onGameStart();
		public void onGameOver();
		public void onGameScoreChanged(int totalScore, int maxCardValue);
	}
	
	class Card extends RelativeLayout{

		private int num = 0;
		private TextView mTextView;
		
		public Card(Context context) {
			super(context);
			LayoutParams params = new LayoutParams(-1, -1);
			params.setMargins(cardDistance, cardDistance, 0, 0);
			mTextView = new TextView(getContext());
			mTextView.setLayoutParams(params);
			mTextView.setGravity(Gravity.CENTER);
			mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
			addView(mTextView);
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
			if (num <= 0) {
				mTextView.setText("");
			}else {
				mTextView.setText(String.valueOf(num));
			}
			mTextView.setTextColor(0xCCffffff);
			switch (num) {
			case 0:
				mTextView.setBackground(UEImage.createBackground(0xFFFFFF, 80, 8));
				break;
			case 2:
				mTextView.setTextColor(0xFF888888);
				mTextView.setBackground(UEImage.createBackground(0xe5dcd2, 8));
				break;
			case 4:
				mTextView.setTextColor(0xFF888888);
				mTextView.setBackground(UEImage.createBackground(0xede0c8, 8));
				break;
			case 8:
				mTextView.setBackground(UEImage.createBackground(0xf2b179, 8));
				break;	
			case 16:
				mTextView.setBackground(UEImage.createBackground(0xf59563, 8));
				break;
			case 32:
				mTextView.setBackground(UEImage.createBackground(0xf67c5f, 8));
				break;
			case 64:
				mTextView.setBackground(UEImage.createBackground(0xf65e3b, 8));
				break;
			case 128:
				mTextView.setBackground(UEImage.createBackground(0xedcf72, 8));
				break;
			case 256:
				mTextView.setBackground(UEImage.createBackground(0xedcc61, 8));
				break;
			case 512:
				mTextView.setBackground(UEImage.createBackground(0xedc850, 8));
				break;
			case 1024:
				mTextView.setBackground(UEImage.createBackground(0xedc53f, 8));
				break;
			case 2048:
				mTextView.setBackground(UEImage.createBackground(0xedc22e, 8));
				break;
			case 4096:
				mTextView.setBackground(UEImage.createBackground(0x3c3a32, 8));
				break;
			case 8192:
				mTextView.setBackground(UEImage.createBackground(0xff0000, 8));
				break;
			case 16384:
				mTextView.setBackground(UEImage.createBackground(0x009efc, 8));
				break;
			case 32768:
				mTextView.setBackground(UEImage.createBackground(0x000000, 8));
				break;
			default:
				mTextView.setTextColor(0x000000);
				mTextView.setBackground(UEImage.createBackground(0xFFFFFF, 8));
				break;
			}
		}
		
		public boolean equals(Card card){
			return card.getNum() == num;
		}
	}
}
