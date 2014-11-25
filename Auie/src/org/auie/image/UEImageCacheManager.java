package org.auie.image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.auie.utils.UE;
import org.auie.utils.UEException.UEImageNotByteException;
import org.auie.utils.UEString;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * 
 * 图片缓存类
 * 
 * 图片缓存管理，提供双缓存(软缓存与硬缓存)+外置存储，支持缓存本地文件URL与互联网URL
 * 
 * @author Soniy7x
 *
 */
public final class UEImageCacheManager {
	
	//软缓存
    private Map<String, SoftReference<Bitmap>> mImageCache; 
    //硬缓存
    private LruCache<String, Bitmap> mLruCache;
    //是否使用外置存储
    private boolean external = false;  
    //外置存储文件夹
    private String cachedDir;
      
    /**
     * 构造方法
     * @param imageCache 软缓存
     * @param mLruCache 硬缓存
     */
    public UEImageCacheManager(Map<String, SoftReference<Bitmap>> imageCache, LruCache<String, Bitmap> mLruCache){  
        this(imageCache, mLruCache, false, null);
    }  
    
    /**
     * 构造方法
     * @param imageCache 软缓存
     * @param mLruCache 硬缓存
     * @param external 是否使用外置存储
     * @param cachedDir 外置存储文件夹
     */
    public UEImageCacheManager(Map<String, SoftReference<Bitmap>> imageCache, LruCache<String, Bitmap> mLruCache, boolean external, String cachedDir){  
        this.mImageCache = imageCache;  
        this.mLruCache = mLruCache;
        this.external = external;
        this.cachedDir = cachedDir;
    }
    
    /**
     * 设置是否使用外置存储
     * @param external 是否使用外置存储
     * @param cacheDir 外置存储文件夹
     */
    public void setExternal(boolean external, String cacheDir){
    	this.external = external;
    	this.cachedDir = cacheDir;
    }
    
    /**
     * 缓存Bitmap图片
     * @param key 图片索引key值
     * @param bitmap 将要缓存的图片
     */
    public void putBitmap(String key, Bitmap bitmap){
    	if (bitmap == null) {
    		Log.w(UE.TAG, "图片为空，无法进行缓存");
			return;
		}
    	mImageCache.put(key, new SoftReference<Bitmap>(bitmap));
    	mLruCache.put(key, bitmap);
    	if (external) {
    		String fileName = UEString.encryptMD5(key);  
            String filePath = this.cachedDir + "/" +fileName;  
            FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(filePath);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); 
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				Log.w(UE.TAG, "图片缓存到SD卡失败");
			}
		}
    }
    
    /**
     * 从网络下载图片
     * @param url 图片地址
     * @param cache 是否缓存
     * @return Bitmap
     */
    public Bitmap getBitmapFromHttp(String url, boolean cache){  
        Bitmap bitmap = null;  
        try{  
            URL u = new URL(url);  
            HttpURLConnection conn = (HttpURLConnection)u.openConnection();    
            InputStream is = conn.getInputStream();  
            bitmap = BitmapFactory.decodeStream(is);
            if(cache){
                mImageCache.put(url, new SoftReference<Bitmap>(bitmap));  
                mLruCache.put(url, bitmap);
                if(external){
                    String fileName = UEString.encryptMD5(url);  
                    String filePath = this.cachedDir + "/" +fileName;  
                    FileOutputStream fos = new FileOutputStream(filePath);  
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); 
                    if (fos != null) {
    					fos.close();
    				}
                }  
            }
            is.close();  
            conn.disconnect();  
            return bitmap;  
        }catch(IOException e){   
            return null;  
        }
    }
    
    /**
     * 从本地文件下载图片
     * @param path 图片地址
     * @param cache 是否缓存
     * @return Bitmap
     */
    public Bitmap getBitmapFromFile(String path, boolean cache){  
        Bitmap bitmap = null;  
        try{   
            bitmap = new UEImage(path, true).toBitmap();
            if(cache){  
                mImageCache.put(path, new SoftReference<Bitmap>(bitmap));
                mLruCache.put(path, bitmap);
                if(external){
                    String fileName = UEString.encryptMD5(path);  
                    String filePath = this.cachedDir + "/" +fileName;  
                    FileOutputStream fos = new FileOutputStream(filePath);  
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);  
                    if (fos != null) {
    					fos.close();
    				}
                }  
            } 
            return bitmap;  
        }catch(IOException e){
            return null;  
        } catch (UEImageNotByteException e) {
			return null;  
		}  
    }
    
    /**
     * 从缓存中获取图片
     * @param url 图片地址
     * @return Bitmap
     */
    public Bitmap getBitmapFromMemory(String url){  
        Bitmap bitmap = null;  
        if(mImageCache.containsKey(url)){  
            synchronized(mImageCache){  
                SoftReference<Bitmap> bitmapRef = mImageCache.get(url);  
                if(bitmapRef != null){  
                    bitmap = bitmapRef.get();  
                    return bitmap;  
                }  
            }  
        }
        bitmap = mLruCache.get(url);
        if (bitmap != null) {
			return bitmap;
		}
        if(external){  
            bitmap = getBitmapFromExternal(url);  
            if(bitmap != null)  
                mImageCache.put(url, new SoftReference<Bitmap>(bitmap));  
        }
        return bitmap;  
    }  
    
    /**
     * 从外置存储中获取图片
     * @param url 图片地址
     * @return Bitmap
     */
    private Bitmap getBitmapFromExternal(String url){  
        Bitmap bitmap = null;  
        String fileName = UEString.encryptMD5(url);  
        if(fileName == null)  
            return null;  
        String filePath = cachedDir + "/" + fileName;   
        try {  
            FileInputStream fis = new FileInputStream(filePath);  
            bitmap = BitmapFactory.decodeStream(fis);  
        } catch (FileNotFoundException e) {  
            bitmap = null;  
        }  
        return bitmap;  
    }
}
