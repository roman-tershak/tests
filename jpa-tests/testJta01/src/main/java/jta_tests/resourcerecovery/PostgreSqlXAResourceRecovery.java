package jta_tests.resourcerecovery;

import jta_tests.test02.XAUtils;

public class PostgreSqlXAResourceRecovery extends AbstractXAResourceRecovery {

	public PostgreSqlXAResourceRecovery() throws Exception {
		super(XAUtils.getXADataSources(false, false, false, false, false, true).get(0));
	}
}
