package tests;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import tests.transactions.TransRemote;

public class ServletUtils {

	public static TransRemote getBean(HttpSession httpSession, String httpSessionKey, String jndiName, boolean force) {
		TransRemote transRemote = (TransRemote) httpSession.getAttribute(httpSessionKey);
		if (transRemote == null || force) {
			try {
				transRemote = (TransRemote) new InitialContext().lookup(jndiName);
			} catch (NamingException e) {
				throw new RuntimeException(e);
			}
			httpSession.setAttribute(httpSessionKey, transRemote);
		}
		return transRemote;
	}

	public static TransRemote getBean(HttpSession httpSession, String httpSessionKey, boolean force) {
		return getBean(httpSession, httpSessionKey, httpSessionKey, force);
	}
	
	public static Object getAnyBean(HttpSession httpSession, String httpSessionKey, boolean force) {
		Object remote = httpSession.getAttribute(httpSessionKey);
		if (remote == null || force) {
			try {
				remote = new InitialContext().lookup(httpSessionKey);
			} catch (NamingException e) {
				throw new RuntimeException(e);
			}
			httpSession.setAttribute(httpSessionKey, remote);
		}
		return remote;
	}
	
	public static Map<String, Object> retrieveRequestParameters(HttpServletRequest req) {
		Map<String, Object> params = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Enumeration<String> parameterNames = req.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			params.put(paramName, req.getParameter(paramName));
		}
		return params;
	}

}
