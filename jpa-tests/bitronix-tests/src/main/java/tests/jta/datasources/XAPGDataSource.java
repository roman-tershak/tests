package tests.jta.datasources;

import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import org.postgresql.xa.PGXADataSource;

import tests.jpainejb.jta.util.Utils;

public class XAPGDataSource extends AbstractXADataSource {

	public XAPGDataSource() throws Exception {
		PGXADataSource delegate = new PGXADataSource();
		delegate.setServerName("localhost");
		delegate.setPortNumber(5432);
		delegate.setDatabaseName("postgres");
		delegate.setUser("postgres");
		delegate.setPassword("12345");

		setDelegate(delegate);
	}

	public XAConnection getXAConnection() throws SQLException {
		return Utils.createXaConnectionProxy(getDelegate(), getDelegate().getXAConnection(), XAResource.class);
//		return getDelegate().getXAConnection();
	}

}
