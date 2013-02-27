package jta_tests.datasources;

import org.postgresql.xa.PGXADataSource;

public class XAPGDataSource extends AbstractXADataSource {

	public XAPGDataSource() throws Exception {
		PGXADataSource delegate = new PGXADataSource();
		delegate.setServerName("localhost");
		delegate.setPortNumber(5432);
		delegate.setDatabaseName("tests");
		delegate.setUser("tests");
		delegate.setPassword("tests");

		setDelegate(delegate);
	}

}
