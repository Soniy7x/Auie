package org.auie.image;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.auie.utils.UE;
import org.auie.utils.UEException.UEImageNotByteException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * 图片加载类
 * 
 * 提供从网络或文件加载并缓存图片
 * 
 * @author Soniy7x
 * 
 */
public final class UEImageLoader {

	//硬缓存大小
	private static final int LRUCACHE_SIZE = 8 * 1024 * 1024;
	//下载中的URL集合
    private static HashSet<String> mSet;
    //软缓存
    private static Map<String,SoftReference<Bitmap>> mImageCache;   
    //硬缓存
    private static LruCache<String, Bitmap> mLruCache;
    //图片缓存管理类
    private static UEImageCacheManager mCacheManager;  
    //线程池对象
    private static ExecutorService mExecutorService;
    private static Map<String, List<OnUEImageLoadListener>> mWaitor;
    private Handler mHandler;  
    
    private static UEImageLoader instance;
    
    /**
     * 静态对象初始化
     */
    static{  
        mSet = new HashSet<String>();  
        mLruCache = new LruCache<String, Bitmap>(LRUCACHE_SIZE);
        mImageCache = new HashMap<String,SoftReference<Bitmap>>();  
        mCacheManager = new UEImageCacheManager(mImageCache , mLruCache);  
        mWaitor = new HashMap<String, List<OnUEImageLoadListener>>();
    }  
  
    /**
     * 构造方法
     */
    private UEImageLoader(Context context){  
        mHandler = new Handler();
        startThreadPoolIfNecessary(); 
        setExternal(true, context.getCacheDir().getAbsolutePath());
    }  
    
    /**
     * 构造方法
     */
    private UEImageLoader(Context context, boolean external){  
        mHandler = new Handler();
        startThreadPoolIfNecessary(); 
        setExternal(external, context.getCacheDir().getAbsolutePath());
    }
    
    /**
     * 获取单一实例并使用外置存储
     * @param context
     */
    public static UEImageLoader getInstance(Context context) {
		if (instance == null) {
			instance = new UEImageLoader(context);
		}
		return instance;
	}
    
    /**
     * 获取单一实例
     * @param context
     * @param external 是否使用外置存储
     * @return
     */
    public static UEImageLoader getInstance(Context context, boolean external) {
		if (instance == null) {
			instance = new UEImageLoader(context, external);
		}
		return instance;
	}
    
    /**
     * 设置是否使用外置存储
     * @param external 是否使用外置存储
     * @param cacheDir 外置存储文件夹
     */
    public void setExternal(boolean external, String cacheDir){
    	mCacheManager.setExternal(external, cacheDir);
    }
  
    /**
     * 开启线程池
     */
    private static void startThreadPoolIfNecessary(){
        if(mExecutorService == null || mExecutorService.isShutdown() || mExecutorService.isTerminated()){  
            mExecutorService = Executors.newFixedThreadPool(3);
        }
    }  
    
    /**
     * 根据键值加载图片
     * @param key 唯一键值
     * @return
     */
    public Bitmap loadBitmap(String key){
    	if(TextUtils.isEmpty(key)){  
            Log.w(UE.TAG, "图片键值不得为空");  
            return null;
        }
    	Bitmap bitmap = mCacheManager.getBitmapFromMemory(key);  
        if(bitmap != null){  
            return bitmap;
        }
        return null;
    }
    
    /**
     * 缓存Bitmap图片
     * @param key 唯一键值
     * @param bitmap Bitmap对象
     * @param service 是否使用线程池
     */
    public void putBitmap(final String key, final Bitmap bitmap, boolean service){
    	if (!service) {
    		mCacheManager.putBitmap(key, bitmap); 
		}else {
			mExecutorService.submit(new Runnable(){  
	            @Override  
	            public void run() {  
	                mHandler.post(new Runnable(){  
	                    @Override  
	                    public void run(){  
	                        mCacheManager.putBitmap(key, bitmap); 
	                    }  
	                });  
	            }  
	        });
		}
    }
    
    /**
     * 缓存Drawable图片
     * @param key 唯一键值
     * @param drawable Drawable对象
     * @param service 是否使用线程池
     */
    public void putBitmap(String key, Drawable drawable, boolean service){
    	putBitmap(key, new UEImage(drawable).toBitmap(), service);
    }
    
    /**
     * 缓存UEImage图片
     * @param key 唯一键值
     * @param image UEImage对象
     * @param service 是否使用线程池
     */
    public void putBitmap(String key, UEImage image, boolean service){
    	putBitmap(key, image.toBitmap(), service);
    }
    
    /**
     * 缓存字节格式图片
     * @param key 唯一键值
     * @param bs 图片字节
     * @param service 是否使用线程池
     * @throws UEImageNotByteException 字节无法转化为图片异常
     */
    public void putBitmap(String key, byte[] bs, boolean service) throws UEImageNotByteException{
    	putBitmap(key, new UEImage(bs).toBitmap(), service);
    }
    
    /**
     * 从网络加载图片并缓存
     * @param url 图片地址
     * @param callback 回调方法
     */
    public void loadBitmapByHttp(final String url, final OnUEImageLoadListener callback){  
    	loadBitmapByHttp(url, true, callback);  
    } 
    
    /**
     * 从网络加载图片
     * @param url 图片地址
     * @param cache 是否缓存
     * @param callback 回调方法
     */
    public void loadBitmapByHttp(final String url, final boolean cache, final OnUEImageLoadListener callback){  
    	if(mSet.contains(url)){  
    		Log.w(UE.TAG, "图片正在下载，不能重复下载");  
    		return;
    	}
        Bitmap bitmap = mCacheManager.getBitmapFromMemory(url);  
        if(bitmap != null){  
            if(callback != null){  
                callback.onImageLoadCompleted(bitmap, url);  
            }  
        }else{
            mSet.add(url);  
            mExecutorService.submit(new Runnable(){  
                @Override  
                public void run() {  
                    final Bitmap bitmap = mCacheManager.getBitmapFromHttp(url, cache);  
                    mHandler.post(new Runnable(){  
                        @Override  
                        public void run(){  
                            if(callback != null)  
                                callback.onImageLoadCompleted(bitmap, url);  
                            mSet.remove(url);  
                        }  
                    });  
                }  
            });  
        }  
    }
    
    /**
     * 从网络加载图片且不过滤相同URL
     * @param url 图片地址
     * @param callback 回调方法
     */
    public void loadBitmapByHttpNoCheck(final String url, final OnUEImageLoadListener callback){ 
    	loadBitmapByHttpNoCheck(url, true, callback);
    }
    
    /**
     * 从网络加载图片且不过滤相同URL
     * @param url 图片地址
     * @param cache 是否缓存
     * @param callback 回调方法
     */
    public void loadBitmapByHttpNoCheck(final String url, final boolean cache, final OnUEImageLoadListener callback){  
        Bitmap bitmap = mCacheManager.getBitmapFromMemory(url);  
        if(bitmap != null){  
            if(callback != null){  
                callback.onImageLoadCompleted(bitmap, url);  
            }  
        }else{
        	if(mSet.contains(url)){  
        		if (mWaitor.containsKey(url)) {
					mWaitor.get(url).add(callback);
				}else {
					List<OnUEImageLoadListener> callbacks = new ArrayList<OnUEImageLoadListener>();
					callbacks.add(callback);
					mWaitor.put(url, callbacks);
				}
        		return;
        	}  
            mSet.add(url);  
            mExecutorService.submit(new Runnable(){  
                @Override  
                public void run() {  
                    final Bitmap bitmap = mCacheManager.getBitmapFromHttp(url, cache);  
                    mHandler.post(new Runnable(){  
                        @Override  
                        public void run(){  
                            if(callback != null)  
                                callback.onImageLoadCompleted(bitmap, url);  
                            mSet.remove(url); 
                            if (mWaitor.containsKey(url)) {
                            	List<OnUEImageLoadListener> callbacks = mWaitor.get(url);
                            	for (OnUEImageLoadListener callback : callbacks) {
									callback.onImageLoadCompleted(bitmap, url);
								}
							}
                        }  
                    });  
                }  
            });  
        }  
    }
    
    /**
     * 从文件加载图片并缓存
     * @param url 图片地址
     * @param callback 回调方法
     */
    public void loadBitmapByFile(final String url, final OnUEImageLoadListener callback){  
    	loadBitmapByFile(url, true, callback);  
    } 
    
    /**
     * 从文件加载图片
     * @param url 图片地址
     * @param cache 是否缓存
     * @param callback 回调方法
     */
    public void loadBitmapByFile(final String url, final boolean cache, final OnUEImageLoadListener callback){
    	if(mSet.contains(url)){  
            Log.w(UE.TAG, url + "图片正在读取，不能重复读取");  
            return;
        }
    	Bitmap bitmap = mCacheManager.getBitmapFromMemory(url);  
        if(bitmap != null && callback != null){
        	callback.onImageLoadCompleted(bitmap, url);
        }else{  
        	mSet.add(url);  
            mExecutorService.submit(new Runnable(){  
                @Override  
                public void run() {  
					final Bitmap bitmap = mCacheManager.getBitmapFromFile(url, cache);
					mHandler.post(new Runnable(){  
						@Override  
						public void run(){  
							if(callback != null)  
								callback.onImageLoadCompleted(bitmap, url);
							mSet.remove(url);  
						}  
					});  
                }  
            });  
        }  
    }
    
    /**
     * 从文件加载图片并缓存
     * @param url 图片地址
     * @param callback 回调方法
     */
    public void loadBitmapByFileNoCheck(final String url, final OnUEImageLoadListener callback){  
    	loadBitmapByFileNoCheck(url, true, callback);  
    } 
    
    /**
     * 从文件加载图片
     * @param url 图片地址
     * @param cache 是否缓存
     * @param callback 回调方法
     */
    public void loadBitmapByFileNoCheck(final String url, final boolean cache, final OnUEImageLoadListener callback){
    	Bitmap bitmap = mCacheManager.getBitmapFromMemory(url);  
        if(bitmap != null && callback != null){
        	callback.onImageLoadCompleted(bitmap, url);
        }else{  
        	if(mSet.contains(url)){  
        		if (mWaitor.containsKey(url)) {
					mWaitor.get(url).add(callback);
				}else {
					List<OnUEImageLoadListener> callbacks = new ArrayList<OnUEImageLoadListener>();
					callbacks.add(callback);
					mWaitor.put(url, callbacks);
				}
        		return;
        	} 
        	mSet.add(url);  
            mExecutorService.submit(new Runnable(){  
                @Override  
                public void run() {  
					final Bitmap bitmap = mCacheManager.getBitmapFromFile(url, cache);
					mHandler.post(new Runnable(){  
						@Override  
						public void run(){  
							if(callback != null)  
								callback.onImageLoadCompleted(bitmap, url);
							mSet.remove(url);  
							if (mWaitor.containsKey(url)) {
                            	List<OnUEImageLoadListener> callbacks = mWaitor.get(url);
                            	for (OnUEImageLoadListener callback : callbacks) {
									callback.onImageLoadCompleted(bitmap, url);
								}
							}
						}  
					});  
                }  
            });  
        }  
    }
    
    /**
     * 图片加载监听器
     */
    public interface OnUEImageLoadListener{ 
    	/**
    	 * 图片加载完成
    	 * @param bitmap 加载到的图片
    	 * @param imageUrl 图片地址
    	 */
        public void onImageLoadCompleted(Bitmap bitmap, String imageUrl);  
    }  
      
}
