package org.auie.image;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import org.auie.utils.UEException.UEImageNotByteException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

@SuppressWarnings("deprecation")
public class UEImage implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static final float TRANSFORMROUND_CIRCLE = -1;

	private Bitmap bitmap = null;
	private byte[] bytes = null;
	
	public UEImage(Drawable drawable){
		drawableToBitmap(drawable);
	}
	
	public UEImage(Resources res, int resId){
		this(res.getDrawable(resId));
	}
	
	public UEImage(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	
	public UEImage(String filePath) throws UEImageNotByteException{
		if (!new File(filePath).exists()) {
			throw new UEImageNotByteException("this params length is 0 or not exists, so not tansform to image.");
		}
		this.bitmap = BitmapFactory.decodeFile(filePath);
	}

	public UEImage(byte[] data) throws UEImageNotByteException{
		if (data.length == 0) {
			throw new UEImageNotByteException("this params length is 0 or not exists, so not tansform to image.");
		}
		this.bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	public UEImage(String filePath, boolean resize) throws IOException, UEImageNotByteException{
		if (!resize) {
			if (!new File(filePath).exists()) {
				throw new UEImageNotByteException("this params length is 0 or not exists, so not tansform to image.");
			}
			this.bitmap = BitmapFactory.decodeFile(filePath);
		} else {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(filePath)));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			int i = 0;
			Bitmap bitmap = null;
			while (true) {
				if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
					in = new BufferedInputStream(new FileInputStream(new File(filePath)));
					options.inSampleSize = (int) Math.pow(2.0D, i);
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(in, null, options);
					break;
				}
				i += 1;
			}
			this.bitmap = bitmap;
		}
	}
	
	public UEImage transformRound(float radius){
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
		this.bitmap = mBitmap;
		return this;
	}
	
	public Bitmap toBitmap(){
		return bitmap;
	}
	
	public Drawable toDrawable(){
		return bitmapToDrawable();
	}
	
	public byte[] toByteArray(){
		if (bytes == null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		    return baos.toByteArray();
		}else {
			return bytes;
		}
	}
	
	private Drawable bitmapToDrawable(){
		return new BitmapDrawable(this.bitmap);
	}
	
	private void drawableToBitmap(Drawable drawable) {
		final int width = drawable.getIntrinsicWidth();
		final int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		this.bitmap = bitmap;
	}
	
	public UEImage addFilterToGray(){
		Drawable drawable = bitmapToDrawable();
		drawable.mutate();
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0);
		drawable.setColorFilter(new ColorMatrixColorFilter(matrix));
		drawableToBitmap(drawable);
		return this;
	}
	
	public UEImage compressOnlyQuality(int quality) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        bytes = baos.toByteArray();
        return this;
    }
	
	public UEImage compress(int quality) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        int options = 100;  
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > quality) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }  
        bytes = baos.toByteArray();
        return this;
    }
	
	public UEImage resize(int width, int height){
		float scaleWidth = ((float)width) / bitmap.getWidth();
		float scaleHeight = ((float)height) / bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
 		return this;
	}
	
	/**
	 * 生成图片
	 */
	public static ShapeDrawable createBackground(int color, float radius){
		return createBackground(color, 255, radius);
	}
	
	/**
	 * 生成图片
	 */
	public static ShapeDrawable createBackground(int color, int alpha, float radius){
		float[] outerR = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
		RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setAlpha(alpha);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        return shapeDrawable;
	}
}
