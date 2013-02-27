package tests.datasources;

import org.postgresql.xa.PGXADataSource;


public class XAPostgresDataSource extends AbstractXADataSource {

	public XAPostgresDataSource() throws Exception {
		PGXADataSource delegate = new PGXADataSource();
		delegate.setServerName("localhost");
		delegate.setDatabaseName("tests");
		delegate.setUser("tests");
		delegate.setPassword("tests");
		delegate.setLoginTimeout(10);

		setDelegate(delegate);
	}

}
