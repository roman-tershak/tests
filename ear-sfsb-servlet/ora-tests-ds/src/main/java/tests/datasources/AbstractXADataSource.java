package tests.datasources;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

import tests.utils.TransUtils;


public abstract class AbstractXADataSource implements XADataSource {

	private XADataSource delegate;

	protected XADataSource getDelegate() {
		return delegate;
	}
	protected void setDelegate(XADataSource delegate) {
		this.delegate = delegate;
	}

	public XAConnection getXAConnection() throws SQLException {
		XAConnection xaConnection = TransUtils.createXaConnectionProxy(delegate.getXAConnection(), XAResource.class/*, Connection.class*/);
		
		System.err.println("\t\t" + xaConnection + " = getXAConnection()");
		return xaConnection;
//		return delegate.getXAConnection();
	}

	public XAConnection getXAConnection(String user, String password)
			throws SQLException {
		XAConnection xaConnection = TransUtils.createXaConnectionProxy(delegate.getXAConnection(user, password), XAResource.class/*, Connection.class*/);
		
		System.err.println("\t\t" + xaConnection + " = getXAConnection(user=" + user + ", password" + password + ")");
		return xaConnection;
//		return delegate.getXAConnection(user, password);
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