package jta_tests.datasources;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class XAMySqlDataSource extends AbstractXADataSource {

	public XAMySqlDataSource() throws Exception {
		MysqlXADataSource delegate = new MysqlXADataSource();
		delegate.setURL("jdbc:mysql://localhost:3306/test");
		delegate.setUser("root");
		delegate.setPassword("root");

		setDelegate(delegate);
	}

}
