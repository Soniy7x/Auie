package org.auie.image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UEImageCacheManager {
	
    private Map<String, SoftReference<Bitmap>> mImageCache;  
    private boolean isExternal = false;  
    private String cachedDir;  
      
    public UEImageCacheManager(Map<String, SoftReference<Bitmap>> imageCache){  
        this.mImageCache = imageCache;  
    }  
         
    public Bitmap getBitmapFromHttp(String url, boolean cache2Memory){  
        Bitmap bitmap = null;  
        try{  
            URL u = new URL(url);  
            HttpURLConnection conn = (HttpURLConnection)u.openConnection();    
            InputStream is = conn.getInputStream();  
            bitmap = BitmapFactory.decodeStream(is);
            if(cache2Memory){  
                mImageCache.put(url, new SoftReference<Bitmap>(bitmap));  
                if(isExternal){
                    String fileName = getMD5Str(url);  
                    String filePath = this.cachedDir + "/" +fileName;  
                    FileOutputStream fos = new FileOutputStream(filePath);  
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);  
                }  
            }
            is.close();  
            conn.disconnect();  
            return bitmap;  
        }catch(IOException e){  
            e.printStackTrace();  
            return null;  
        }  
    }
    
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
        if(isExternal){  
            bitmap = getBitmapFromFile(url);  
            if(bitmap != null)  
                mImageCache.put(url, new SoftReference<Bitmap>(bitmap));  
        }
        return bitmap;  
    }  
      
    private Bitmap getBitmapFromFile(String url){  
        Bitmap bitmap = null;  
        String fileName = getMD5Str(url);  
        if(fileName == null)  
            return null;  
        String filePath = cachedDir + "/" + fileName;   
        try {  
            FileInputStream fis = new FileInputStream(filePath);  
            bitmap = BitmapFactory.decodeStream(fis);  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            bitmap = null;  
        }  
        return bitmap;  
    }  
          
    private static String getMD5Str(String str) {     
        MessageDigest messageDigest = null;     
        try {     
            messageDigest = MessageDigest.getInstance("MD5");     
            messageDigest.reset();     
            messageDigest.update(str.getBytes("UTF-8"));     
        } catch (NoSuchAlgorithmException e) {   
            return null;  
        } catch (UnsupportedEncodingException e) {
            return null;  
        }     
     
        byte[] byteArray = messageDigest.digest();     
        StringBuffer md5StrBuff = new StringBuffer();     
        for (int i = 0; i < byteArray.length; i++) {                 
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)     
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));     
            else     
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));     
        }     
     
        return md5StrBuff.toString();     
    } 
    
    
    public void setCache2File(boolean flag){  
        isExternal = flag;  
    }  
       
    public void setCachedDir(String cacheDir){  
        this.cachedDir = cacheDir;  
    } 
}
