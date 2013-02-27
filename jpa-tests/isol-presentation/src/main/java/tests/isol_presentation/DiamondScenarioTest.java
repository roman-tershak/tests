package tests.isol_presentation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import tests.isol_presentation.datasources.OracleXADataSourceWrapper;

public class DiamondScenarioTest {

	static final int NUM_OF_THREADS = 2;
	
	private static final class ThreadWorker extends Thread {
		
		private final Transaction transaction;
		private final AtomicInteger stepSynchronizer;
		private final int debit;
		
		private Connection connection;
		private XAResource xaResource;
		
		public ThreadWorker(XADataSource xaDataSource, Transaction transaction, AtomicInteger stepSynchronizer, int debit) throws Exception {
			this.transaction = transaction;
			this.stepSynchronizer = stepSynchronizer;
			this.debit = debit;
			
			XAConnection xaConnection = xaDataSource.getXAConnection();
			connection = xaConnection.getConnection();
			
			if (transaction != null) {
				xaResource = xaConnection.getXAResource();
			}
			
			System.out.println("Thread: " + this + ", XAConnection: " + xaConnection);
		}

		@Override
		public void run() {
			try {
				enlistToTransaction();
				
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				
				synchronizeStep();
				
				ResultSet resultSet = executeRequest("SELECT * FROM TESTS.TABLE_DT_DIAMOND_SCENARIO WHERE ID = 1");
				BigDecimal balance = getBalance(resultSet);
				BigDecimal newBalance = balance.add(new BigDecimal(debit));
				
				synchronizeStep();
				
				executeUpdate("UPDATE TESTS.TABLE_DT_DIAMOND_SCENARIO SET BALANCE = " + newBalance + " WHERE ID = 1");
				
				synchronizeStep();
				
				getBalance( executeRequest("SELECT * FROM TESTS.TABLE_DT_DIAMOND_SCENARIO WHERE ID = 1") );
				
				synchronizeStep();
				
				delistFromTransaction();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void synchronizeStep() {
			stepSynchronizer.incrementAndGet();
			while ((stepSynchronizer.get() % NUM_OF_THREADS) != 0) {
				yield();
			}
		}

		private BigDecimal getBalance(ResultSet resultSet) throws SQLException {
			resultSet.next();
			BigDecimal balance = resultSet.getBigDecimal("BALANCE");
			System.out.println("Thread: " + this + ", balance: " + balance);
			return balance;
		}
		
		private void executeUpdate(String sql) throws SQLException {
			System.out.println("Thread: " + this + ", executing SQL: " + sql + " on " + connection);
			connection.createStatement().executeUpdate(sql);
		}

		private ResultSet executeRequest(String sql) throws SQLException {
			System.out.println("Thread: " + this + ", executing SQL: " + sql + " on " + connection);
			return connection.createStatement().executeQuery(sql);
		}

		private void enlistToTransaction() throws RollbackException, SystemException {
			System.out.println("Thread: " + this + ", enlisting XAResource: " + xaResource + " to " + transaction);
			if (transaction != null) {
				transaction.enlistResource(xaResource);
			} else {
				System.out.println("Skipping...");
			}
		}
		
		private void delistFromTransaction() throws RollbackException, SystemException {
			System.out.println("Thread: " + this + ", delisting XAResource: " + xaResource + " from " + transaction);
			if (transaction != null) {
				transaction.delistResource(xaResource, XAResource.TMSUCCESS);
			} else {
				System.out.println("Skipping...");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
		
		AtomicInteger stepSynchronizer = new AtomicInteger(0);
		
		XADataSource xaDataSource1 = new OracleXADataSourceWrapper("jdbc:oracle:thin:@localhost:1521:XE", "tests", "tests");
		XADataSource xaDataSource2 = new OracleXADataSourceWrapper("jdbc:oracle:thin:@localhost:1521:XE", "tests2", "tests");
		
		transactionManager.begin();
		Transaction transaction = transactionManager.getTransaction();
//		Transaction transaction = null;
		
		Thread thread1 = new ThreadWorker(xaDataSource1, transaction, stepSynchronizer, 75);
		Thread thread2 = new ThreadWorker(xaDataSource2, transaction, stepSynchronizer, 150);
		
		thread1.start();
		thread2.start();
		
		thread1.join();
		thread2.join();
		
		transactionManager.commit();
	}

}
