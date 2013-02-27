package tests.datasources;

import oracle.jdbc.xa.client.OracleXADataSource;


public class XAOracleDataSource extends AbstractXADataSource {

	public XAOracleDataSource() throws Exception {
		OracleXADataSource delegate = new OracleXADataSource();
		delegate.setURL("jdbc:oracle:thin:@localhost:1521:XE");
		delegate.setUser("tests");
		delegate.setPassword("tests");
		delegate.setLoginTimeout(10);

		setDelegate(delegate);
	}

}
