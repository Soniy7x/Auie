package org.auie.utils;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;

public class UEFontAwesome {

	private static UEFontAwesome instance;
	private static Typeface typeface;
	private static Context context;
	
	private UEFontAwesome(Context context){
		UEFontAwesome.context = context;
	}
	
	public static UEFontAwesome getInstance(Context context){
		if (instance == null) {
			instance = new UEFontAwesome(context);
			typeface = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome.ttf");
		}
		return instance;
	}
	
	public void setText(View view, String text){
		try {
			view.getClass().getMethod("setTypeface", Typeface.class).invoke(view, typeface);
			view.getClass().getMethod("setText", CharSequence.class).invoke(view, Html.fromHtml(text));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			Log.w(UE.TAG, "此view不存在setText或setTypeface方法");
		}
	}
	
	public void setDrawable(View view, String icon){
		try {
			view.getClass().getMethod("setImageDrawable", Drawable.class).invoke(view, new UEFontAwesomeDrawable(context, view, icon));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			Log.w(UE.TAG, "此view不存在setImageDrawable方法");
		}
	}
	
	public void setDrawable(View view, String icon, int color){
		try {
			view.getClass().getMethod("setImageDrawable", Drawable.class).invoke(view, new UEFontAwesomeDrawable(context, view, icon));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			Log.w(UE.TAG, "此view不存在setImageDrawable方法");
		}
	}
	
	class UEFontAwesomeDrawable extends Drawable{

		private FontMetricsInt mFontMetrics;
		private TextPaint mTextPaint;
		private String text;
		private int width;
		private int height;
		
		public UEFontAwesomeDrawable(Context context, final View view, String text, int color) {
			this.text = Html.fromHtml(text).toString();
			mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
			mTextPaint.setTypeface(typeface);
			mTextPaint.setDither(true);
			mTextPaint.setColor(color);
			mTextPaint.setTextAlign(Paint.Align.CENTER);
			mTextPaint.measureText(text);
			view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					width = view.getWidth();
					height = view.getHeight();
					mTextPaint.setTextSize(Math.min(width, height));
					view.getViewTreeObserver().removeOnPreDrawListener(this);
					return false;
				}
			});
		}
		
		public UEFontAwesomeDrawable(Context context, final View view, String text) {
			this(context, view, text, Color.BLACK);
		}
		
		@Override
		public void draw(Canvas canvas) {
			mFontMetrics = mTextPaint.getFontMetricsInt();
			int baseline = (mFontMetrics.bottom - mFontMetrics.top + height) / 2 - mFontMetrics.bottom;
			canvas.drawText(text, width/2, baseline, mTextPaint);
		}

		@Override
		public void setAlpha(int alpha) {
			mTextPaint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			mTextPaint.setColorFilter(cf);
		}
		
		public void setColor(int color) {
			mTextPaint.setColor(color);
		}

		@Override
		public int getOpacity() {
			return 0;
		}

	}
	
	public static final String FA_ADJUST = "&#xf042;";
	public static final String FA_ADN = "&#xf170;";
	public static final String FA_ALIGN_CENTER = "&#xf037;";
	public static final String FA_ALIGN_JUSTIFY = "&#xf039;";
	public static final String FA_ALIGN_LEFT = "&#xf036;";
	public static final String FA_ALIGN_RIGHT = "&#xf038;";
	public static final String FA_AMBULANCE = "&#xf0f9;";
	public static final String FA_ANCHOR = "&#xf13d;";
	public static final String FA_ANDROID = "&#xf17b;";
	public static final String FA_ANGELLIST = "&#xf209;";
	public static final String FA_ANGLE_DOUBLE_DOWN = "&#xf103;";
	public static final String FA_ANGLE_DOUBLE_LEFT = "&#xf100;";
	public static final String FA_ANGLE_DOUBLE_RIGHT = "&#xf101;";
	public static final String FA_ANGLE_DOUBLE_UP = "&#xf102;";
	public static final String FA_ANGLE_DOWN = "&#xf107;";
	public static final String FA_ANGLE_LEFT = "&#xf104;";
	public static final String FA_ANGLE_RIGHT = "&#xf105;";
	public static final String FA_ANGLE_UP = "&#xf106;";
	public static final String FA_APPLE = "&#xf179;";
	public static final String FA_ARCHIVE = "&#xf187;";
	public static final String FA_AREA_CHART = "&#xf1fe;";
	public static final String FA_ARROW_CIRCLE_DOWN = "&#xf0ab;";
	public static final String FA_ARROW_CIRCLE_LEFT = "&#xf0a8;";
	public static final String FA_ARROW_CIRCLE_O_DOWN = "&#xf01a;";
	public static final String FA_ARROW_CIRCLE_O_LEFT = "&#xf190;";
	public static final String FA_ARROW_CIRCLE_O_RIGHT = "&#xf18e;";
	public static final String FA_ARROW_CIRCLE_O_UP = "&#xf01b;";
	public static final String FA_ARROW_CIRCLE_RIGHT = "&#xf0a9;";
	public static final String FA_ARROW_CIRCLE_UP = "&#xf0aa;";
	public static final String FA_ARROW_DOWN = "&#xf063;";
	public static final String FA_ARROW_LEFT = "&#xf060;";
	public static final String FA_ARROW_RIGHT = "&#xf061;";
	public static final String FA_ARROW_UP = "&#xf062;";
	public static final String FA_ARROWS = "&#xf047;";
	public static final String FA_ARROWS_ALT = "&#xf0b2;";
	public static final String FA_ARROWS_H = "&#xf07e;";
	public static final String FA_ARROWS_V = "&#xf07d;";
	public static final String FA_ASTERISK = "&#xf069;";
	public static final String FA_AT = "&#xf1fa;";
	public static final String FA_AUTOMOBILE = "&#xf1b9;";
	public static final String FA_BACKWARD = "&#xf04a;";
	public static final String FA_BAN = "&#xf05e;";
	public static final String FA_BANK = "&#xf19c;";
	public static final String FA_BAR_CHART = "&#xf080;";
	public static final String FA_BAR_CHART_O = "&#xf080;";
	public static final String FA_BARCODE = "&#xf02a;";
	public static final String FA_BARS = "&#xf0c9;";
	public static final String FA_BEER = "&#xf0fc;";
	public static final String FA_BEHANCE = "&#xf1b4;";
	public static final String FA_BEHANCE_SQUARE = "&#xf1b5;";
	public static final String FA_BELL = "&#xf0f3;";
	public static final String FA_BELL_O = "&#xf0a2;";
	public static final String FA_BELL_SLASH = "&#xf1f6;";
	public static final String FA_BELL_SLASH_O = "&#xf1f7;";
	public static final String FA_BICYCLE = "&#xf206;";
	public static final String FA_BINOCULARS = "&#xf1e5;";
	public static final String FA_BIRTHDAY_CAKE = "&#xf1fd;";
	public static final String FA_BITBUCKET = "&#xf171;";
	public static final String FA_BITBUCKET_SQUARE = "&#xf172;";
	public static final String FA_BITCOIN = "&#xf15a;";
	public static final String FA_BOLD = "&#xf032;";
	public static final String FA_BOLT = "&#xf0e7;";
	public static final String FA_BOMB = "&#xf1e2;";
	public static final String FA_BOOK = "&#xf02d;";
	public static final String FA_BOOKMARK = "&#xf02e;";
	public static final String FA_BOOKMARK_O = "&#xf097;";
	public static final String FA_BRIEFCASE = "&#xf0b1;";
	public static final String FA_BTC = "&#xf15a;";
	public static final String FA_BUG = "&#xf188;";
	public static final String FA_BUILDING = "&#xf1ad;";
	public static final String FA_BUILDING_O = "&#xf0f7;";
	public static final String FA_BULLHORN = "&#xf0a1;";
	public static final String FA_BULLSEYE = "&#xf140;";
	public static final String FA_BUS = "&#xf207;";
	public static final String FA_CAB = "&#xf1ba;";
	public static final String FA_CALCULATOR = "&#xf1ec;";
	public static final String FA_CALENDAR = "&#xf073;";
	public static final String FA_CALENDAR_O = "&#xf133;";
	public static final String FA_CAMERA = "&#xf030;";
	public static final String FA_CAMERA_RETRO = "&#xf083;";
	public static final String FA_CAR = "&#xf1b9;";
	public static final String FA_CARET_DOWN = "&#xf0d7;";
	public static final String FA_CARET_LEFT = "&#xf0d9;";
	public static final String FA_CARET_RIGHT = "&#xf0da;";
	public static final String FA_CARET_SQUARE_O_DOWN = "&#xf150;";
	public static final String FA_CARET_SQUARE_O_LEFT = "&#xf191;";
	public static final String FA_CARET_SQUARE_O_RIGHT = "&#xf152;";
	public static final String FA_CARET_SQUARE_O_UP = "&#xf151;";
	public static final String FA_CARET_UP = "&#xf0d8;";
	public static final String FA_CC = "&#xf20a;";
	public static final String FA_CC_AMEX = "&#xf1f3;";
	public static final String FA_CC_DISCOVER = "&#xf1f2;";
	public static final String FA_CC_MASTERCARD = "&#xf1f1;";
	public static final String FA_CC_PAYPAL = "&#xf1f4;";
	public static final String FA_CC_STRIPE = "&#xf1f5;";
	public static final String FA_CC_VISA = "&#xf1f0;";
	public static final String FA_CERTIFICATE = "&#xf0a3;";
	public static final String FA_CHAIN = "&#xf0c1;";
	public static final String FA_CHAIN_BROKEN = "&#xf127;";
	public static final String FA_CHECK = "&#xf00c;";
	public static final String FA_CHECK_CIRCLE = "&#xf058;";
	public static final String FA_CHECK_CIRCLE_O = "&#xf05d;";
	public static final String FA_CHECK_SQUARE = "&#xf14a;";
	public static final String FA_CHECK_SQUARE_O = "&#xf046;";
	public static final String FA_CHEVRON_CIRCLE_DOWN = "&#xf13a;";
	public static final String FA_CHEVRON_CIRCLE_LEFT = "&#xf137;";
	public static final String FA_CHEVRON_CIRCLE_RIGHT = "&#xf138;";
	public static final String FA_CHEVRON_CIRCLE_UP = "&#xf139;";
	public static final String FA_CHEVRON_DOWN = "&#xf078;";
	public static final String FA_CHEVRON_LEFT = "&#xf053;";
	public static final String FA_CHEVRON_RIGHT = "&#xf054;";
	public static final String FA_CHEVRON_UP = "&#xf077;";
	public static final String FA_CHILD = "&#xf1ae;";
	public static final String FA_CIRCLE = "&#xf111;";
	public static final String FA_CIRCLE_O = "&#xf10c;";
	public static final String FA_CIRCLE_O_NOTCH = "&#xf1ce;";
	public static final String FA_CIRCLE_THIN = "&#xf1db;";
	public static final String FA_CLIPBOARD = "&#xf0ea;";
	public static final String FA_CLOCK_O = "&#xf017;";
	public static final String FA_CLOSE = "&#xf00d;";
	public static final String FA_CLOUD = "&#xf0c2;";
	public static final String FA_CLOUD_DOWNLOAD = "&#xf0ed;";
	public static final String FA_CLOUD_UPLOAD = "&#xf0ee;";
	public static final String FA_CNY = "&#xf157;";
	public static final String FA_CODE = "&#xf121;";
	public static final String FA_CODE_FORK = "&#xf126;";
	public static final String FA_CODEPEN = "&#xf1cb;";
	public static final String FA_COFFEE = "&#xf0f4;";
	public static final String FA_COG = "&#xf013;";
	public static final String FA_COGS = "&#xf085;";
	public static final String FA_COLUMNS = "&#xf0db;";
	public static final String FA_COMMENT = "&#xf075;";
	public static final String FA_COMMENT_O = "&#xf0e5;";
	public static final String FA_COMMENTS = "&#xf086;";
	public static final String FA_COMMENTS_O = "&#xf0e6;";
	public static final String FA_COMPASS = "&#xf14e;";
	public static final String FA_COMPRESS = "&#xf066;";
	public static final String FA_COPY = "&#xf0c5;";
	public static final String FA_COPYRIGHT = "&#xf1f9;";
	public static final String FA_CREDIT_CARD = "&#xf09d;";
	public static final String FA_CROP = "&#xf125;";
	public static final String FA_CROSSHAIRS = "&#xf05b;";
	public static final String FA_CSS3 = "&#xf13c;";
	public static final String FA_CUBE = "&#xf1b2;";
	public static final String FA_CUBES = "&#xf1b3;";
	public static final String FA_CUT = "&#xf0c4;";
	public static final String FA_CUTLERY = "&#xf0f5;";
	public static final String FA_DASHBOARD = "&#xf0e4;";
	public static final String FA_DATABASE = "&#xf1c0;";
	public static final String FA_DEDENT = "&#xf03b;";
	public static final String FA_DELICIOUS = "&#xf1a5;";
	public static final String FA_DESKTOP = "&#xf108;";
	public static final String FA_DEVIANTART = "&#xf1bd;";
	public static final String FA_DIGG = "&#xf1a6;";
	public static final String FA_DOLLAR = "&#xf155;";
	public static final String FA_DOT_CIRCLE_O = "&#xf192;";
	public static final String FA_DOWNLOAD = "&#xf019;";
	public static final String FA_DRIBBBLE = "&#xf17d;";
	public static final String FA_DROPBOX = "&#xf16b;";
	public static final String FA_DRUPAL = "&#xf1a9;";
	public static final String FA_EDIT = "&#xf044;";
	public static final String FA_EJECT = "&#xf052;";
	public static final String FA_ELLIPSIS_H = "&#xf141;";
	public static final String FA_ELLIPSIS_V = "&#xf142;";
	public static final String FA_EMPIRE = "&#xf1d1;";
	public static final String FA_ENVELOPE = "&#xf0e0;";
	public static final String FA_ENVELOPE_O = "&#xf003;";
	public static final String FA_ENVELOPE_SQUARE = "&#xf199;";
	public static final String FA_ERASER = "&#xf12d;";
	public static final String FA_EUR = "&#xf153;";
	public static final String FA_EURO = "&#xf153;";
	public static final String FA_EXCHANGE = "&#xf0ec;";
	public static final String FA_EXCLAMATION = "&#xf12a;";
	public static final String FA_EXCLAMATION_CIRCLE = "&#xf06a;";
	public static final String FA_EXCLAMATION_TRIANGLE = "&#xf071;";
	public static final String FA_EXPAND = "&#xf065;";
	public static final String FA_EXTERNAL_LINK = "&#xf08e;";
	public static final String FA_EXTERNAL_LINK_SQUARE = "&#xf14c;";
	public static final String FA_EYE = "&#xf06e;";
	public static final String FA_EYE_SLASH = "&#xf070;";
	public static final String FA_EYEDROPPER = "&#xf1fb;";
	public static final String FA_FACEBOOK = "&#xf09a;";
	public static final String FA_FACEBOOK_SQUARE = "&#xf082;";
	public static final String FA_FAST_BACKWARD = "&#xf049;";
	public static final String FA_FAST_FORWARD = "&#xf050;";
	public static final String FA_FAX = "&#xf1ac;";
	public static final String FA_FEMALE = "&#xf182;";
	public static final String FA_FIGHTER_JET = "&#xf0fb;";
	public static final String FA_FILE = "&#xf15b;";
	public static final String FA_FILE_ARCHIVE_O = "&#xf1c6;";
	public static final String FA_FILE_AUDIO_O = "&#xf1c7;";
	public static final String FA_FILE_CODE_O = "&#xf1c9;";
	public static final String FA_FILE_EXCEL_O = "&#xf1c3;";
	public static final String FA_FILE_IMAGE_O = "&#xf1c5;";
	public static final String FA_FILE_MOVIE_O = "&#xf1c8;";
	public static final String FA_FILE_O = "&#xf016;";
	public static final String FA_FILE_PDF_O = "&#xf1c1;";
	public static final String FA_FILE_PHOTO_O = "&#xf1c5;";
	public static final String FA_FILE_PICTURE_O = "&#xf1c5;";
	public static final String FA_FILE_POWERPOINT_O = "&#xf1c4;";
	public static final String FA_FILE_SOUND_O = "&#xf1c7;";
	public static final String FA_FILE_TEXT = "&#xf15c;";
	public static final String FA_FILE_TEXT_O = "&#xf0f6;";
	public static final String FA_FILE_VIDEO_O = "&#xf1c8;";
	public static final String FA_FILE_WORD_O = "&#xf1c2;";
	public static final String FA_FILE_ZIP_O = "&#xf1c6;";
	public static final String FA_FILES_O = "&#xf0c5;";
	public static final String FA_FILM = "&#xf008;";
	public static final String FA_FILTER = "&#xf0b0;";
	public static final String FA_FIRE = "&#xf06d;";
	public static final String FA_FIRE_EXTINGUISHER = "&#xf134;";
	public static final String FA_FLAG = "&#xf024;";
	public static final String FA_FLAG_CHECKERED = "&#xf11e;";
	public static final String FA_FLAG_O = "&#xf11d;";
	public static final String FA_FLASH = "&#xf0e7;";
	public static final String FA_FLASK = "&#xf0c3;";
	public static final String FA_FLICKR = "&#xf16e;";
	public static final String FA_FLOPPY_O = "&#xf0c7;";
	public static final String FA_FOLDER = "&#xf07b;";
	public static final String FA_FOLDER_O = "&#xf114;";
	public static final String FA_FOLDER_OPEN = "&#xf07c;";
	public static final String FA_FOLDER_OPEN_O = "&#xf115;";
	public static final String FA_FONT = "&#xf031;";
	public static final String FA_FORWARD = "&#xf04e;";
	public static final String FA_FOURSQUARE = "&#xf180;";
	public static final String FA_FROWN_O = "&#xf119;";
	public static final String FA_FUTBOL_O = "&#xf1e3;";
	public static final String FA_GAMEPAD = "&#xf11b;";
	public static final String FA_GAVEL = "&#xf0e3;";
	public static final String FA_GBP = "&#xf154;";
	public static final String FA_GE = "&#xf1d1;";
	public static final String FA_GEAR = "&#xf013;";
	public static final String FA_GEARS = "&#xf085;";
	public static final String FA_GIFT = "&#xf06b;";
	public static final String FA_GIT = "&#xf1d3;";
	public static final String FA_GIT_SQUARE = "&#xf1d2;";
	public static final String FA_GITHUB = "&#xf09b;";
	public static final String FA_GITHUB_ALT = "&#xf113;";
	public static final String FA_GITHUB_SQUARE = "&#xf092;";
	public static final String FA_GITTIP = "&#xf184;";
	public static final String FA_GLASS = "&#xf000;";
	public static final String FA_GLOBE = "&#xf0ac;";
	public static final String FA_GOOGLE = "&#xf1a0;";
	public static final String FA_GOOGLE_PLUS = "&#xf0d5;";
	public static final String FA_GOOGLE_PLUS_SQUARE = "&#xf0d4;";
	public static final String FA_GOOGLE_WALLET = "&#xf1ee;";
	public static final String FA_GRADUATION_CAP = "&#xf19d;";
	public static final String FA_GROUP = "&#xf0c0;";
	public static final String FA_H_SQUARE = "&#xf0fd;";
	public static final String FA_HACKER_NEWS = "&#xf1d4;";
	public static final String FA_HAND_O_DOWN = "&#xf0a7;";
	public static final String FA_HAND_O_LEFT = "&#xf0a5;";
	public static final String FA_HAND_O_RIGHT = "&#xf0a4;";
	public static final String FA_HAND_O_UP = "&#xf0a6;";
	public static final String FA_HDD_O = "&#xf0a0;";
	public static final String FA_HEADER = "&#xf1dc;";
	public static final String FA_HEADPHONES = "&#xf025;";
	public static final String FA_HEART = "&#xf004;";
	public static final String FA_HEART_O = "&#xf08a;";
	public static final String FA_HISTORY = "&#xf1da;";
	public static final String FA_HOME = "&#xf015;";
	public static final String FA_HOSPITAL_O = "&#xf0f8;";
	public static final String FA_HTML5 = "&#xf13b;";
	public static final String FA_ILS = "&#xf20b;";
	public static final String FA_IMAGE = "&#xf03e;";
	public static final String FA_INBOX = "&#xf01c;";
	public static final String FA_INDENT = "&#xf03c;";
	public static final String FA_INFO = "&#xf129;";
	public static final String FA_INFO_CIRCLE = "&#xf05a;";
	public static final String FA_INR = "&#xf156;";
	public static final String FA_INSTAGRAM = "&#xf16d;";
	public static final String FA_INSTITUTION = "&#xf19c;";
	public static final String FA_IOXHOST = "&#xf208;";
	public static final String FA_ITALIC = "&#xf033;";
	public static final String FA_JOOMLA = "&#xf1aa;";
	public static final String FA_JPY = "&#xf157;";
	public static final String FA_JSFIDDLE = "&#xf1cc;";
	public static final String FA_KEY = "&#xf084;";
	public static final String FA_KEYBOARD_O = "&#xf11c;";
	public static final String FA_KRW = "&#xf159;";
	public static final String FA_LANGUAGE = "&#xf1ab;";
	public static final String FA_LAPTOP = "&#xf109;";
	public static final String FA_LASTFM = "&#xf202;";
	public static final String FA_LASTFM_SQUARE = "&#xf203;";
	public static final String FA_LEAF = "&#xf06c;";
	public static final String FA_LEGAL = "&#xf0e3;";
	public static final String FA_LEMON_O = "&#xf094;";
	public static final String FA_LEVEL_DOWN = "&#xf149;";
	public static final String FA_LEVEL_UP = "&#xf148;";
	public static final String FA_LIFE_BOUY = "&#xf1cd;";
	public static final String FA_LIFE_BUOY = "&#xf1cd;";
	public static final String FA_LIFE_RING = "&#xf1cd;";
	public static final String FA_LIFE_SAVER = "&#xf1cd;";
	public static final String FA_LIGHTBULB_O = "&#xf0eb;";
	public static final String FA_LINE_CHART = "&#xf201;";
	public static final String FA_LINK = "&#xf0c1;";
	public static final String FA_LINKEDIN = "&#xf0e1;";
	public static final String FA_LINKEDIN_SQUARE = "&#xf08c;";
	public static final String FA_LINUX = "&#xf17c;";
	public static final String FA_LIST = "&#xf03a;";
	public static final String FA_LIST_ALT = "&#xf022;";
	public static final String FA_LIST_OL = "&#xf0cb;";
	public static final String FA_LIST_UL = "&#xf0ca;";
	public static final String FA_LOCATION_ARROW = "&#xf124;";
	public static final String FA_LOCK = "&#xf023;";
	public static final String FA_LONG_ARROW_DOWN = "&#xf175;";
	public static final String FA_LONG_ARROW_LEFT = "&#xf177;";
	public static final String FA_LONG_ARROW_RIGHT = "&#xf178;";
	public static final String FA_LONG_ARROW_UP = "&#xf176;";
	public static final String FA_MAGIC = "&#xf0d0;";
	public static final String FA_MAGNET = "&#xf076;";
	public static final String FA_MAIL_FORWARD = "&#xf064;";
	public static final String FA_MAIL_REPLY = "&#xf112;";
	public static final String FA_MAIL_REPLY_ALL = "&#xf122;";
	public static final String FA_MALE = "&#xf183;";
	public static final String FA_MAP_MARKER = "&#xf041;";
	public static final String FA_MAXCDN = "&#xf136;";
	public static final String FA_MEANPATH = "&#xf20c;";
	public static final String FA_MEDKIT = "&#xf0fa;";
	public static final String FA_MEH_O = "&#xf11a;";
	public static final String FA_MICROPHONE = "&#xf130;";
	public static final String FA_MICROPHONE_SLASH = "&#xf131;";
	public static final String FA_MINUS = "&#xf068;";
	public static final String FA_MINUS_CIRCLE = "&#xf056;";
	public static final String FA_MINUS_SQUARE = "&#xf146;";
	public static final String FA_MINUS_SQUARE_O = "&#xf147;";
	public static final String FA_MOBILE = "&#xf10b;";
	public static final String FA_MOBILE_PHONE = "&#xf10b;";
	public static final String FA_MONEY = "&#xf0d6;";
	public static final String FA_MOON_O = "&#xf186;";
	public static final String FA_MORTAR_BOARD = "&#xf19d;";
	public static final String FA_MUSIC = "&#xf001;";
	public static final String FA_NAVICON = "&#xf0c9;";
	public static final String FA_NEWSPAPER_O = "&#xf1ea;";
	public static final String FA_OPENID = "&#xf19b;";
	public static final String FA_OUTDENT = "&#xf03b;";
	public static final String FA_PAGELINES = "&#xf18c;";
	public static final String FA_PAINT_BRUSH = "&#xf1fc;";
	public static final String FA_PAPER_PLANE = "&#xf1d8;";
	public static final String FA_PAPER_PLANE_O = "&#xf1d9;";
	public static final String FA_PAPERCLIP = "&#xf0c6;";
	public static final String FA_PARAGRAPH = "&#xf1dd;";
	public static final String FA_PASTE = "&#xf0ea;";
	public static final String FA_PAUSE = "&#xf04c;";
	public static final String FA_PAW = "&#xf1b0;";
	public static final String FA_PAYPAL = "&#xf1ed;";
	public static final String FA_PENCIL = "&#xf040;";
	public static final String FA_PENCIL_SQUARE = "&#xf14b;";
	public static final String FA_PENCIL_SQUARE_O = "&#xf044;";
	public static final String FA_PHONE = "&#xf095;";
	public static final String FA_PHONE_SQUARE = "&#xf098;";
	public static final String FA_PHOTO = "&#xf03e;";
	public static final String FA_PICTURE_O = "&#xf03e;";
	public static final String FA_PIE_CHART = "&#xf200;";
	public static final String FA_PIED_PIPER = "&#xf1a7;";
	public static final String FA_PIED_PIPER_ALT = "&#xf1a8;";
	public static final String FA_PINTEREST = "&#xf0d2;";
	public static final String FA_PINTEREST_SQUARE = "&#xf0d3;";
	public static final String FA_PLANE = "&#xf072;";
	public static final String FA_PLAY = "&#xf04b;";
	public static final String FA_PLAY_CIRCLE = "&#xf144;";
	public static final String FA_PLAY_CIRCLE_O = "&#xf01d;";
	public static final String FA_PLUG = "&#xf1e6;";
	public static final String FA_PLUS = "&#xf067;";
	public static final String FA_PLUS_CIRCLE = "&#xf055;";
	public static final String FA_PLUS_SQUARE = "&#xf0fe;";
	public static final String FA_PLUS_SQUARE_O = "&#xf196;";
	public static final String FA_POWER_OFF = "&#xf011;";
	public static final String FA_PRINT = "&#xf02f;";
	public static final String FA_PUZZLE_PIECE = "&#xf12e;";
	public static final String FA_QQ = "&#xf1d6;";
	public static final String FA_QRCODE = "&#xf029;";
	public static final String FA_QUESTION = "&#xf128;";
	public static final String FA_QUESTION_CIRCLE = "&#xf059;";
	public static final String FA_QUOTE_LEFT = "&#xf10d;";
	public static final String FA_QUOTE_RIGHT = "&#xf10e;";
	public static final String FA_RA = "&#xf1d0;";
	public static final String FA_RANDOM = "&#xf074;";
	public static final String FA_REBEL = "&#xf1d0;";
	public static final String FA_RECYCLE = "&#xf1b8;";
	public static final String FA_REDDIT = "&#xf1a1;";
	public static final String FA_REDDIT_SQUARE = "&#xf1a2;";
	public static final String FA_REFRESH = "&#xf021;";
	public static final String FA_REMOVE = "&#xf00d;";
	public static final String FA_RENREN = "&#xf18b;";
	public static final String FA_REORDER = "&#xf0c9;";
	public static final String FA_REPEAT = "&#xf01e;";
	public static final String FA_REPLY = "&#xf112;";
	public static final String FA_REPLY_ALL = "&#xf122;";
	public static final String FA_RETWEET = "&#xf079;";
	public static final String FA_RMB = "&#xf157;";
	public static final String FA_ROAD = "&#xf018;";
	public static final String FA_ROCKET = "&#xf135;";
	public static final String FA_ROTATE_LEFT = "&#xf0e2;";
	public static final String FA_ROTATE_RIGHT = "&#xf01e;";
	public static final String FA_ROUBLE = "&#xf158;";
	public static final String FA_RSS = "&#xf09e;";
	public static final String FA_RSS_SQUARE = "&#xf143;";
	public static final String FA_RUB = "&#xf158;";
	public static final String FA_RUBLE = "&#xf158;";
	public static final String FA_RUPEE = "&#xf156;";
	public static final String FA_SAVE = "&#xf0c7;";
	public static final String FA_SCISSORS = "&#xf0c4;";
	public static final String FA_SEARCH = "&#xf002;";
	public static final String FA_SEARCH_MINUS = "&#xf010;";
	public static final String FA_SEARCH_PLUS = "&#xf00e;";
	public static final String FA_SEND = "&#xf1d8;";
	public static final String FA_SEND_O = "&#xf1d9;";
	public static final String FA_SHARE = "&#xf064;";
	public static final String FA_SHARE_ALT = "&#xf1e0;";
	public static final String FA_SHARE_ALT_SQUARE = "&#xf1e1;";
	public static final String FA_SHARE_SQUARE = "&#xf14d;";
	public static final String FA_SHARE_SQUARE_O = "&#xf045;";
	public static final String FA_SHEKEL = "&#xf20b;";
	public static final String FA_SHEQEL = "&#xf20b;";
	public static final String FA_SHIELD = "&#xf132;";
	public static final String FA_SHOPPING_CART = "&#xf07a;";
	public static final String FA_SIGN_IN = "&#xf090;";
	public static final String FA_SIGN_OUT = "&#xf08b;";
	public static final String FA_SIGNAL = "&#xf012;";
	public static final String FA_SITEMAP = "&#xf0e8;";
	public static final String FA_SKYPE = "&#xf17e;";
	public static final String FA_SLACK = "&#xf198;";
	public static final String FA_SLIDERS = "&#xf1de;";
	public static final String FA_SLIDESHARE = "&#xf1e7;";
	public static final String FA_SMILE_O = "&#xf118;";
	public static final String FA_SOCCER_BALL_O = "&#xf1e3;";
	public static final String FA_SORT = "&#xf0dc;";
	public static final String FA_SORT_ALPHA_ASC = "&#xf15d;";
	public static final String FA_SORT_ALPHA_DESC = "&#xf15e;";
	public static final String FA_SORT_AMOUNT_ASC = "&#xf160;";
	public static final String FA_SORT_AMOUNT_DESC = "&#xf161;";
	public static final String FA_SORT_ASC = "&#xf0de;";
	public static final String FA_SORT_DESC = "&#xf0dd;";
	public static final String FA_SORT_DOWN = "&#xf0dd;";
	public static final String FA_SORT_NUMERIC_ASC = "&#xf162;";
	public static final String FA_SORT_NUMERIC_DESC = "&#xf163;";
	public static final String FA_SORT_UP = "&#xf0de;";
	public static final String FA_SOUNDCLOUD = "&#xf1be;";
	public static final String FA_SPACE_SHUTTLE = "&#xf197;";
	public static final String FA_SPINNER = "&#xf110;";
	public static final String FA_SPOON = "&#xf1b1;";
	public static final String FA_SPOTIFY = "&#xf1bc;";
	public static final String FA_SQUARE = "&#xf0c8;";
	public static final String FA_SQUARE_O = "&#xf096;";
	public static final String FA_STACK_EXCHANGE = "&#xf18d;";
	public static final String FA_STACK_OVERFLOW = "&#xf16c;";
	public static final String FA_STAR = "&#xf005;";
	public static final String FA_STAR_HALF = "&#xf089;";
	public static final String FA_STAR_HALF_EMPTY = "&#xf123;";
	public static final String FA_STAR_HALF_FULL = "&#xf123;";
	public static final String FA_STAR_HALF_O = "&#xf123;";
	public static final String FA_STAR_O = "&#xf006;";
	public static final String FA_STEAM = "&#xf1b6;";
	public static final String FA_STEAM_SQUARE = "&#xf1b7;";
	public static final String FA_STEP_BACKWARD = "&#xf048;";
	public static final String FA_STEP_FORWARD = "&#xf051;";
	public static final String FA_STETHOSCOPE = "&#xf0f1;";
	public static final String FA_STOP = "&#xf04d;";
	public static final String FA_STRIKETHROUGH = "&#xf0cc;";
	public static final String FA_STUMBLEUPON = "&#xf1a4;";
	public static final String FA_STUMBLEUPON_CIRCLE = "&#xf1a3;";
	public static final String FA_SUBSCRIPT = "&#xf12c;";
	public static final String FA_SUITCASE = "&#xf0f2;";
	public static final String FA_SUN_O = "&#xf185;";
	public static final String FA_SUPERSCRIPT = "&#xf12b;";
	public static final String FA_SUPPORT = "&#xf1cd;";
	public static final String FA_TABLE = "&#xf0ce;";
	public static final String FA_TABLET = "&#xf10a;";
	public static final String FA_TACHOMETER = "&#xf0e4;";
	public static final String FA_TAG = "&#xf02b;";
	public static final String FA_TAGS = "&#xf02c;";
	public static final String FA_TASKS = "&#xf0ae;";
	public static final String FA_TAXI = "&#xf1ba;";
	public static final String FA_TENCENT_WEIBO = "&#xf1d5;";
	public static final String FA_TERMINAL = "&#xf120;";
	public static final String FA_TEXT_HEIGHT = "&#xf034;";
	public static final String FA_TEXT_WIDTH = "&#xf035;";
	public static final String FA_TH = "&#xf00a;";
	public static final String FA_TH_LARGE = "&#xf009;";
	public static final String FA_TH_LIST = "&#xf00b;";
	public static final String FA_THUMB_TACK = "&#xf08d;";
	public static final String FA_THUMBS_DOWN = "&#xf165;";
	public static final String FA_THUMBS_O_DOWN = "&#xf088;";
	public static final String FA_THUMBS_O_UP = "&#xf087;";
	public static final String FA_THUMBS_UP = "&#xf164;";
	public static final String FA_TICKET = "&#xf145;";
	public static final String FA_TIMES = "&#xf00d;";
	public static final String FA_TIMES_CIRCLE = "&#xf057;";
	public static final String FA_TIMES_CIRCLE_O = "&#xf05c;";
	public static final String FA_TINT = "&#xf043;";
	public static final String FA_TOGGLE_DOWN = "&#xf150;";
	public static final String FA_TOGGLE_LEFT = "&#xf191;";
	public static final String FA_TOGGLE_OFF = "&#xf204;";
	public static final String FA_TOGGLE_ON = "&#xf205;";
	public static final String FA_TOGGLE_RIGHT = "&#xf152;";
	public static final String FA_TOGGLE_UP = "&#xf151;";
	public static final String FA_TRASH = "&#xf1f8;";
	public static final String FA_TRASH_O = "&#xf014;";
	public static final String FA_TREE = "&#xf1bb;";
	public static final String FA_TRELLO = "&#xf181;";
	public static final String FA_TROPHY = "&#xf091;";
	public static final String FA_TRUCK = "&#xf0d1;";
	public static final String FA_TRY = "&#xf195;";
	public static final String FA_TTY = "&#xf1e4;";
	public static final String FA_TUMBLR = "&#xf173;";
	public static final String FA_TUMBLR_SQUARE = "&#xf174;";
	public static final String FA_TURKISH_LIRA = "&#xf195;";
	public static final String FA_TWITCH = "&#xf1e8;";
	public static final String FA_TWITTER = "&#xf099;";
	public static final String FA_TWITTER_SQUARE = "&#xf081;";
	public static final String FA_UMBRELLA = "&#xf0e9;";
	public static final String FA_UNDERLINE = "&#xf0cd;";
	public static final String FA_UNDO = "&#xf0e2;";
	public static final String FA_UNIVERSITY = "&#xf19c;";
	public static final String FA_UNLINK = "&#xf127;";
	public static final String FA_UNLOCK = "&#xf09c;";
	public static final String FA_UNLOCK_ALT = "&#xf13e;";
	public static final String FA_UNSORTED = "&#xf0dc;";
	public static final String FA_UPLOAD = "&#xf093;";
	public static final String FA_USD = "&#xf155;";
	public static final String FA_USER = "&#xf007;";
	public static final String FA_USER_MD = "&#xf0f0;";
	public static final String FA_USERS = "&#xf0c0;";
	public static final String FA_VIDEO_CAMERA = "&#xf03d;";
	public static final String FA_VIMEO_SQUARE = "&#xf194;";
	public static final String FA_VINE = "&#xf1ca;";
	public static final String FA_VK = "&#xf189;";
	public static final String FA_VOLUME_DOWN = "&#xf027;";
	public static final String FA_VOLUME_OFF = "&#xf026;";
	public static final String FA_VOLUME_UP = "&#xf028;";
	public static final String FA_WARNING = "&#xf071;";
	public static final String FA_WECHAT = "&#xf1d7;";
	public static final String FA_WEIBO = "&#xf18a;";
	public static final String FA_WEIXIN = "&#xf1d7;";
	public static final String FA_WHEELCHAIR = "&#xf193;";
	public static final String FA_WIFI = "&#xf1eb;";
	public static final String FA_WINDOWS = "&#xf17a;";
	public static final String FA_WON = "&#xf159;";
	public static final String FA_WORDPRESS = "&#xf19a;";
	public static final String FA_WRENCH = "&#xf0ad;";
	public static final String FA_XING = "&#xf168;";
	public static final String FA_XING_SQUARE = "&#xf169;";
	public static final String FA_YAHOO = "&#xf19e;";
	public static final String FA_YELP = "&#xf1e9;";
	public static final String FA_YEN = "&#xf157;";
	public static final String FA_YOUTUBE = "&#xf167;";
	public static final String FA_YOUTUBE_PLAY = "&#xf16a;";
	public static final String FA_YOUTUBE_SQUARE = "&#xf166;";

}
