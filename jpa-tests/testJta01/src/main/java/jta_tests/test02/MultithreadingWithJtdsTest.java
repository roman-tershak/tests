package jta_tests.test02;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import oracle.jdbc.xa.client.OracleXADataSource;

import org.postgresql.xa.PGXADataSource;

import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class MultithreadingWithJtdsTest {

	protected static final int NUM_OF_RUNS = 2;
	
	public static void main(String[] args) throws SQLException {
		final List<XAConnection> xaConns = new LinkedList<XAConnection>();
		
		UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
		try {
			TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

			final List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3306/test", "root", "12345"},
					new Object[] {SQLServerXADataSource.class, "jdbc:sqlserver://localhost:1433/test", "test", "test!2"},
//					new Object[] {JtdsDataSource.class, "jdbc:jtds:sqlserver://localhost:1433/test", "test", "test!2"},
					new Object[] {PGXADataSource.class, "jdbc:postgresql://localhost:5432/postgres", "postgres", "PostfreSql!2"}
			);

			userTransaction.begin();
			Transaction tran = transactionManager.getTransaction();
			
			Thread thread1 = new ThreadWorker(tran, xaDataSources.get(0), 
					"insert into table1 (col1, col2) values ('test02', 2)", 2, xaConns);
			Thread thread2 = new ThreadWorker(tran, xaDataSources.get(1), 
					"insert into table1 (col1, col2, col3) values ('test03', 'test04', 3)", 1, xaConns);
//			Thread thread2a = new ThreadWorker(tran, xaDataSources.get(1), 
//					"insert into table1 (col1, col2, col3) values ('test03', 'test04', 3)", 1, xaConns);
//			Thread thread2b = new ThreadWorker(tran, xaDataSources.get(1), 
//					"insert into table1 (col1, col2, col3) values ('test03', 'test04', 3)", 1, xaConns);
			Thread thread3 = new ThreadWorker(tran, xaDataSources.get(2), 
					"insert into table1 (col1, col2) values ('test with ms sql jdbc', 4)", 5, xaConns);
//			Thread thread3a = new ThreadWorker(tran, xaDataSources.get(2), 
//					"insert into table1 (col1, col2) values ('test jtds', 4)", 2, xaConns);
//			Thread thread3b = new ThreadWorker(tran, xaDataSources.get(2), 
//					"insert into table1 (col1, col2) values ('test jtds', 4)", 2, xaConns);
			Thread thread4 = new ThreadWorker(tran, xaDataSources.get(3), 
					"insert into table1 (col1, col2) values (5, 'test postgres')", 1, xaConns);

			TransUtil.startThreads(
					thread1, 
					thread2, 
//					thread2a, 
//					thread2b, 
					thread3,
//					thread3a,
//					thread3b, 
					thread4
			);
			TransUtil.joinThreads(
					thread1, 
					thread2, 
//					thread2a, 
//					thread2b, 
					thread3,
//					thread3a, 
//					thread3b, 
					thread4
			);
			
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
