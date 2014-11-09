package org.auie.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.auie.utils.UE;

import android.os.AsyncTask;
import android.util.Log;

public class UERequestTask extends AsyncTask<Void, Void, UEResponseHolder>{
	
	HttpEntity entity = null;
	DefaultHttpClient client = null;
	UERequestHolder request = null;
	
	public UERequestTask(UERequestHolder request, UEHttpClient client){
		this.request = request;
		this.client = client.getHttpClient();
	}
	
	@Override
	protected UEResponseHolder doInBackground(Void... params) {
		try {
			HttpResponse response = client.execute((HttpUriRequest) request.getRequest());
			StatusLine status = response.getStatusLine();
			Log.i(UE.TAG, "HTTP Requst status - " + status.getStatusCode());
			if(status.getStatusCode() > 300){
				return new UEResponseHolder(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), 
						request.getListener());
			}
			entity = response.getEntity();
			if(entity != null){
				entity = new BufferedHttpEntity(entity);
			}
		} catch (Exception e){
			Log.d(UE.TAG, e.toString());
			return new UEResponseHolder(e, request.getListener());
		}
		return new UEResponseHolder(entity, request.getListener());
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(UEResponseHolder result) {
		super.onPostExecute(result);
		
		if(isCancelled()){
			return;
		}
		
		UEResponseListener listener = result.getListener();
		HttpEntity response = result.getResponse();
		Throwable exception = result.getException();
		if(response!=null){
			listener.onResponseReceived(response);
		}else{
			listener.onResponseReceived(exception);
		}
		request.getListener().onResponseReceived(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		request.getListener().onResponseReceived(true);
	}

	
}
