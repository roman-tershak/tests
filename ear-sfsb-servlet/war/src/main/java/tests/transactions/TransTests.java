package tests.transactions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tests.ServletUtils;
import tests.transactions.TransRemote;

public class TransTests extends HttpServlet {
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

		Map<String, Object> params = ServletUtils.retrieveRequestParameters(req);
		
		String result = Runtime.getRuntime().hashCode() + "-" + "TransTests, sessInt = " + sessionAttrib.getAndIncrement() + ", parameters = " + params + "\n";
		System.out.println(result);
		
		String testName = req.getParameter("testName");
		String beanName = req.getParameter("bn");
		String beanName2 = req.getParameter("bn2");
		Boolean forceBeanReload = Boolean.valueOf(req.getParameter("forceBeanReload"));
		int testStep = 0;
		if (req.getParameter("testStep") != null) {
			testStep = Integer.valueOf(req.getParameter("testStep"));
		}
		
		TransRemote beanA = null;
		TransRemote beanB = null;
		if (beanName != null) {
			beanA = ServletUtils.getBean(httpSession, beanName, beanName, forceBeanReload);
		}
		if (beanName2 != null) {
			beanB = ServletUtils.getBean(httpSession, beanName2, beanName2, forceBeanReload);
		}
		try {
			if (testName == null) {
				result += beanA.opp1(beanB, params);
			} else if ("bmt".equals(testName)) {
				if (testStep == 1) {
					result += beanA.bmtOpp1(beanB, params);
				} else if (testStep == 2) {
					result += beanA.bmtOpp2(beanB, params);
				} else {
					result += beanA.bmtOpp3(beanB, params);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
}
