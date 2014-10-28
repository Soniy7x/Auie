package org.auie.http;

import org.apache.http.HttpEntity;

public class UEResponseHolder {

	private HttpEntity response;
	private Throwable exception;
	private UEResponseListener listener;

	public UEResponseHolder(HttpEntity response, UEResponseListener listener) {
		this.response = response;
		this.listener = listener;
	}

	public UEResponseHolder(Throwable exception, UEResponseListener listener) {
		this.exception = exception;
		this.listener = listener;
	}

	public HttpEntity getResponse() {
		return response;
	}

	public Throwable getException() {
		return exception;
	}

	public UEResponseListener getListener() {
		return listener;
	}

}
