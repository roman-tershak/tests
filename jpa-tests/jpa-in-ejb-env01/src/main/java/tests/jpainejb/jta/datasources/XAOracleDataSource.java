package tests.jpainejb.jta.datasources;

import oracle.jdbc.xa.client.OracleXADataSource;

public class XAOracleDataSource extends AbstractXADataSource {

	public XAOracleDataSource() throws Exception {
		OracleXADataSource delegate = new OracleXADataSource();
		delegate.setURL("jdbc:oracle:thin:@localhost:1521:XE");
		delegate.setUser("tests3");
		delegate.setPassword("tests");

		setDelegate(delegate);
	}

}
