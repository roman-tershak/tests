package jta_tests.resourcerecovery;

import jta_tests.test02.XAUtils;

public class MySql50XAResourceRecovery extends AbstractXAResourceRecovery {
	
	public MySql50XAResourceRecovery() throws Exception {
		super(XAUtils.getXADataSources(false, false, true, false, false, false).get(0));
	}
}
