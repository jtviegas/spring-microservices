package org.aprestos.labs.apis.springboot2.statemanager;

public class StateNotFoundException extends StateManagerException {

	private static final long serialVersionUID = 1L;

	public StateNotFoundException() {
		super();
	}

	public StateNotFoundException(String arg0) {
		super(arg0);
	}

	public StateNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public StateNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
