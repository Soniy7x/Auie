package org.auie.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;

public class UEImageManager {

	public static final float TRANSFORMROUND_CIRCLE = -1;
	
	public static Bitmap transformRound(Drawable drawable, float radius){
		return transformRound(drawableToBitmap(drawable), radius);
	}
	
	public static Bitmap transformRound(Bitmap bitmap, float radius){
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final int color = 0xff888888; 
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		if (radius == TRANSFORMROUND_CIRCLE) {
			radius = width > height ? width/2 : height/2;
		}
		Bitmap mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		paint.setAntiAlias(true);
		paint.setColor(color);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, radius, radius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return mBitmap;
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {
		final int width = drawable.getIntrinsicWidth();
		final int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}
	
}
