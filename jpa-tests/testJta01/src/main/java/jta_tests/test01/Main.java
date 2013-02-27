package jta_tests.test01;

import java.sql.Connection;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import jta_tests.test02.XAUtils;


import oracle.jdbc.xa.client.OracleXADataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class Main {
	
	public static void main(String[] args) throws Exception {
//		testWithTransactionManager();
//		testWithoutTranMngrWithXARess();
		testWithXARessWithCommittedAndRolledBackBranches();
	}

	protected static void testWithTransactionManager() throws Exception {
//		Properties props = new Properties();
//		props.load(Main.class.getResourceAsStream("/jta.properties"));
//		for (Entry<Object, Object> entry : props.entrySet()) {
//			System.setProperty((String) entry.getKey(), (String) entry.getValue());
//		}
		
		UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
		TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
		
//		Class.forName("oracle.jdbc.driver.OracleDriver");
//		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests");
		
		OracleXADataSource oxds1 = new OracleXADataSource();
        oxds1.setURL("jdbc:oracle:thin:@localhost:1521:xe");
        oxds1.setUser("tests");
        oxds1.setPassword("tests");

        MysqlXADataSource myxds1 = new MysqlXADataSource();
        myxds1.setUrl("jdbc:mysql://localhost:3307/test");
        myxds1.setUser("root");
        myxds1.setPassword("12345");

        XAConnection xaConn1 = oxds1.getXAConnection();
        XAConnection xaConn2 = myxds1.getXAConnection();
		
        XAResource xaRes1 = xaConn1.getXAResource();
        XAResource xaRes2 = xaConn2.getXAResource();
        
        Connection conn1 = xaConn1.getConnection();
        Connection conn2 = xaConn2.getConnection();
        
		userTransaction.begin();
		
		Transaction transaction = transactionManager.getTransaction();
		
		System.out.println(transaction.enlistResource(xaRes1));
		System.out.println(transaction.enlistResource(xaRes2));
		
		conn1.createStatement().execute("insert into table1 (col1, col2) values ('test02', 2)");
		conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('test03', 'test04', 3)");
		
		System.out.println(transaction.delistResource(xaRes1, XAResource.TMSUCCESS));
		System.out.println(transaction.delistResource(xaRes2, XAResource.TMSUCCESS));
		
		userTransaction.commit();
	}

	protected static void testWithoutTranMngrWithXARess() throws Exception {
		
		List<XADataSource> xaDataSources = XAUtils.getXADataSources(true, false, true, true, true, true);
		
        XAConnection xaConn1 = xaDataSources.get(0).getXAConnection();
//        XAConnection xaConn1a = xaDataSources.get(1).getXAConnection();
        XAConnection xaConn2 = xaDataSources.get(2).getXAConnection();
        XAConnection xaConn3 = xaDataSources.get(3).getXAConnection();
        XAConnection xaConn4 = xaDataSources.get(4).getXAConnection();
        XAConnection xaConn5 = xaDataSources.get(5).getXAConnection();
		
        XAResource xaRes1 = xaConn1.getXAResource();
//        XAResource xaRes1a = xaConn1a.getXAResource();
        XAResource xaRes2 = xaConn2.getXAResource();
        XAResource xaRes3 = xaConn3.getXAResource();
        XAResource xaRes4 = xaConn4.getXAResource();
        XAResource xaRes5 = xaConn5.getXAResource();
        
		Connection conn1 = xaConn1.getConnection();
//		Connection conn1a = xaConn1a.getConnection();
		Connection conn2 = xaConn2.getConnection();
		Connection conn3 = xaConn3.getConnection();
		Connection conn4 = xaConn4.getConnection();
		Connection conn5 = xaConn5.getConnection();
		
		Xid xid1 = XAUtils.createXid(2, 1);
//		Xid xid1a = createXid(2, 11);
		Xid xid2 = XAUtils.createXid(2, 2);
		Xid xid3 = XAUtils.createXid(2, 3);
		Xid xid4 = XAUtils.createXid(2, 4);
		Xid xid5 = XAUtils.createXid(2, 5);
		
		
		try {
			
			xaRes1.start(xid1, XAResource.TMNOFLAGS);
			try {
				// If there were unique constraint on the col1, the second INSERT would fail,
				// but it would NOT lead to rolling back of the entire branch
				// This is true for Oracle, MySQL 5.x, SQL Server,
				// But not for PostgreSQL 9.0
				conn1.createStatement().execute("insert into table1 (col1, col2) values ('test02', 2)");
//				conn1.createStatement().execute("insert into table1 (col1, col2) values ('test02', 2)");
			} catch (Exception e) {
				e.printStackTrace();
			}
			xaRes1.end(xid1, XAResource.TMSUCCESS);
//			xaRes1a.start(xid1a, XAResource.TMNOFLAGS);
//			try {
//				conn1a.createStatement().execute("insert into table1 (col1, col2) values ('test02a', 2)");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			xaRes1a.end(xid1a, XAResource.TMFAIL);
			
			xaRes2.start(xid2, XAResource.TMNOFLAGS);
			try {
				conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('test01', 'test02', 1)");
//				conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('test01', 'test02', 1)");
			} catch (Exception e) {
				e.printStackTrace();
			}
			xaRes2.end(xid2, XAResource.TMSUCCESS);
			
			xaRes3.start(xid3, XAResource.TMNOFLAGS);
			try {
				conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('test01', 'test02', 1)");
//				conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('test01', 'test02', 1)");
			} catch (Exception e) {
				e.printStackTrace();
			}
			xaRes3.end(xid3, XAResource.TMSUCCESS);
			
			xaRes4.start(xid4, XAResource.TMNOFLAGS);
			try {
				conn4.createStatement().execute("insert into table1 (col1, col2, col3) values ('test01', 'test02', 1)");
//				conn4.createStatement().execute("insert into table1 (col1, col2, col3) values ('test01', 'test02', 1)");
			} catch (Exception e) {
				e.printStackTrace();
			}
			xaRes4.end(xid4, XAResource.TMSUCCESS);
			
			xaRes5.start(xid5, XAResource.TMNOFLAGS);
			try {
				conn5.createStatement().execute("insert into table1 (col1, col2, col3) values ('test01', 'test02', 1)");
//				conn5.createStatement().execute("insert into table1 (col1, col2, col3) values ('test01', 'test02', 1)");
			} catch (Exception e) {
				e.printStackTrace();
			}
			xaRes5.end(xid5, XAResource.TMSUCCESS);
			
			// While Oracle allows preparation of branches without end
			// MySql 5.x do not
			// prepare branch([1], [1]) - 0
			// Exception in thread "main" com.mysql.jdbc.jdbc2.optional.MysqlXAException: XAER_RMFAIL: The command cannot be executed when global transaction is in the  ACTIVE state
			int prep1 = XAUtils.prepare(xid1, xaRes1);
//			int prep1a = prepare(xid1a, xaRes1a);
			int prep2 = XAUtils.prepare(xid2, xaRes2);
			int prep3 = XAUtils.prepare(xid3, xaRes3);
			int prep4 = XAUtils.prepare(xid4, xaRes4);
			int prep5 = XAUtils.prepare(xid5, xaRes5);
			
			XAUtils.commit(xid1, xaRes1, prep1);
//			commit(xid1a, xaRes1a, prep1a);
			XAUtils.commit(xid2, xaRes2, prep2);
			XAUtils.commit(xid3, xaRes3, prep3);
			XAUtils.commit(xid4, xaRes4, prep4);
			XAUtils.commit(xid5, xaRes5, prep5);
			
//			rollback(xid1, xaRes1, prep1);
//			rollback(xid2, xaRes2, prep2);
//			rollback(xid3, xaRes3, prep3);
//			rollback(xid4, xaRes4, prep4);
//			rollback(xid5, xaRes5, prep5);
			
			// While Oracle allows rolling back of branches without end
			// MySql 5.x do not
			// Rolling back branch com.mysql.jdbc.jdbc2.optional.MysqlXid@1 on oracle.jdbc.driver.T4CXAResource@50d89c
			// Rolling back branch com.mysql.jdbc.jdbc2.optional.MysqlXid@1 on com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection@1bd0dd4
			// Rolling back branch com.mysql.jdbc.jdbc2.optional.MysqlXid@1 on com.mysql.jdbc.jdbc2.optional.JDBC4MysqlXAConnection@eb017e
			// com.mysql.jdbc.jdbc2.optional.MysqlXAException: XAER_RMFAIL: The command cannot be executed when global transaction is in the  ACTIVE state
			// com.mysql.jdbc.jdbc2.optional.MysqlXAException: XAER_RMFAIL: The command cannot be executed when global transaction is in the  ACTIVE state
//			rollback(xid1, xaRes1);
//			rollback(xid2, xaRes2);
//			rollback(xid3, xaRes3);
//			rollback(xid4, xaRes4);
//			rollback(xid5, xaRes5);
			
		} catch (Exception e) {
			e.printStackTrace();
//			rollback(xid1, xaRes1);
//			rollback(xid1a, xaRes1a);
//			rollback(xid2, xaRes2);
//			rollback(xid3, xaRes3);
//			rollback(xid4, xaRes4);
//			rollback(xid5, xaRes5);
		}
		
		conn1.close();
//		conn1a.close();
		conn2.close();
		conn3.close();
		conn4.close();
		conn5.close();
		
		xaConn1.close();
//		xaConn1a.close();
		xaConn2.close();
		xaConn3.close();
		xaConn4.close();
		xaConn5.close();
	}
	
	protected static void testWithXARessWithCommittedAndRolledBackBranches() throws Exception {
		
		List<XADataSource> xaDataSources = XAUtils.getXADataSources();
		List<XAConnection> xaConnections = XAUtils.getXAConnections(xaDataSources);
		List<XAResource> xaResources = XAUtils.getXaResources(xaConnections);
		List<Connection> connections = XAUtils.getConnections(xaConnections);
		
        XAResource xaRes1 = xaResources.get(0);
//        XAResource xaRes2 = xaResources.get(1);
        XAResource xaRes3 = xaResources.get(1);
        
		Xid xid1 = XAUtils.createXid(1, 1);
//		Xid xid2 = createXid(1, 2);
		Xid xid3 = XAUtils.createXid(1, 3);
		
		xaRes1.start(xid1, XAResource.TMNOFLAGS);
//		xaRes2.start(xid2, XAResource.TMNOFLAGS);
		xaRes3.start(xid3, XAResource.TMNOFLAGS);
		
		connections.get(0).createStatement().execute("insert into table1 (col1, col2) values ('test tmfail rollback', 2)");
//		connections.get(1).createStatement().execute("insert into table1 (col1, col2) values ('test03', 3)");
		connections.get(1).createStatement().execute("insert into table1 (col1, col2, col3) values ('test tmfail rollback', 'test02', 1)");
		
		xaRes1.end(xid1, XAResource.TMSUCCESS);
//		xaRes2.end(xid2, XAResource.TMSUCCESS);
		xaRes3.end(xid3, XAResource.TMFAIL);
		
		int prep1 = xaRes1.prepare(xid1);
//		int prep2 = xaRes2.prepare(xid2);
//		int prep3 = xaRes3.prepare(xid3);
		
		System.out.println("xaRes1.prepare(xid1): " + prep1);
//		System.out.println("xaRes2.prepare(xid2): " + prep2);
//		System.out.println("xaRes3.prepare(xid3): " + prep3);
		
		XAUtils.commit(xid1, xaRes1, prep1);
//		commit(xid2, xaRes2, prep2);
		XAUtils.rollback(xid3, xaRes3);
		
		XAUtils.closeConnections(connections);
		XAUtils.closeXAConnections(xaConnections);
	}
}

