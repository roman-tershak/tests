package jta_tests.resourcerecovery;

import jta_tests.test02.XAUtils;

public class MySql55XAResourceRecovery extends AbstractXAResourceRecovery {

	public MySql55XAResourceRecovery() throws Exception {
		super(XAUtils.getXADataSources(false, false, false, true, false, false).get(0));
	}
}
