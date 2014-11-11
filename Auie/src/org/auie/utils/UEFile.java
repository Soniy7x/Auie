package org.auie.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import android.content.Context;

public final class UEFile {
	
	private static DecimalFormat mDecimalFormat = new DecimalFormat("0.0");
	
	/**
	 * 清空当前程序缓存
	 * @param context
	 * @return
	 */
	public static boolean deleteCacheFile(Context context){
		try {
			return deleteFile(context.getCacheDir());
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 删除文件或文件夹
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static boolean deleteFile(File file) throws FileNotFoundException{
		if (!file.exists() || file == null) {
			throw new FileNotFoundException();
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deleteFile(f);
			}
			return file.delete();
		}else {
			return file.delete();
		}
	}
	
	/**
	 * 获取当前程序缓存文件大小
	 * @param context
	 * @return
	 */
	public static String getCacheFileSize(Context context){
		try {
			return getFileAutoSize(context.getCacheDir());
		} catch (FileNotFoundException e) {
			return "";
		}
	}
	
	/**
	 * 获取文件大小自动转化最优单位
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getFileAutoSize(File file) throws FileNotFoundException{
		long size = getFileSize(file);
		if (size < 1024) {
			return size + "B";
		}else if (size < Math.pow(1024, 2)) {
			return mDecimalFormat.format(size / 1024) + "K";
		}else if (size < Math.pow(1024, 3)) {
			return mDecimalFormat.format(size / Math.pow(1024, 2)) + "M";
		}else {
			return mDecimalFormat.format(size / Math.pow(1024, 3)) + "G";
		}
	}
	
	/**
	 * 获取文件大小且指定单位
	 * @param file
	 * @param unit
	 * @return 
	 * @throws FileNotFoundException
	 */
	public static String getFileSize(File file, Size unit) throws FileNotFoundException{
		long size = getFileSize(file);
		if (unit == Size.K || unit == Size.KB) {
			return size / 1024 + unit.name;
		}else if (unit == Size.M || unit == Size.MB) {
			return size / Math.pow(1024, 2) + unit.name;
		}else if (unit == Size.G || unit == Size.GB) {
			return size / Math.pow(1024, 3) + unit.name;
		}else {
			return size + "B";
		}
	}
	
	/**
	 * 获取文件大小
	 * @param file
	 * @return B
	 * @throws FileNotFoundException
	 */
	public static long getFileSize(File file) throws FileNotFoundException{
		if (!file.exists() || file == null) {
			throw new FileNotFoundException();
		}
		long size = 0;
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				size += getFileSize(f);
			}else {
				size += f.length();
			}
		}
		return size;
	}

	public enum Size{
		
		B("B"), K("K"), M("M"), G("G"), KB("KB"), MB("MB"), GB("GB");
		
		public String name;
		
		private Size(String name){
			this.name = name;
		}
	}
}
