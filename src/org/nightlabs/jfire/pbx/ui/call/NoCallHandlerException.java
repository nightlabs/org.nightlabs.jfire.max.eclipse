package org.nightlabs.jfire.pbx.ui.call;

import org.nightlabs.jfire.pbx.PhoneSystemException;

public class NoCallHandlerException extends PhoneSystemException {
	private static final long serialVersionUID = 1L;

	public NoCallHandlerException() {
	}

	public NoCallHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoCallHandlerException(String message) {
		super(message);
	}

	public NoCallHandlerException(Throwable cause) {
		super(cause);
	}
}
