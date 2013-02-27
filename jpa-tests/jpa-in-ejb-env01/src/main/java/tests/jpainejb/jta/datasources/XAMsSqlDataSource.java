package tests.jpainejb.jta.datasources;

import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;

public class XAMsSqlDataSource extends AbstractXADataSource{

	public XAMsSqlDataSource() throws Exception {
		SQLServerXADataSource delegate = new SQLServerXADataSource();
		delegate.setServerName("localhost");
		delegate.setPortNumber(1433);
		delegate.setDatabaseName("test");
		delegate.setUser("test");
		delegate.setPassword("test!2");

		setDelegate(delegate);
	}

}
