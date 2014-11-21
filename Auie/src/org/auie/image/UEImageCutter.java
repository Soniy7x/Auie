package org.auie.image;

import java.util.HashMap;
import java.util.Map;

import org.auie.image.UEImageLoader.OnUEImageLoadListener;
import org.auie.utils.UEException.UEImageNotByteException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public final class UEImageCutter {

	private Context mContext;
	
	private static Map<String, Bitmap> map;
	private static Map<String, Bitmap[]> bitmaps;
	private static UEImageCutter instance;
	private static UEImageLoader mImageLoader;
	
	static{
		map = new HashMap<String, Bitmap>();
		bitmaps = new HashMap<String, Bitmap[]>();
	}
	
	private OnUEImageLoadListener mLoadListener = new OnUEImageLoadListener() {
		@Override
		public void onImageLoadCompleted(Bitmap bitmap, String imageUrl) {
			if (bitmap != null) {
				map.put(imageUrl, bitmap);
			}
		}
	};
	
	private UEImageCutter(Context context){
		mContext = context;
		mImageLoader = UEImageLoader.getInstance(context);
	};
	
	public static UEImageCutter getInstance(Context context){
		if (instance == null) {
			instance = new UEImageCutter(context);
		}
		return instance;
	}
	
	public Bitmap cut(String key, int x1, int y1, int x2, int y2){
		Bitmap bitmap = getImage(key);
		if (bitmap == null || y2 > bitmap.getHeight() || x1 > bitmap.getWidth() || x1 < 0 || y2 < 0) {
			return null;
		}
		return Bitmap.createBitmap(bitmap, x1, y1, x2 - x1, y2 - y1);
	}
	
	public Bitmap cut(String key, String x1, String y1, String x2, String y2){
		return cut(key, Integer.parseInt(x1), Integer.parseInt(y1), Integer.parseInt(x2), Integer.parseInt(y2));
	}
	
	public Bitmap getImage(String key){
		return map.get(key);
	}
	
	public Bitmap getSubImage(String key, int index){
		if(!bitmaps.containsKey(key)){
			return null;
		}
		Bitmap[] bits = bitmaps.get(key);
		if (bits == null || bits.length <= index) {
			return null;
		}
		return bits[index];
	}
	
	public void putImage(String key, Drawable drawable, int rules){
		putImage(key, new UEImage(drawable).toBitmap(), rules);
    }
	
	public void putImage(String key, int resId, int rules){
		putImage(key, mContext.getResources().getDrawable(resId), rules);
    }
	
    public void putImage(String key, UEImage image, int rules){
    	putImage(key, image.toBitmap(), rules);
    }
	
	public void putImage(String key, Bitmap bitmap, int rules){
		putImage(key, bitmap, mContext.getResources().getStringArray(rules));
	}
	
	public void putImage(String key, Drawable drawable, String[] rules){
		putImage(key, new UEImage(drawable).toBitmap(), rules);
    }
	
	public void putImage(String key, int resId, String[] rules){
		putImage(key, mContext.getResources().getDrawable(resId), rules);
    }
	
    public void putImage(String key, UEImage image, String[] rules){
    	putImage(key, image.toBitmap(), rules);
    }
	
	public void putImage(String key, Bitmap bitmap, String[] rules){
		putImage(key, bitmap);
		if (rules == null || rules.length == 0) {
			bitmaps.put(key, new Bitmap[]{bitmap});
		}else {
			int length = rules.length;
			Bitmap[] bits = new Bitmap[length];
			for (int i = 0; i < length; i++) {
				String[] xy = rules[i].trim().split(",");
				if (xy.length == 4) {
					bits[i] = cut(key, xy[0], xy[1], xy[2], xy[3]);
				}
			}
			bitmaps.put(key, bits);
		}
	}
	
	public void putImage(String key, Bitmap bitmap){
		map.put(key, bitmap);
	}
	
	public void putImage(String key, Drawable drawable){
		putImage(key, new UEImage(drawable).toBitmap());
    }
	
	public void putImage(String key, int resId){
		putImage(key, mContext.getResources().getDrawable(resId));
    }
	
    public void putImage(String key, UEImage image){
    	putImage(key, image.toBitmap());
    }
    
    public void putImage(String key, byte[] bs) throws UEImageNotByteException{
    	putImage(key, new UEImage(bs).toBitmap());
    }
    
    public void putImageByFile(String filepath){
    	mImageLoader.loadBitmapByFile(filepath, mLoadListener);
    }
    
    public void putImageByHTTP(String url){
    	mImageLoader.loadBitmapByHttp(url, mLoadListener);
    }
    
}
