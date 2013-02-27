package jta_tests.datasources;

import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;

public class XAMsSqlDataSource extends AbstractXADataSource{

	public XAMsSqlDataSource() throws Exception {
		SQLServerXADataSource delegate = new SQLServerXADataSource();
		delegate.setURL("jdbc:sqlserver://;serverName=::1;port=1433;databaseName=test;user=sa;password=sasasa!2;");
//		delegate.setURL("jdbc:sqlserver://127.0.0.1:1433;databaseName=test;user=test;password=test;");
//		delegate.setURL("jdbc:sqlserver://172.24.224.5:1433;databaseName=test;user=test;password=test;");
//		delegate.setURL("jdbc:sqlserver://172.24.224.5\\SQLEXPRESS:1433;databaseName=test;userName=test;password=test;");
//		delegate.setURL("jdbc:sqlserver://localhost:1433;instanceName=LWO1-DHP-F07487\\SQLEXPRESS;databaseName=test;");
//		"jdbc:sqlserver://localhost;databaseName=test;portNumber=1433;instanceName=./SQLEXPRESS;userName=test;password=test;";
//		delegate.setServerName("localhost\\SQLEXPRESS");
//		delegate.setPortNumber(1433);
//		delegate.setDatabaseName("test");
//		delegate.setUser("test");
//		delegate.setPassword("test");

		setDelegate(delegate);
	}

}
