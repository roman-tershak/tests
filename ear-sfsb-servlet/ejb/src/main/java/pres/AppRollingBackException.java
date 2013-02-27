package pres;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class AppRollingBackException extends Exception {
	private static final long serialVersionUID = 1L;
	
}
