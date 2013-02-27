package tests.isol_presentation.datasources;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.CommonDataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;


public class Utils {
	
	public static XADataSource createXaDataSourceProxy(XADataSource xaDataSource, Class<?>... toProxy) {
		return (XADataSource) createProxy(null, xaDataSource, 
				new Class[]{XADataSource.class, CommonDataSource.class}, 
				toProxy, new String[0]);
	}

	public static XADataSource createXaDataSourceProxy(XADataSource xaDataSource, String... proxyFrom) {
		return (XADataSource) createProxy(null, xaDataSource, 
				new Class[]{XADataSource.class, CommonDataSource.class}, 
				new Class[0], proxyFrom);
	}

	public static XAConnection createXaConnectionProxy(XADataSource xaDataSource, XAConnection xaConnection, Class<?>... toProxy) {
		return (XAConnection) createProxy(xaDataSource, xaConnection, 
				new Class[]{XAConnection.class, PooledConnection.class}, 
				toProxy, new String[0]);
	}
	
	public static XAConnection createXaConnectionProxy(XADataSource xaDataSource, XAConnection xaConnection, String... proxyFrom) {
		return (XAConnection) createProxy(xaDataSource, xaConnection, 
				new Class[]{XAConnection.class, PooledConnection.class}, 
				new Class[0], proxyFrom);
	}
	
	private static Object createProxy(XADataSource xaDataSource, Object delegate, Class<?>[] proxyInterfaces, Class<?>[] toProxy, String[] proxyFrom) {
		InvocationHandler h;
		if (toProxy.length > 0) {
			h = new GenericInvocationHandler(xaDataSource, delegate, toProxy);
		} else {
			h = new GenericInvocationHandler(xaDataSource, delegate, proxyFrom);
		}
		return Proxy.newProxyInstance(delegate.getClass().getClassLoader(), proxyInterfaces, h);
	}

	private static final class GenericInvocationHandler implements InvocationHandler {
		
		private final XADataSource xaDataSource;
		private final Object delegate;
		private final Class<?>[] toProxy;
		private final String[] proxyFrom;
		private final ConcurrentHashMap<Method, Object> cachedProxies = new ConcurrentHashMap<Method, Object>();
		private final ConcurrentHashMap<Method, Object> cachedResults = new ConcurrentHashMap<Method, Object>();

		public GenericInvocationHandler(XADataSource xaDataSource, Object delegate, Class<?>... toProxy) {
			this.xaDataSource = xaDataSource;
			this.delegate = delegate;
			this.toProxy = toProxy;
			this.proxyFrom = new String[0];
		}
		
		public GenericInvocationHandler(XADataSource xaDataSource, Object delegate, String... proxyFrom) {
			this.xaDataSource = xaDataSource;
			this.delegate = delegate;
			this.toProxy = new Class[0];
			this.proxyFrom = proxyFrom;
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				args = argsWorkaround(proxy, args);
				
				Object result = method.invoke(delegate, args);
				
				printMethodCall(delegate, method, args, result);
				
				return proxyResultIfNeeded(method, result);
				
			} catch (Exception e) {
				printMethodCall(delegate, method, args, e);
				
				throw e;
			}
		}

		private Object[] argsWorkaround(Object proxy, Object[] args) {
			if (args == null) {
				return args;
			}
			Object[] newArgs = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg == proxy) {
					newArgs[i] = this.delegate;
				} else if (Proxy.isProxyClass(arg.getClass())) {
					InvocationHandler invocationHandler = Proxy.getInvocationHandler(arg);
					if (GenericInvocationHandler.class.isInstance(invocationHandler)) {
						newArgs[i] = ((GenericInvocationHandler) invocationHandler).delegate;
					} else {
						newArgs[i] = arg;
					}
				} else {
					newArgs[i] = arg;
				}
			}
			return newArgs;
		}

		private Object proxyResultIfNeeded(Method method, Object result) {
			if (result == null) {
				return result;
			}
			List<Class<?>> proxyInterfaces = new LinkedList<Class<?>>();
			for (int i = 0; i < toProxy.length; i++) {
				if (toProxy[i].isInstance(result)) {
					// Need to proxy
					proxyInterfaces.add(toProxy[i]);
				}
			}
			for (int i = 0; i < proxyFrom.length; i++) {
				if (method.getName().equals(proxyFrom[i])) {
					// Need to proxy
					proxyInterfaces.add(method.getReturnType());
					break;
				}
			}
			
			if (proxyInterfaces.size() > 0) {
				Object proxy;
				Object prevResult = cachedResults.put(method, result);
				if (result.equals(prevResult)) {
					proxy = cachedProxies.get(method);
				} else {
					Class<?>[] interfacesToProxy = proxyInterfaces.toArray(new Class[proxyInterfaces.size()]);
					if (prevResult == null) {
						proxy = createProxy(xaDataSource, result, interfacesToProxy, toProxy, proxyFrom);
						cachedProxies.put(method, proxy);
					} else {
						proxy = createProxy(xaDataSource, result, interfacesToProxy, toProxy, proxyFrom);
					}
				}
				result = proxy;
			}
			return result;
		}

	}

	public static void printMethodCall(Object delegate, Method method, Object[] args, Object result) {
		if (method.getName().equals("toString") ||
				method.getName().equals("hashCode") ||
				method.getName().equals("equals")) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(method.getName()).append("(").append(printMethodArgs(args)).append(")");
		sb.append(" on ").append(delegate);
		
		if (result instanceof Exception) {
			StringWriter stringWriter = new StringWriter();
			((Exception) result).printStackTrace(new PrintWriter(stringWriter));
			
			sb.append(" - Exception:\n").append(stringWriter);
		} else {
			Class<?> returnType = method.getReturnType();
			if (returnType != void.class && returnType != Void.class) {
				sb.insert(0, " = ");
				if (returnType.isArray()) {
					sb.insert(0, Arrays.toString((Object[]) result));
				} else {
					sb.insert(0, result);
				}
			}
		}
		
		System.err.println(sb);
		System.err.flush();
	}

	private static String printMethodArgs(Object[] args) {
		StringBuilder sb = new StringBuilder();
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(arg.getClass().getSimpleName()).append("=").append(arg);
			}
		}
		return sb.toString();
	}
}
