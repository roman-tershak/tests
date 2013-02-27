package jta_tests.test02;

import static jta_tests.test02.TransUtil.delistSuccess;
import static jta_tests.test02.TransUtil.enlist;

import java.sql.Connection;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

import oracle.jdbc.xa.client.OracleXADataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class MultithreadingAndDistrTransTest {

	public static void main(String[] args) {
		try {
			final TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

			List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3306/test", "root", "12345"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3307/test", "root", "12345"}
			);
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			final XAResource xaRes1 = xaRess.get(0);
			final XAResource xaRes2 = xaRess.get(1);
			final XAResource xaRes3 = xaRess.get(2);

			final Connection conn1 = conns.get(0);
			final Connection conn2 = conns.get(1);
			final Connection conn3 = conns.get(2);

			UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
			
			userTransaction.begin();
			final Transaction trans = transactionManager.getTransaction();
			System.out.println(trans);

			Thread thread1 = new Thread() {
				@Override
				public void run() {
					try {
						UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
						userTransaction.begin();
						Transaction trans = transactionManager.getTransaction();
						System.out.println(trans);
						
						enlist(trans, xaRes1);
						conn1.createStatement().execute("insert into table1 (col1, col2) values ('test02', 2)");
						delistSuccess(trans, xaRes1);
						
						userTransaction.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			Thread thread2 = new Thread() {
				@Override
				public void run() {
					try {
						transactionManager.begin();
						Transaction trans = transactionManager.getTransaction();
						System.out.println(trans);
						
						enlist(trans, xaRes2);
						conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('test03', 'test04', 3)");
						delistSuccess(trans, xaRes2);
						
						transactionManager.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			Thread thread3 = new Thread() {
				@Override
				public void run() {
					try {
						enlist(trans, xaRes3);
						System.out.println(transactionManager.getTransaction());
						conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('test04', 'test05', 4)");
						delistSuccess(trans, xaRes3);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			thread1.start();
			thread2.start();
			thread3.start();
			
			thread1.join();
			thread2.join();
			thread3.join();
			
			userTransaction.rollback();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
