package tests.isol_presentation.datasources;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

import oracle.jdbc.xa.client.OracleXADataSource;


public class OracleXADataSourceWrapper implements XADataSource {

	private XADataSource delegate;

	public OracleXADataSourceWrapper(String url, String user, String password) throws SQLException {
		OracleXADataSource xaDataSource = new OracleXADataSource();
		xaDataSource.setURL(url);
		xaDataSource.setUser(user);
		xaDataSource.setPassword(password);
		
		this.delegate = xaDataSource;
	}
	
	public XAConnection getXAConnection() throws SQLException {
		return Utils.createXaConnectionProxy(delegate, delegate.getXAConnection(), XAResource.class);
	}

	public XAConnection getXAConnection(String user, String password)
			throws SQLException {
		return delegate.getXAConnection(user, password);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}
	public void setLogWriter(PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}
	
	public int getLoginTimeout() throws SQLException {
		return delegate.getLoginTimeout();
	}
	public void setLoginTimeout(int seconds) throws SQLException {
		delegate.setLoginTimeout(seconds);
	}

}