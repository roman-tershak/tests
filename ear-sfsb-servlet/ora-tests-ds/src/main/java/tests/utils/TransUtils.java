package tests.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.commons.codec.binary.Hex;

public class TransUtils {
	
	public static XAConnection createXaConnectionProxy(XAConnection xaConnection, Class<?>... toProxy) {
		return (XAConnection) createProxy(xaConnection, 
				new Class[]{XAConnection.class, PooledConnection.class}, 
				toProxy, new String[0]);
	}
	
	public static XAConnection createXaConnectionProxy(XAConnection xaConnection, String... proxyFrom) {
		return (XAConnection) createProxy(xaConnection, 
				new Class[]{XAConnection.class, PooledConnection.class}, 
				new Class[0], proxyFrom);
	}
	
	private static Object createProxy(Object delegate, Class<?>[] proxyInterfaces, Class<?>[] toProxy, String[] proxyFrom) {
		InvocationHandler h;
		if (toProxy.length > 0) {
			h = new GenericInvocationHandler(delegate, toProxy);
		} else {
			h = new GenericInvocationHandler(delegate, proxyFrom);
		}
		return Proxy.newProxyInstance(delegate.getClass().getClassLoader(), proxyInterfaces, h);
	}

	private static final class GenericInvocationHandler implements InvocationHandler {
		
		private final Object delegate;
		private final Class<?>[] toProxy;
		private final String[] proxyFrom;
		private final ConcurrentHashMap<Method, Object> cachedProxies = new ConcurrentHashMap<Method, Object>();
		private final ConcurrentHashMap<Method, Object> cachedResults = new ConcurrentHashMap<Method, Object>();

		public GenericInvocationHandler(Object delegate, Class<?>... toProxy) {
			this.delegate = delegate;
			this.toProxy = toProxy;
			this.proxyFrom = new String[0];
		}
		
		public GenericInvocationHandler(Object delegate, String... proxyFrom) {
			this.delegate = delegate;
			this.toProxy = new Class[0];
			this.proxyFrom = proxyFrom;
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				args = argsWorkaround(proxy, args);
				
				delayCall(method);
				
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
						proxy = createProxy(result, interfacesToProxy, toProxy, proxyFrom);
						cachedProxies.put(method, proxy);
					} else {
						proxy = createProxy(result, interfacesToProxy, toProxy, proxyFrom);
					}
				}
				result = proxy;
			}
			return result;
		}

		private void delayCall(Method method) throws Exception {
//			if (xaDataSource != null && (xaDataSource instanceof PGXADataSource ||
//					xaDataSource instanceof SQLServerXADataSource)) {
//				if (method.getName().equals("prepare")) {

//					System.out.println("\n\n\n\nIn prepare\n\n\n\n");
//					Thread.sleep(10000);
					
//				} else if (method.getName().equals("commit")) {
//					if (commitCallNum++ < 1) {
//						
//						System.out.println("\n\n\n\nIn commit\n\n\n\n");
//						Thread.sleep(10000);
//					}
//				}
//			}
		}
	}

	public static void printMethodCall(Object delegate, Method method, Object[] args, Object result) {
		String methodName = method.getName();
		
		if (methodName.equals("toString") ||
				methodName.equals("hashCode") ||
				methodName.equals("equals")) {
			return;
		}
		
		StringBuilder sb = new StringBuilder("\t\t");
		
		printMethodResult(method, result, sb);
		sb.append(methodName).append("(").append(printMethodArgs(method, args)).append(")").append(" on ").append(delegate);
		
		System.out.println(sb);
		System.out.flush();
	}

	private static void printMethodResult(Method method, Object result, StringBuilder sb) {
		String methodName = method.getName();
		
		if (result instanceof Exception) {
			StringWriter stringWriter = new StringWriter();
			((Exception) result).printStackTrace(new PrintWriter(stringWriter));
			
			sb.append(" - Exception:\n").append(stringWriter);
		} else {
			Class<?> returnType = method.getReturnType();
			
			if (returnType == void.class || returnType == Void.class) {
				return;
			}
			
			if (returnType.isArray()) {
				sb.append(Arrays.toString((Object[]) result));
			} else if (method.getDeclaringClass() == XAResource.class) {
				if ("prepare".equals(methodName)) {
					sb.append(retrieveXaResourceFlagsFromInt((Integer) result, true));
				} else if ("recover".equals(methodName)) {
					sb.append("[");
					Xid[] xids = (Xid[]) result;
					for (int i = 0; i < xids.length; i++) {
						if (i > 0) {
							sb.append(", ");
						}
						sb.append(printXid(xids[i]));
					}
					sb.append("]");
				} else {
					sb.append(result);
				}
			} else {
				sb.append(result);
			}
			
			sb.append(" = ");
		}
	}

	private static String printMethodArgs(Method method, Object[] args) {
		StringBuilder sb = new StringBuilder();
		if (args != null) {
			Class<?> methodDeclaringClass = method.getDeclaringClass();
			String methodName = method.getName();
			
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (i > 0) {
					sb.append(", ");
				}
				
				String argName = arg.getClass().getSimpleName();
				
				if (methodDeclaringClass == XAResource.class) {
					if (arg instanceof Xid) {
						argName = "Xid";
						arg = printXid((Xid) arg);
					} else if ("commit".equals(methodName) && i == 1) {
						argName = "onePhase";
					} else if ("start".equals(methodName) && i == 1) {
						argName = "flags";
						arg = retrieveXaResourceFlagsFromInt((Integer) arg, false);
					} else if ("end".equals(methodName) && i == 1) {
						argName = "flags";
						arg = retrieveXaResourceFlagsFromInt((Integer) arg, false);
					} else if ("recover".equals(methodName)) {
						argName = "flag";
						arg = retrieveXaResourceFlagsFromInt((Integer) arg, false);
					}
				}
				sb.append(argName).append("=").append(arg);
			}
		}
		return sb.toString();
	}
	
	private static String retrieveXaResourceFlagsFromInt(int flags, boolean prepare) {
	    switch (flags) {
	    case 0x20000000:
	    	return "TMFAIL";
	    case 0x00200000:
	    	return "TMJOIN";
	    case 0x00000000:
	    	if (prepare) {
	    		return "XA_OK";
	    	} else {
	    		return "TMNOFLAGS";
	    	}
	    case 0x40000000:
	    	return "TMONEPHASE";
	    case 0x08000000:
	    	return "TMRESUME";
	    case 0x01000000:
	    	return "TMSTARTRSCAN";
	    case 0x00800000:
	    	return "TMENDRSCAN";
	    case 0x04000000:
	    	return "TMSUCCESS";
	    case 0x02000000:
	    	return "TMSUSPEND";
	    case 0x00000003:
	    	return "XA_RDONLY";
	    default:
	    	return String.valueOf(flags);
	    }
	}

	private static String printXid(Xid xid) {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(Hex.encodeHexString(xid.getGlobalTransactionId())).append(", ");
		sb.append(Hex.encodeHexString(xid.getBranchQualifier())).append(">");
		return sb.toString();
	}
	
}
