package org.auie.http;

import org.apache.http.HttpEntity;

public interface UEResponseListener {

	public void onResponseReceived(HttpEntity response);

	public void onResponseReceived(Throwable response);

	public void onResponseReceived(boolean b);
}
