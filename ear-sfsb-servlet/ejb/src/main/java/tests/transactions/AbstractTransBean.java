package tests.transactions;

import java.util.Map;

public abstract class AbstractTransBean implements TransRemote {

	public String opp1(TransRemote next, Map<String, Object> params)
			throws Exception {
		return null;
	}

	public String bmtOpp1(TransRemote next, Map<String, Object> params)
			throws Exception {
		return null;
	}
	
	public String bmtOpp2(TransRemote next, Map<String, Object> params)
			throws Exception {
		return null;
	}
	
	public String bmtOpp3(TransRemote next, Map<String, Object> params)
			throws Exception {
		return null;
	}
}
