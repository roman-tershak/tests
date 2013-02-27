package jta_tests.test02;

import static jta_tests.test02.TransUtil.joinThreads;
import static jta_tests.test02.TransUtil.startThreads;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import oracle.jdbc.xa.client.OracleXADataSource;

import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;

public class CoupledBranchesTest {

	public static void main(String[] args) throws SQLException {
		final List<XAConnection> xaConns = new LinkedList<XAConnection>();
		
		UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
		try {
			TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

			final List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {SQLServerXADataSource.class, "jdbc:sqlserver://localhost:1433/test", "test", "test!2"}
			);

			userTransaction.begin();
			Transaction tran = transactionManager.getTransaction();
			
			// While Oracle's branches are tightly coupled and see updates of each other
			Thread thread1 = new ThreadWorker(tran, xaDataSources.get(0), 
					"update table1 set col2 = col2 + 1", 2, xaConns);
			Thread thread1a = new ThreadWorker(tran, xaDataSources.get(0), 
					"update table1 set col2 = col2 + 1", 2, xaConns);
			Thread thread1b = new ThreadWorker(tran, xaDataSources.get(0), 
					"update table1 set col2 = col2 + 1", 2, xaConns);
			// ... MS SQL's are loosely coupled and block each other
			Thread thread2 = new ThreadWorker(tran, xaDataSources.get(1), 
					"update table1 set col2 = col2 + 1", 1, xaConns);
			Thread thread2a = new ThreadWorker(tran, xaDataSources.get(1), 
					"update table1 set col2 = col2 + 1", 0, xaConns);
			Thread thread2b = new ThreadWorker(tran, xaDataSources.get(1), 
					"update table1 set col2 = col2 + 1", 0, xaConns);

			startThreads(thread1, thread1a, thread1b, thread2, thread2a, thread2b);
			joinThreads(thread1, thread1a, thread1b, thread2, thread2a, thread2b);
			
			userTransaction.commit();
//			userTransaction.rollback();
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			XAUtils.closeXAConnections(xaConns);
		}
	}

}
