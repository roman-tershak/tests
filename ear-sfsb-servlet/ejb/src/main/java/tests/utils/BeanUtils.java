package tests.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BeanUtils {

	private static final ConcurrentHashMap<Class<?>, AtomicInteger> beanCounters = new ConcurrentHashMap<Class<?>, AtomicInteger>();

	public static int getBeanInstanceNumber(Class<?> beanClass) {
		if (!beanCounters.containsKey(beanClass)) {
			beanCounters.putIfAbsent(beanClass, new AtomicInteger(0));
		}
		return beanCounters.get(beanClass).incrementAndGet();
	}
	
	public static String getBeanIdentity(String className, Object... others) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(Runtime.getRuntime().hashCode()).append("\t").append(className);
		
		for (int i = 0; i < others.length; i++) {
			sb.append("\t").append(others[i]);
			
		}
		sb.append("\n");
		
		return sb.toString();
	}
	
	public static void pushBeanCall() {
		
	}
	
	public static void popBeanCall() {
		
	}
	
	private BeanUtils() {
	}
}
