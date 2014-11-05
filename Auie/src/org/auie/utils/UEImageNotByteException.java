package org.auie.utils;

public class UEImageNotByteException extends Throwable {

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
