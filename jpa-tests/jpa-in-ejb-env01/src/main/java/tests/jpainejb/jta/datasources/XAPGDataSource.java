package tests.jpainejb.jta.datasources;

import org.postgresql.xa.PGXADataSource;

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

}
