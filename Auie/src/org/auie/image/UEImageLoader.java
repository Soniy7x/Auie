package org.auie.image;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
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
 * 图片加载类(Image Load Class)
 * 
 * 提供从网络或文件加载并缓存图片
 * (Provide to load Image from network or file)
 * 
 * @author Soniy7x
 * 
 */
public final class UEImageLoader {

	//硬缓存大小(LruCache size)
	private static final int LRUCACHE_SIZE = 8 * 1024 * 1024;
	//下载中的URL集合(Downloading URL Collection)
    private static HashSet<String> mSet;
    //软缓存(SoftReference)
    private static Map<String,SoftReference<Bitmap>> mImageCache;   
    //硬缓存(LruCache)
    private static LruCache<String, Bitmap> mLruCache;
    //图片缓存管理类(Image Cache Manager)
    private static UEImageCacheManager cacheManager;  
    //线程池对象(ExecutorService)
    private static ExecutorService mExecutorService;
    private Handler mHandler;  
    
    private static UEImageLoader instance;
    
    /**
     * 静态对象初始化(Static Object initialization)
     */
    static{  
        mSet = new HashSet<String>();  
        mLruCache = new LruCache<String, Bitmap>(LRUCACHE_SIZE);
        mImageCache = new HashMap<String,SoftReference<Bitmap>>();  
        cacheManager = new UEImageCacheManager(mImageCache , mLruCache);  
    }  
  
    /**
     * 构造方法(Constructor)
     * @param context 上下文
     */
    private UEImageLoader(Context context){  
        mHandler = new Handler();
        startThreadPoolIfNecessary(); 
        setExternal(true, context.getCacheDir().getAbsolutePath());
    }  
    
    private UEImageLoader(Context context, boolean external){  
        mHandler = new Handler();
        startThreadPoolIfNecessary(); 
        setExternal(external, context.getCacheDir().getAbsolutePath());
    }
    
    public static UEImageLoader getInstance(Context context) {
		if (instance == null) {
			instance = new UEImageLoader(context);
		}
		return instance;
	}
    
    public static UEImageLoader getInstance(Context context, boolean external) {
		if (instance == null) {
			instance = new UEImageLoader(context, external);
		}
		return instance;
	}
    
    /**
     * 设置是否使用外置存储
     * (Set whether to use external storage, if external is true that cacheDir shouldn't null)
     * @param external 是否使用外置存储(Whether to use external storage)
     * @param cacheDir 外置存储文件夹(External storage folder)
     */
    public void setExternal(boolean external, String cacheDir){
    	cacheManager.setExternal(external, cacheDir);
    }
  
    /**
     * 开启线程池(start ThreadPool)
     */
    private static void startThreadPoolIfNecessary(){
        if(mExecutorService == null || mExecutorService.isShutdown() || mExecutorService.isTerminated()){  
            mExecutorService = Executors.newFixedThreadPool(3);
        }
    }  
    
    public Bitmap loadBitmap(String key){
    	if(TextUtils.isEmpty(key)){  
            Log.w(UE.TAG, "图片键值不得为空");  
            return null;
        }
    	Bitmap bitmap = cacheManager.getBitmapFromMemory(key);  
        if(bitmap != null){  
            return bitmap;
        }
        return null;
    }
    
    public void putBitmap(final String key, final Bitmap bitmap, boolean service){
    	if (!service) {
    		cacheManager.putBitmap(key, bitmap); 
		}else {
			mExecutorService.submit(new Runnable(){  
	            @Override  
	            public void run() {  
	                mHandler.post(new Runnable(){  
	                    @Override  
	                    public void run(){  
	                        cacheManager.putBitmap(key, bitmap); 
	                    }  
	                });  
	            }  
	        });
		}
    }
    
    public void putBitmap(String key, Drawable drawable, boolean service){
    	putBitmap(key, new UEImage(drawable).toBitmap(), service);
    }
    
    public void putBitmap(String key, UEImage image, boolean service){
    	putBitmap(key, image.toBitmap(), service);
    }
    
    public void putBitmap(String key, byte[] bs, boolean service) throws UEImageNotByteException{
    	putBitmap(key, new UEImage(bs).toBitmap(), service);
    }
    
    /**
     * 从网络下载图片并缓存(Download and Cache Image from HTTP)
     * @param url 图片地址(Image Address)
     * @param callback 回调方法(callback method)
     */
    public void loadBitmapByHttp(final String url, final OnUEImageLoadListener callback){  
    	loadBitmapByHttp(url, true, callback);  
    } 
    
    /**
     * 从网络下载图片(Download Image from HTTP)
     * @param url 图片地址(Image Address)
     * @param cache 是否缓存(Whether to cache)
     * @param callback 回调方法(callback method)
     */
    public void loadBitmapByHttp(final String url, final boolean cache, final OnUEImageLoadListener callback){  
        if(mSet.contains(url)){  
            Log.w(UE.TAG, "图片正在下载，不能重复下载");  
            return;
        }  
          
        Bitmap bitmap = cacheManager.getBitmapFromMemory(url);  
        if(bitmap != null){  
            if(callback != null){  
                callback.onImageLoadCompleted(bitmap, url);  
            }  
        }else{
            mSet.add(url);  
            mExecutorService.submit(new Runnable(){  
                @Override  
                public void run() {  
                    final Bitmap bitmap = cacheManager.getBitmapFromHttp(url, cache);  
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
     * 从文件下载图片并缓存(Download and Cache Image from File)
     * @param url 图片地址(Image Address)
     * @param callback 回调方法(callback method)
     */
    public void loadBitmapByFile(final String url, final OnUEImageLoadListener callback){  
    	loadBitmapByFile(url, true, callback);  
    } 
    
    /**
     * 从文件下载图片(Download Image from File)
     * @param url 图片地址(Image Address)
     * @param cache 是否缓存(Whether to cache)
     * @param callback 回调方法(callback method)
     */
    public void loadBitmapByFile(final String url, final boolean cache, final OnUEImageLoadListener callback){
    	if(mSet.contains(url)){  
            Log.w(UE.TAG, url + "图片正在读取，不能重复读取");  
            return;
        }
    	Bitmap bitmap = cacheManager.getBitmapFromMemory(url);  
        if(bitmap != null && callback != null){
        	callback.onImageLoadCompleted(bitmap, url);
        }else{  
        	mSet.add(url);  
            mExecutorService.submit(new Runnable(){  
                @Override  
                public void run() {  
					final Bitmap bitmap = cacheManager.getBitmapFromFile(url, cache);
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
     * 
     * 图片加载监听器(Image Load Listener)
     * 
     * @author Soniy7x
     */
    public interface OnUEImageLoadListener{ 
    	/**
    	 * 图片加载完成(Image load completed)
    	 * @param bitmap 加载到的图片(Is loaded into the Image)
    	 * @param imageUrl 图片地址(Image address)
    	 */
        public void onImageLoadCompleted(Bitmap bitmap, String imageUrl);  
    }  
      
}
