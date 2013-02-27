package tests.transactions;


@javax.ejb.ApplicationException(rollback=true)
public class ApplicationException extends Exception {
	private static final long serialVersionUID = 5833212458945679281L;

	public ApplicationException() {
		super();
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}

	
}
