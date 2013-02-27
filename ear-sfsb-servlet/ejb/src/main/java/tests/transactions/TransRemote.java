package tests.transactions;

import java.util.Map;

import javax.ejb.Remote;

@Remote
public interface TransRemote {

	String opp1(TransRemote next, Map<String, Object> params) throws Exception;
	
	String bmtOpp1(TransRemote next, Map<String, Object> params) throws Exception;

	String bmtOpp2(TransRemote next, Map<String, Object> params) throws Exception;

	String bmtOpp3(TransRemote next, Map<String, Object> params) throws Exception;
}
