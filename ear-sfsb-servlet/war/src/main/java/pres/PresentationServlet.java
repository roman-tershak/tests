package pres;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pres.transactions.DBUtils;

import tests.ServletUtils;

public class PresentationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String CLEAN_DATABASE = "cleanDatabase";
	private static final String FORCE_BEAN_RELOAD = "forceBeanReload";
	
	private static int rowCount = 0;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Map<String, Object> params = ServletUtils.retrieveRequestParameters(req);
		String result = printCall(params);
		
		try {
			if (params.containsKey(CLEAN_DATABASE)) {
				DBUtils.cleanupDatabase();
			}

			List<GenericBeanInterface> beans = retrieveBeans(req.getSession(), params);
			if (beans.size() > 0) {
				GenericBeanInterface firstBean = beans.get(0);
				GenericBeanInterface[] otherBeans = beans.subList(1, beans.size()).toArray(new GenericBeanInterface[beans.size() - 1]);

				params.put("rowId", rowCount);
				rowCount += 10;

				try {
					result += firstBean.opp(params, otherBeans);
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		} finally {
			PrintWriter writer = resp.getWriter();
			writer.print(result);
			writer.flush();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	private List<GenericBeanInterface> retrieveBeans(HttpSession httpSession, Map<String, Object> params) {
		Boolean forceReload = Boolean.valueOf((String) params.get(FORCE_BEAN_RELOAD));
		
		TreeMap<Integer, GenericBeanInterface> orderedBeans = new TreeMap<Integer, GenericBeanInterface>();
		for (String paramName : params.keySet()) {
			if (paramName.startsWith("bn")) {
				Integer order;
				try {
					order = Integer.valueOf(paramName.substring(2));
				} catch (NumberFormatException e) {
					order = 0;
				}
				orderedBeans.put(order, 
						(GenericBeanInterface) ServletUtils.getAnyBean(httpSession, (String) params.get(paramName), forceReload));
			}
		}
		
		List<GenericBeanInterface> beans = new LinkedList<GenericBeanInterface>();
		for (GenericBeanInterface genericBeanInterface : orderedBeans.values()) {
			beans.add(genericBeanInterface);
		}
		
		return beans;
	}

	private String printCall(Map<String, Object> params) {
		StringBuilder sb = new StringBuilder("\n\n\n\n\n\n\n\n\n\n");
		sb.append("------------------------------------------------------------------------------------------------------------------\n");
		sb.append(Runtime.getRuntime().hashCode()).append("-").append("PresentationServlet, parameters = ").append(params).append("\n");
		String log = sb.toString();
		
		System.out.println(log);
		return log;
	}
}
