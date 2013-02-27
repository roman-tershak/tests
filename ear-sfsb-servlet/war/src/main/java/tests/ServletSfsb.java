package tests;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ServletSfsb extends HttpServlet {
	private static final String HTTP_SESSION_ATTR_CLUSTERED_TEST_KEY = "clustered test key";

	private static final long serialVersionUID = 1L;

//	@EJB(mappedName="sfsBean")
//	private SfsbRemote sfsbRemote;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession httpSession = req.getSession();

		AtomicInteger sessionAttrib = (AtomicInteger) httpSession.getAttribute(HTTP_SESSION_ATTR_CLUSTERED_TEST_KEY);
		if (sessionAttrib == null) {
			sessionAttrib = new AtomicInteger(0);
			httpSession.setAttribute(HTTP_SESSION_ATTR_CLUSTERED_TEST_KEY, sessionAttrib);
		}
		
		String beanName = req.getParameter("bn");
		String beanName2 = req.getParameter("bn2");
		Integer m = Integer.valueOf(req.getParameter("m"));
		
		String result = Runtime.getRuntime().hashCode() + "-" + "Servlet, sessInt = " + sessionAttrib.getAndIncrement() + "\n";
		System.out.println(result);
		
//		switch (m) {
//		case 1:
//			result += getBean(httpSession, beanName, beanName, false).opp1();
//			break;
//		case 2:
//			result += getBean(httpSession, beanName, beanName, false).opp2();
//			break;
//		case 3:
//			result += getBean(httpSession, beanName, beanName, true).opp3(
//					getBean(httpSession, beanName2, beanName2, false));
//			break;
//		case 4:
//			result += getBean(httpSession, beanName, beanName, false).opp3(
//					getBean(httpSession, beanName2, beanName2, false));
//			break;
//		}
		
//		System.out.println(result);
		
		PrintWriter writer = resp.getWriter();
		writer.print(result);
		writer.flush();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

//	private SbRemote getBean(HttpSession httpSession, String httpSessionKey, String jndiName, boolean force) {
//		SbRemote sbRemote = (SbRemote) httpSession.getAttribute(httpSessionKey);
//		if (sbRemote == null || force) {
//			try {
//				sbRemote = (SbRemote) new InitialContext().lookup(jndiName);
//			} catch (NamingException e) {
//				throw new RuntimeException(e);
//			}
//			httpSession.setAttribute(httpSessionKey, sbRemote);
//		}
//		return sbRemote;
//	}
}
