package tests.transactions.jbossCache;

import java.io.IOException;
import java.util.Map;
import javax.management.MalformedObjectNameException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.config.Configuration;
import org.jboss.cache.config.Configuration.CacheMode;
import org.jboss.cache.lock.IsolationLevel;

import tests.ServletUtils;

public class JbossCacheTests extends HttpServlet {
	private static final String TEST_CACHE_KEY = "testCacheKey";

	private static final long serialVersionUID = 1L;
	
	private static Cache<String,Object> cache;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession httpSession = req.getSession();

		Map<String, Object> params = ServletUtils.retrieveRequestParameters(req);
		
//		Node<String, Object> root = cache.getRoot();
//		
//		AtomicInteger mutableInt = (AtomicInteger) root.get(TEST_CACHE_KEY);
//		if (mutableInt == null) {
//			mutableInt = new AtomicInteger(0);
//			root.put(TEST_CACHE_KEY, mutableInt);
//		}
//		int prevVal = mutableInt.getAndSet(Runtime.getRuntime().hashCode());
//		root.put(TEST_CACHE_KEY, mutableInt);
		
		System.out.println(Runtime.getRuntime().hashCode() + "-" + this.getClass().getSimpleName() + ", parameters = " + params + "\n");
		
		String beanName = req.getParameter("bn");
		CacheRemote bean = (CacheRemote) ServletUtils.getAnyBean(httpSession, beanName, true);
		
		int mode = Integer.valueOf(req.getParameter("mode"));
		try {
			bean.test01(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
//	@Override
//	public void init() throws ServletException {
//		try {
//			initCache();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ServletException(e);
//		}
//	}
	
	public static void initCache() throws MalformedObjectNameException, NullPointerException {
//		MBeanServer server = MBeanServerLocator.locateJBoss();
//		ObjectName on = new ObjectName("jboss.cache:service=Cache");
//		CacheJmxWrapperMBean cacheWrapper = 
//				(CacheJmxWrapperMBean) MBeanServerInvocationHandler.newProxyInstance(server, on, 
//						CacheJmxWrapperMBean.class, false);
//		Cache cache = cacheWrapper.getCache();

		CacheFactory<String,Object> cacheFactory = new DefaultCacheFactory<String, Object>();
		Configuration cfg = new Configuration();
		cfg.setCacheMode(CacheMode.REPL_SYNC);
		cfg.setIsolationLevel(IsolationLevel.REPEATABLE_READ);
		cfg.setClusterName("TRoma-JbossCache");
		cache = cacheFactory.createCache(cfg, false);
		
		cache.start();
		
	}
	
	
}
