package org.auie.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.auie.utils.UE;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;

public class UEImageManager {

	private static final String[] THUMBNAILS_PROJECTTION = { Thumbnails.IMAGE_ID, Thumbnails.DATA };
	private static final String[] BOCKETS_PROJECTION = { Media._ID, Media.BUCKET_ID, Media.DATA, Media.BUCKET_DISPLAY_NAME };
	private static final String[] ALBUMS_PROJECTION = { Albums._ID, Albums.ALBUM, Albums.ALBUM_ART, Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS };
	private static final String[] IMAGES_PROJECTTION = { Media._ID, Media.DATA };
	
	private ContentResolver mContentResolver;
	private boolean hasCreatedBuckets = false;
	private Map<String, String> thumbnails = new HashMap<String, String>();
	private Map<String, Bucket> buckets = new HashMap<String, Bucket>();
	private List<Bucket> tempbuckets = new ArrayList<Bucket>();
	private List<Map<String, String>> albums = new ArrayList<Map<String, String>>();
	
	private static UEImageManager instance;
	
	private UEImageManager(){
		
	}
	
	private UEImageManager(Context context){
		mContentResolver = context.getContentResolver();
		instance = new UEImageManager();
	}
	
	public static UEImageManager getInstance(Context context){
		if (instance == null) {
			instance = new UEImageManager(context);
		}
		return instance;
	}
	
	/**
	 * 创建图片缩略图集合
	 */
	private void createThumbnails(){
		Cursor cursor = mContentResolver.query(Thumbnails.EXTERNAL_CONTENT_URI, THUMBNAILS_PROJECTTION, null, null, null);
		if (cursor.moveToFirst()) {
			int id;
			String data;
			int idIndex  = cursor.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataIndex = cursor.getColumnIndex(Thumbnails.DATA);
			thumbnails.clear();
			do{
				id = cursor.getInt(idIndex );
				data = cursor.getString(dataIndex);
				thumbnails.put(String.valueOf(id), data);
			}while(cursor.moveToNext());
		}
		cursor.close();
	}
	
	/**
	 * 创建图片原图集合
	 */
	private void createAlbums(){
		Cursor cursor = mContentResolver.query(Media.EXTERNAL_CONTENT_URI, ALBUMS_PROJECTION, null, null, null);
		if (cursor.moveToFirst()) {
			int idIndex = cursor.getColumnIndex(Albums._ID);
			int albumIndex  = cursor.getColumnIndex(Albums.ALBUM);
			int albumArtIndex  = cursor.getColumnIndex(Albums.ALBUM_ART);
			int albumKeyIndex  = cursor.getColumnIndex(Albums.ALBUM_KEY);
			int artistIndex  = cursor.getColumnIndex(Albums.ARTIST);
			int numberOfSongsIndex  = cursor.getColumnIndex(Albums.NUMBER_OF_SONGS);
			do {
				int id = cursor.getInt(idIndex);
				int numberOfSongs = cursor.getInt(numberOfSongsIndex);
				String album = cursor.getString(albumIndex);
				String artist = cursor.getString(artistIndex);
				String albumKey = cursor.getString(albumKeyIndex);
				String albumArt = cursor.getString(albumArtIndex);
				Map<String, String> albumItem = new HashMap<String, String>();
				albumItem.put("_id", String.valueOf(id));
				albumItem.put("album", album);
				albumItem.put("albumArt", albumArt);
				albumItem.put("albumKey", albumKey);
				albumItem.put("artist", artist);
				albumItem.put("numOfSongs", String.valueOf(numberOfSongs));
				albums.add(albumItem);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}
	
	/**
	 * 创建相册集合
	 */
	private void createBuckets(){
		createThumbnails();
		Cursor cursor = mContentResolver.query(Media.EXTERNAL_CONTENT_URI, BOCKETS_PROJECTION, null, null, null);
		if (cursor.moveToFirst()) {
			int idIndex = cursor.getColumnIndex(Media._ID);
			int dataIndex = cursor.getColumnIndex(Media.DATA);
			int bucketDisplatNameIndex = cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cursor.getColumnIndex(Media.BUCKET_ID);
			buckets.clear();
			tempbuckets.clear();
			do {
				String id = cursor.getString(idIndex);
				String data = cursor.getString(dataIndex);
				String bucketDisplayName = cursor.getString(bucketDisplatNameIndex);
				String bucketId = cursor.getString(bucketIdIndex);
				Bucket bucket = buckets.get(bucketId);
				if (bucket == null) {
					bucket = new Bucket();
					bucket.name = bucketDisplayName;
					buckets.put(bucketId, bucket);
					tempbuckets.add(bucket);
				}
				Image image = new Image();
				image.id = id;
				image.path = data;
				image.thumbnail = thumbnails.get(id);
				bucket.add(image);
			} while (cursor.moveToNext());
			hasCreatedBuckets = true;
		}
		cursor.close();
	}
	
	/**
	 * 获取相册缓存集合
	 * @return 
	 */
	public List<Bucket> getTempBuckets(boolean refresh){
		if (refresh || (!refresh && !hasCreatedBuckets)) {
			createBuckets();
		}
		return tempbuckets;
	}
	
	/**
	 * 获取原图集合
	 */
	public List<Map<String, String>> getAlbums(boolean refresh){
		if (refresh) {
			createAlbums();
		}
		return albums;
	}
	
	/**
	 * 获取图片原始路径
	 */
	public String getOriginalImagePath(String id) {
		Cursor cursor = mContentResolver.query(Media.EXTERNAL_CONTENT_URI, IMAGES_PROJECTTION, Media._ID + "=" + id, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(Media.DATA));
		}
		return null;
	}
	
	/**
	 * 相册/图片集合
	 */
	public class Bucket{
		public String name;
		public int count = 0;
		public List<Image> images = new ArrayList<Image>();
		
		public void add(Image image){
			images.add(image);
			count++;
		}
	}
	
	/**
	 * 图片
	 */
	@SuppressWarnings("serial")
	public static class Image extends UE{
		
		public String id;
		public String path;
		public String thumbnail;
		
	}
}
