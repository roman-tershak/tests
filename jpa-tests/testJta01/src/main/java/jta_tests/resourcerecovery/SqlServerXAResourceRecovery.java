package jta_tests.resourcerecovery;

import jta_tests.test02.XAUtils;

public class SqlServerXAResourceRecovery extends AbstractXAResourceRecovery {

	public SqlServerXAResourceRecovery() throws Exception {
		super(XAUtils.getXADataSources(false, false, false, false, true, false).get(0));
	}
}
