package org.auie.utils;

public class UEIndexNotFoundException extends Throwable {

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
