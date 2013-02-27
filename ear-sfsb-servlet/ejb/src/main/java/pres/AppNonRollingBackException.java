package pres;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class AppNonRollingBackException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
}
