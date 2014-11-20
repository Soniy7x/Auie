package org.auie.utils;

public final class UEException {
	
	public static class UEImageNotByteException extends Throwable {

		private static final long serialVersionUID = 1L;

		public UEImageNotByteException() {
	        super();
	    }
	    
	    public UEImageNotByteException(String msg) {
	        super(msg);
	    }
	    
	    public UEImageNotByteException(String msg, Throwable cause) {
	        super(msg, cause);
	    }
	    
	    public UEImageNotByteException(Throwable cause) {
	        super(cause);
	    }
	}

	public static class UEIndexNotFoundException extends Throwable {

		private static final long serialVersionUID = 1L;

		public UEIndexNotFoundException() {
	        super();
	    }
	    
	    public UEIndexNotFoundException(String msg) {
	        super(msg);
	    }
	    
	    public UEIndexNotFoundException(String msg, Throwable cause) {
	        super(msg, cause);
	    }
	    
	    public UEIndexNotFoundException(Throwable cause) {
	        super(cause);
	    }
	}

	public static class UEJSONTranslateException extends Throwable {

		private static final long serialVersionUID = 1L;

		public UEJSONTranslateException() {
	        super();
	    }
	    
	    public UEJSONTranslateException(String msg) {
	        super(msg);
	    }
	    
	    public UEJSONTranslateException(String msg, Throwable cause) {
	        super(msg, cause);
	    }
	    
	    public UEJSONTranslateException(Throwable cause) {
	        super(cause);
	    }
	}

}
