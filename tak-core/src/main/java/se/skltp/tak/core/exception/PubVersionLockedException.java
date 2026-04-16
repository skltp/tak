package se.skltp.tak.core.exception;
public class PubVersionLockedException extends RuntimeException {

	private static final long serialVersionUID = 7213649767294975339L;

	public PubVersionLockedException() {
		super();
	}

	public PubVersionLockedException(String message) {
		super(message);
	}

}
