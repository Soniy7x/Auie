package org.auie.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpRequest;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.auie.utils.UE;

import android.util.Log;

public class UEHttpClient {

	protected static final int SOCKET_TIMEOUT = 5 * 1000;
	protected static final int CONNECTION_TIMEOUT = 5 * 1000;

	private static UEHttpClient instance = null;
	private static DefaultHttpClient httpClient = null;
	
	private UEHttpClient(){}
	
	public static UEHttpClient getInstance(){
		if (instance == null) {
			instance = new UEHttpClient();
		}
		return instance;
	}
	
	
	public void get(String url, UEHttpParams params, UEHttpListener listener){
		sendRequest(getGetRequest(url, params), listener);
	}
	
	public void get(String url, UEHttpParams params, UEHttpListener handler, int connectionTime, int socketTime){
		sendRequest(getGetRequest(url, params), handler, connectionTime, socketTime);
	}
	
	public void post(String url, UEHttpParams params, UEHttpListener handler){
		sendRequest(getPostRequest(url, params), handler);
	}
	
	public void post(String url, UEHttpParams params, UEHttpListener handler, int connectionTime, int socketTime){
		sendRequest(getPostRequest(url, params), handler, connectionTime, socketTime);
	}
	
	public void put(String url, UEHttpParams params, UEHttpListener handler){
		sendRequest(getPutRequest(url, params), handler);
	}
	
	public void put(String url, UEHttpParams params, UEHttpListener handler, int connectionTime, int socketTime){
		sendRequest(getPutRequest(url, params), handler, connectionTime, socketTime);
	}
	
	public void delete(String url, UEHttpParams params, UEHttpListener listener){
		sendRequest(getDeleteRequest(url, params), listener);
	}
	
	public void delete(String url, UEHttpParams params, UEHttpListener handler, int connectionTime, int socketTime){
		sendRequest(getDeleteRequest(url, params), handler, connectionTime, socketTime);
	}
	
	private HttpPut getPutRequest(String url, UEHttpParams params){
		if(params == null){
			return new HttpPut(url);
		}
		HttpPut put = null;
		if(url.length() > 0){
			put = new HttpPut(url);
			try {
				put.setEntity(new UrlEncodedFormEntity(params.getParams(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				Log.d(UE.TAG, e.toString());
			}
		}
		return put;
	}
	
	private HttpDelete getDeleteRequest(String url, UEHttpParams params){
		HttpDelete delete = null;
		if(params == null){
			delete = new HttpDelete(url);
		}else{	
			delete = new HttpDelete(url+"?"+URLEncodedUtils.format(params.getParams(), "UTF-8"));
		}
		return delete;
	}
	
	protected void sendRequest(HttpRequest params, UEHttpListener handler){
		sendRequest(params, handler, CONNECTION_TIMEOUT, SOCKET_TIMEOUT);
	}
	
	protected void sendRequest(HttpRequest params, UEHttpListener handler, int connectionTime, int socketTime){
		HttpParams httpParams = params.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, connectionTime);
		HttpConnectionParams.setSoTimeout(httpParams, socketTime);
		params.setParams(httpParams);
		UERequestHolder request = new UERequestHolder(params, handler);
		UERequestTask task = new UERequestTask(request, this);
		task.execute();
	}
	
	private HttpGet getGetRequest(String url, UEHttpParams params){
		HttpGet get = null;
		if(params == null){
			get = new HttpGet(url);
		}else{	
			get = new HttpGet(url+"?"+URLEncodedUtils.format(params.getParams(), "UTF-8"));
		}
		return get;
	}
	
	private HttpPost getPostRequest(String url, UEHttpParams params){
		if(params == null){
			return new HttpPost(url);
		}
		HttpPost post = null;
		if(url.length() > 0){
			post = new HttpPost(url);
			try {
				post.setEntity(new UrlEncodedFormEntity(params.getParams(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				Log.d(UE.TAG, e.toString());
			}
		}
		return post;
	}
	
	public synchronized DefaultHttpClient getHttpClient(){
		if(httpClient == null){
			final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
			final PlainSocketFactory plainSocketFactory = PlainSocketFactory.getSocketFactory();
			BasicHttpParams params = new BasicHttpParams();
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", plainSocketFactory, 80));
			schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
			
			HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
			
			ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
			httpClient = new DefaultHttpClient(manager, params);
		}
		return httpClient;
	}

}
