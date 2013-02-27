package jta_tests.resourcerecovery;

import jta_tests.test02.XAUtils;

public class OracleXAResourceRecovery extends AbstractXAResourceRecovery {

	public OracleXAResourceRecovery() throws Exception {
		super(XAUtils.getXADataSources(true, false, false, false, false, false).get(0));
	}
}
