package pres;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import tests.utils.BeanUtils;

public abstract class AbstractBean implements GenericBeanInterface {

	public static final String THROW_EXCEPTION = "throwException";
	public static final String SET_ROLLBACK_ONLY = "setRollbackOnly";

	private static AtomicInteger cleanCounter = new AtomicInteger(0);
	
	private final int number;
	
	@Resource
	private SessionContext sessionContext;
	
	@Resource(mappedName = "java:testOracleXaDs")
	protected DataSource oracleDs;
	@Resource(mappedName = "java:testPostgresXaDs")
	protected DataSource postgresDs;
	
	public AbstractBean() {
		number = BeanUtils.getBeanInstanceNumber(getClass());
	}
	
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		if (nexts.length > 0) {
			GenericBeanInterface next = nexts[0];
			GenericBeanInterface[] rest = new GenericBeanInterface[nexts.length - 1];
			System.arraycopy(nexts, 1, rest, 0, rest.length);
			
			return next.opp(params, rest);
		} else {
			return null;
		}
	}
	
	@AroundInvoke
	public Object aroundInvoke(InvocationContext invocationContext) throws Exception {
		StringBuilder sb = new StringBuilder();
		StringBuilder ib = new StringBuilder();
		
		String className = getClass().getSimpleName();
		String methodName = invocationContext.getMethod().getName();
		
		int callLevel = cleanCounter.getAndIncrement();
		
		if (callLevel == 0) {
			sb.append("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		}
		for (int i = 0; i < callLevel; i++) {
			ib.append("        ");
		}
		
		sb.append(ib).append("------------>>>>>>>>>>>  ").append(className).append(".").append(methodName).append("(...) called");
		System.out.println(sb.toString());
		
		try {
			return invocationContext.proceed();
			
		} finally {
			sb = new StringBuilder();
			sb.append(ib).append("<<<<<<<<<<<------------  ").append(className).append(".").append(methodName).append("(...) call finished");
			System.out.println(sb.toString());
			
			cleanCounter.decrementAndGet();
		}
	}
	
	protected SessionContext getSessionContext() {
		return sessionContext;
	}
	
	protected String getBeanIdentity(Object... others) {
		Object[] dest = new Object[others.length + 1];
		dest[0] = number;
		System.arraycopy(others, 0, dest, 1, others.length);
		return BeanUtils.getBeanIdentity(getClass().getSimpleName(), dest);
	}

	protected void throwExceptionIfNeeded(Map<String, Object> params) throws Exception {
		String exceptionParam = (String) params.get(THROW_EXCEPTION);
		if (exceptionParam != null) {
			throw (Exception) Class.forName(exceptionParam).newInstance();
		}
	}

	protected void markForRolbackIfNeeded(Map<String, Object> params) {
		String setRollbackOnly = (String) params.get(SET_ROLLBACK_ONLY);
		if (setRollbackOnly != null) {
			sessionContext.setRollbackOnly();
		}
	}
	
	protected void markForRolbackIfNeeded(UserTransaction userTransaction, Map<String, Object> params) throws Exception {
		String setRollbackOnly = (String) params.get(SET_ROLLBACK_ONLY);
		if (setRollbackOnly != null) {
			userTransaction.setRollbackOnly();
		}
	}
	
}
