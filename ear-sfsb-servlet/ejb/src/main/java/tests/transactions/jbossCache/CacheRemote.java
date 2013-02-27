package tests.transactions.jbossCache;

import java.util.Map;

import javax.ejb.Remote;

@Remote
public interface CacheRemote {

	void test01(Map<String, Object> params) throws Exception;
}
