package jta_tests.test02;

import static jta_tests.test02.TransUtil.delistFail;
import static jta_tests.test02.TransUtil.enlist;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

import oracle.jdbc.xa.client.OracleXADataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class MultithreadingAndTransactionManagerTest {

	private static final int NUM_OF_QUERIES = 1;

	public static void main(String[] args) {
		System.setProperty("com.arjuna.ats.jta.common.propertiesFile", "/jbossts-properties.xml");
		
		UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
		try {
			final TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

			List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3306/test", "root", "12345"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3307/test", "root", "12345"}
			);
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			List<XAResource> xaRess = XAUtils.getXaResourcesWithProxies(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			final XAResource xaRes1 = xaRess.get(0);
			final XAResource xaRes2 = xaRess.get(1);
			final XAResource xaRes3 = xaRess.get(2);

			final Connection conn1 = conns.get(0);
			final Connection conn2 = conns.get(1);
			final Connection conn3 = conns.get(2);

			userTransaction.begin();
//			transactionManager.begin();
			final Transaction trans = transactionManager.getTransaction();
			System.out.println(trans);
			
			final Date now = new Date();

			Thread thread1 = new Thread() {
				@Override
				public void run() {
					try {
						// When a thread is not a distributed transaction originator, then 
						// transactionManager.getTransaction() will return null.
						for (int i = 0; i < NUM_OF_QUERIES; i++) {
							enlist(trans, xaRes1);
//							conn1.createStatement().execute("insert into table1 (col1, col2) values ('" + now + "', 7)");
							conn1.createStatement().execute("insert into table1 (col1, col2) values ('olialia...', 7)");
							delistFail(trans, xaRes1);
//							delistSuccess(trans, xaRes1);
						}
//						trans.setRollbackOnly();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							trans.setRollbackOnly();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			};
			Thread thread2 = new Thread() {
				@Override
				public void run() {
					try {
						// This is about transaction branches on the same physical connection
						// It does not matter whether the branches belong to the same distributed transaction or to 
						// to different, until the active branch is not committed or rolled back, 
						// other branch cannot be started.
						// It means that only one open transaction (distributed or local) can be active at a time
						// So, repeated enlisting/delisting is not supported in MySQL 5.x
						enlist(trans, xaRes2);
						for (int i = 0; i < NUM_OF_QUERIES; i++) {
//							// This is not supported  --->  enlist(trans, xaRes2);
//							conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('" + now + "', '" + now + "', 3)");
							conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('olialia', '" + now + "', 3)");
//							// This is not supported  --->  delistSuccess(trans, xaRes2);  This is not supported
						}
						delistFail(trans, xaRes2);
//						delistSuccess(trans, xaRes2);
					} catch (Exception e) {
						e.printStackTrace();
						try {
//							trans.setRollbackOnly();
//							delistFail(trans, xaRes2);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			};
			Thread thread3 = new Thread() {
				@Override
				public void run() {
					try {
						// When a thread is not a distributed transaction originator, then 
						// transactionManager.getTransaction() will return null.
						enlist(trans, xaRes3);
						for (int i = 0; i < NUM_OF_QUERIES; i++) {
//							conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('" + now + "', '" + now + "', 4)");
							conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('olialia', '" + now + "', 4)");
						}
						delistFail(trans, xaRes3);
//						delistSuccess(trans, xaRes3);
					} catch (Exception e) {
						e.printStackTrace();
						try {
							trans.setRollbackOnly();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			};
			
			thread1.start();
			thread2.start();
			thread3.start();
			
			thread1.join();
			thread2.join();
			thread3.join();
			
			userTransaction.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
