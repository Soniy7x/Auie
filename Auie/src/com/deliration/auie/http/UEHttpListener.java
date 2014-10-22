package com.deliration.auie.http;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public abstract class UEHttpListener implements UEResponseListener{
	
	public static final int RESPONSE_TYPE_STRING = 1;
	public static final int RESPONSE_TYPE_STREAM = 2;
	public static final int RESPONSE_TYPE_JSON_ARRAY = 3;
	public static final int RESPONSE_TYPE_JSON_OBJECT = 4;
	
	private int responseType;
	
	public UEHttpListener(){
		this.responseType = RESPONSE_TYPE_STRING;
	}
	
	public UEHttpListener(int responseType){
		this.responseType = responseType;
	}

	protected void onSuccess(JSONArray response){};
	
	protected void onSuccess(JSONObject response){};
	
	protected void onSuccess(InputStream response){};
	
	protected abstract void onSuccess(String response);

	protected abstract void onFailure(Throwable e);
	
	protected void onStart(){};
	
	protected void onFinish(){}
	
	@Override
	public void onResponseReceived(HttpEntity response) {
		try{
			String responseBody = null;
			switch (this.responseType) {
			case RESPONSE_TYPE_STREAM:
				onSuccess(response.getContent());
				break;
			case RESPONSE_TYPE_JSON_ARRAY:
				responseBody = EntityUtils.toString(response);
				JSONArray jsonArray = null;
				if(responseBody!=null && responseBody.trim().length()>0){
					jsonArray = (JSONArray) new JSONTokener(responseBody).nextValue();
				}
				onSuccess(jsonArray);
				break;
			case RESPONSE_TYPE_JSON_OBJECT:
				responseBody = EntityUtils.toString(response);	
	        	JSONObject jsonObject = null;
	        	if(responseBody!=null && responseBody.trim().length()>0){
	        		jsonObject = (JSONObject) new JSONTokener(responseBody).nextValue();
	        	}
	    		onSuccess(jsonObject);
				break;
				
			default:
				responseBody = EntityUtils.toString(response);
	        	onSuccess(responseBody);
				break;
			}
		}catch(Exception e){
			onFailure(e);
		}
	}

	@Override
	public void onResponseReceived(Throwable response) {
		onFailure(response);
	}

	@Override
	public void onResponseReceived(boolean b) {
		if(b){
			onStart();
		}else{
			onFinish();
		}
	}
	
}
