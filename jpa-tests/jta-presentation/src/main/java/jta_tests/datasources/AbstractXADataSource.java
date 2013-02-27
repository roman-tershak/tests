package jta_tests.datasources;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;


public abstract class AbstractXADataSource implements XADataSource {

	private XADataSource delegate;

	protected XADataSource getDelegate() {
		return delegate;
	}
	protected void setDelegate(XADataSource delegate) {
		this.delegate = delegate;
	}

	public XAConnection getXAConnection() throws SQLException {
		return TransUtils.createXaConnectionProxy(delegate, delegate.getXAConnection(), XAResource.class);
	}

	public XAConnection getXAConnection(String user, String password)
			throws SQLException {
		return TransUtils.createXaConnectionProxy(delegate, delegate.getXAConnection(user, password), XAResource.class);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}

	public int getLoginTimeout() throws SQLException {
		return delegate.getLoginTimeout();
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		delegate.setLoginTimeout(seconds);
	}

}