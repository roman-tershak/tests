package tests.transactions.jbossCache;

import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.Node;
import org.jboss.cache.config.Configuration;
import org.jboss.cache.config.Configuration.CacheMode;
import org.jboss.cache.lock.IsolationLevel;
import org.jboss.cache.transaction.JBossTransactionManagerLookup;

import tests.utils.BeanUtils;

@Stateless(mappedName="CacheBean01")
public class CacheBean01 implements CacheRemote {

	private static final String HUGE_DATA_KEY = "HugeDataKey";
	
	private static Cache<String,Object> cache;

	private final int number;
	@Resource
	private SessionContext sessionContext;

	public CacheBean01() {
		number = BeanUtils.getBeanInstanceNumber(getClass());
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void test01(Map<String, Object> params) throws Exception {
		
		StringBuffer result = new StringBuffer("\n\n\n" + BeanUtils.getBeanIdentity(getClass().getSimpleName(), number));
		
		Node<String, Object> cacheRoot = getCache().getRoot();
		Integer mode = Integer.valueOf((String) params.get("mode"));
		String sizeStr = (String) params.get("size");
		Integer size = sizeStr != null ? Integer.valueOf(sizeStr) : 10000;
		
		Integer[] integers;
		switch (mode) {
		case 0:
			integers = createHugeAmountOfData(size);
			printAPieceOfArray(result, integers);
			cacheRoot.put(HUGE_DATA_KEY, integers);
			break;
		case 1:
			printAPieceOfArray(result, (Integer[]) cacheRoot.get(HUGE_DATA_KEY));
			break;
		case 2:
			integers = createHugeAmountOfData(size);
			printAPieceOfArray(result, integers);
			cacheRoot.put(HUGE_DATA_KEY, integers);
			sessionContext.setRollbackOnly();
			break;
		default:
			throw new IllegalArgumentException("Not correct mode - " + mode);
		}
		
		System.out.println(result);
	}

	private void printAPieceOfArray(StringBuffer result, Integer[] integers) {
		result.append("Array length - ").append(integers.length).append(", [");
		for (int i = 0; i < 30; i++) {
			result.append(integers[i]).append(", ");
		}
		result.append("...]");
	}
	
	private Integer[] createHugeAmountOfData(int size) {
		Integer[] array = new Integer[size];
		for (int i = 0; i < array.length; i++) {
			array[i] = new Integer((int) (Math.random() * Integer.MAX_VALUE));
		}
		return array;
	}

	private synchronized Cache<String, Object> getCache() throws Exception {
		boolean repeat = false;
		do {
			if (cache == null || repeat) {
				CacheFactory<String,Object> cacheFactory = new DefaultCacheFactory<String, Object>();
				Configuration cfg = new Configuration();
				cfg.setCacheMode(CacheMode.REPL_SYNC);
				cfg.setIsolationLevel(IsolationLevel.REPEATABLE_READ);
				cfg.setClusterName("TRoma-JbossCache-SLSB");
				cfg.setStateRetrievalTimeout(30000);
				cfg.setSyncReplTimeout(45000);
				cfg.setTransactionManagerLookupClass(JBossTransactionManagerLookup.class.getName());
				cache = cacheFactory.createCache(cfg, false);
				cache.start();
				
				break;
			} else {
				try {
					System.out.println(cache.getRoot().getKeys());
				} catch (Exception e) {
					repeat = true;
				}
			}
		} while (repeat);
		
		return cache;
	}
}
