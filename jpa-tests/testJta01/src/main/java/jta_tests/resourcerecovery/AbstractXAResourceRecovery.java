package jta_tests.resourcerecovery;

import java.sql.SQLException;

import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

import jta_tests.test02.XAUtils;

import com.arjuna.ats.jta.recovery.XAResourceRecovery;

public abstract class AbstractXAResourceRecovery implements XAResourceRecovery {

	private final XADataSource xaDataSource;
	private boolean hasMoreResources = true;
	
	protected AbstractXAResourceRecovery(XADataSource xaDataSource) throws Exception {
		this.xaDataSource = xaDataSource;
	}

	@Override
	public XAResource getXAResource() throws SQLException {
		hasMoreResources = false;
		return XAUtils.getXaResourceWithProxy(xaDataSource.getXAConnection());
	}

	@Override
	public boolean hasMoreResources() {
		boolean b = hasMoreResources;
		hasMoreResources = true;
		return b;
	}

	@Override
	public boolean initialise(String paramString) throws SQLException {
		return true;
	}

}
