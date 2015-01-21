package org.auie.ui;

import org.auie.utils.UE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

@SuppressLint("NewApi")
public class UICircleImageView extends View{
	
	private Bitmap bitmap;
	private Bitmap dstBitmap;
	private boolean isXfermode = false;
	private Path path=new Path();
	private Paint paint = new Paint();
	private Rect bitmapRect = new Rect();
	private PaintFlagsDrawFilter pdf=new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
	private PorterDuffXfermode xfermode=new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
	
	{
		paint.setStyle(Paint.Style.STROKE);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setAntiAlias(true);
	}
	
	public UICircleImageView(Context context) {
		super(context);
	}
	public UICircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public UICircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	public UICircleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	public void setImageBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
		try {
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				setLayerType(LAYER_TYPE_SOFTWARE, null);
			}
		} catch (Exception e) {
			isXfermode = true;
			Log.d(UE.TAG, e.toString());
		}
	}
	
	private Bitmap makeDst(int w, int h){
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xFFFFFFFF);
		canvas.drawOval(new RectF(0, 0, w, h), paint);
		return bitmap;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (bitmap == null) {
			return;
		}
		if (isXfermode) {
			bitmapRect.set(0, 0, getWidth(), getHeight());
			canvas.save();
			canvas.setDrawFilter(pdf);
			path.reset();
			canvas.clipPath(path);
			path.addCircle(getWidth()/2, getWidth()/2, getHeight()/2, Path.Direction.CCW);
			canvas.clipPath(path, Region.Op.REPLACE);
			canvas.drawBitmap(bitmap, null, bitmapRect, paint);
			canvas.restore();
		}else {
			if (dstBitmap == null) {
				dstBitmap = makeDst(getWidth(), getHeight());
			}
			bitmapRect.set(0, 0, getWidth(), getHeight());
			canvas.save();
			canvas.setDrawFilter(pdf);
			canvas.drawBitmap(dstBitmap, 0, 0, paint);
			paint.setXfermode(xfermode);
			canvas.drawBitmap(bitmap, null, bitmapRect, paint);
			paint.setXfermode(null);
			canvas.restore();
		}
	}
}
