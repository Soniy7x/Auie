package org.auie.http;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.message.BasicNameValuePair;

import android.util.Base64;

public class UEHttpParams{

	private ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<String, String>();
	
	public UEHttpParams(){};
	
	public String toString(){
		StringBuffer paramsStr = new StringBuffer();
		for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()){
			if(paramsStr.length() > 0){
				paramsStr.append("&");
			}
			paramsStr.append(entry.getKey());
			paramsStr.append("=");
			paramsStr.append(entry.getValue());
		}
		return paramsStr.toString();
	}
	
	public List<BasicNameValuePair> getParams(){
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()){
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return params;
	}
	
	public void put(String key, int value){
		urlParams.put(key, String.valueOf(value));
	}
	
	public void put(String key, short value){
		urlParams.put(key, String.valueOf(value));
	}
	
	public void put(String key, long value){
		urlParams.put(key, String.valueOf(value));
	}
	
	public void put(String key, float value){
		urlParams.put(key, String.valueOf(value));
	}
	
	public void put(String key, double value){
		urlParams.put(key, String.valueOf(value));
	}
	
	public void put(String key, boolean value){
		urlParams.put(key, String.valueOf(value));
	}
	
	public void put(String key, char value){
		urlParams.put(key, String.valueOf(value));
	}
	
	public void put(String key, String value){
		urlParams.put(key, value);
	}
	
	public void put(String key, byte[] value) throws UnsupportedEncodingException{
		String valueString = Base64.encodeToString(value, Base64.DEFAULT);
		urlParams.put(key, valueString);
	}
}
