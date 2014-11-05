package org.auie.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.auie.utils.UETag;

import android.content.Context;
import android.util.Log;

public class UERestHttpClient extends UEHttpClient{


	public void put(Context context, String url, UEHttpQueryParams params, UEHttpListener handler){
		sendRequest(context, getPutRequest(url, params), handler);
	}
	
	public void putt(Context context, String url, UEHttpQueryParams params, UEHttpListener handler, int connectionTime, int socketTime){
		sendRequest(context, getPutRequest(url, params), handler, connectionTime, socketTime);
	}
	
	public void delete(Context context, String url, UEHttpQueryParams params, UEHttpListener listener){
		sendRequest(context, getDeleteRequest(url, params), listener);
	}
	
	public void delete(Context context, String url, UEHttpQueryParams params, UEHttpListener handler, int connectionTime, int socketTime){
		sendRequest(context, getDeleteRequest(url, params), handler, connectionTime, socketTime);
	}
	
	private HttpPut getPutRequest(String url, UEHttpQueryParams params){
		if(params == null){
			return new HttpPut(url);
		}
		HttpPut put = null;
		if(url.length() > 0){
			put = new HttpPut(url);
			try {
				put.setEntity(new UrlEncodedFormEntity(params.getParams(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				Log.d(UETag.TAG, e.toString());
			}
		}
		return put;
	}
	
	private HttpDelete getDeleteRequest(String url, UEHttpQueryParams params){
		HttpDelete delete = null;
		if(params == null){
			delete = new HttpDelete(url);
		}else{	
			delete = new HttpDelete(url+"?"+URLEncodedUtils.format(params.getParams(), "UTF-8"));
		}
		return delete;
	}
}
