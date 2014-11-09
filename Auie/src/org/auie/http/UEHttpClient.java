package org.auie.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpRequest;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import org.auie.utils.UE;

import android.content.Context;
import android.util.Log;

public class UEHttpClient {

	protected static final int SOCKET_TIMEOUT = 5 * 1000;
	protected static final int CONNECTION_TIMEOUT = 5 * 1000;

	private DefaultHttpClient httpClient = null;
	
	public void get(Context context, String url, UEHttpQueryParams params, UEHttpListener listener){
		sendRequest(context, getGetRequest(url, params), listener);
	}
	
	public void get(Context context, String url, UEHttpQueryParams params, UEHttpListener handler, int connectionTime, int socketTime){
		sendRequest(context, getGetRequest(url, params), handler, connectionTime, socketTime);
	}
	
	public void post(Context context, String url, UEHttpQueryParams params, UEHttpListener handler){
		sendRequest(context, getPostRequest(url, params), handler);
	}
	
	public void post(Context context, String url, UEHttpQueryParams params, UEHttpListener handler, int connectionTime, int socketTime){
		sendRequest(context, getPostRequest(url, params), handler, connectionTime, socketTime);
	}
	
	protected void sendRequest(Context context, HttpRequest params, UEHttpListener handler){
		sendRequest(context, params, handler, CONNECTION_TIMEOUT, SOCKET_TIMEOUT);
	}
	
	protected void sendRequest(Context context, HttpRequest params, UEHttpListener handler, int connectionTime, int socketTime){
		UERequestHolder request = new UERequestHolder(params, handler);
		UERequestTask task = new UERequestTask(request, this);
		task.execute();
	}
	
	private HttpGet getGetRequest(String url, UEHttpQueryParams params){
		HttpGet get = null;
		if(params == null){
			get = new HttpGet(url);
		}else{	
			get = new HttpGet(url+"?"+URLEncodedUtils.format(params.getParams(), "UTF-8"));
		}
		return get;
	}
	
	private HttpPost getPostRequest(String url, UEHttpQueryParams params){
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
