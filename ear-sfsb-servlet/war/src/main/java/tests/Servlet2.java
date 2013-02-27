package tests;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Servlet2 extends HttpServlet {
	private static final long serialVersionUID = 1730725343354981372L;

//	@Resource
//	it is null
//	private SessionContext sessionContext;
	
//	@EJB(mappedName="slsBean")
//	private SbRemote slsbRemote;
//	
//	@EJB(mappedName="sfsBean")
//	private SbRemote sfsbRemote;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getSession();

		System.out.println("In Servlet2");
		
		switch (Integer.valueOf(req.getParameter("m"))) {
		case 0:
//			slsbRemote.opp1();
			break;
		case 1:
//			sfsbRemote.opp1();
			break;
		}
		
		PrintWriter printWriter = resp.getWriter();
		printWriter.println("In Servlet2");
		printWriter.flush();
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
}
