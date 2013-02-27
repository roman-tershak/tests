package tests.jta.datasources;

import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import oracle.jdbc.xa.client.OracleXADataSource;
import tests.jpainejb.jta.util.Utils;

public class XAOracleDataSource extends AbstractXADataSource {

	public XAOracleDataSource() throws Exception {
		OracleXADataSource delegate = new OracleXADataSource();
		delegate.setURL("jdbc:oracle:thin:@localhost:1521:XE");
		delegate.setUser("tests");
		delegate.setPassword("tests");

		setDelegate(delegate);
	}

	public XAConnection getXAConnection() throws SQLException {
//		return Utils.createXaConnectionProxy(getDelegate(), getDelegate().getXAConnection(), XAResource.class);
		return getDelegate().getXAConnection();
	}

}
