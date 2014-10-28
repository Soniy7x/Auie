package org.auie.http;

import org.apache.http.HttpRequest;

public class UERequestHolder {

	private HttpRequest request;
	private UEHttpListener listener;

	public UERequestHolder(HttpRequest request, UEHttpListener listener) {
		this.request = request;
		this.listener = listener;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public UEHttpListener getListener() {
		return listener;
	}

}
