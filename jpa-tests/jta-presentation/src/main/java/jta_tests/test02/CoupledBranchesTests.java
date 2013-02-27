package jta_tests.test02;

import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import jta_tests.datasources.TransUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class CoupledBranchesTests {
	
	private TransactionManager transactionManager;
	private List<Connection> connections = new LinkedList<Connection>();
	private Transaction transaction;
	private Context context;

	@Before
	public void before() throws Exception {
		System.setProperty("bitronix.tm.configuration", "D:\\Tests\\jpa-workspace\\jta-presentation\\src\\main\\resources\\btm-config.properties");
		System.setProperty("java.naming.factory.initial", "bitronix.tm.jndi.BitronixInitialContextFactory");
		
		TransUtils.pauseOnPrepare = false;
		TransUtils.pauseOnCommit = false;
		
		context = new InitialContext();
		transactionManager = TransactionManagerServices.getTransactionManager();
		
		transactionManager.begin();
		transactionManager.setTransactionTimeout(10);
		
		transaction = transactionManager.getTransaction();
	}
	
	@After
	public void after() throws Exception {
		XAUtils.closeConnections(connections);
	}
	
	@Test
	public void testOnPostgres() throws Exception {
		
		Connection conn1 = ((PoolingDataSource) context.lookup("jdbc/dsPostgreSql")).getConnection();
		Connection conn2 = ((PoolingDataSource) context.lookup("jdbc/dsPostgreSql")).getConnection();
		
		Thread thread1 = new ThreadWorker(conn1.createStatement(), "update coupled_branches_tests set col2 = col2 + 1 where pkey = 1", 5);
//		Thread thread2 = new ThreadWorker(conn2.createStatement(), "update coupled_branches_tests set col2 = col2 + 1 where pkey = 1", 5);
		
		Util.startThreads(thread1/*,thread2*/);
		Util.joinThreads(thread1/*, thread2*/);
		
		transaction.commit();
	}
	
	@Test
	public void testOnMySql() throws Exception {
		
		Connection conn1 = ((PoolingDataSource) context.lookup("jdbc/dsMySql")).getConnection();
		Connection conn2 = ((PoolingDataSource) context.lookup("jdbc/dsMySql")).getConnection();
		
		Thread thread1 = new ThreadWorker(conn1.createStatement(), "update coupled_branches_tests set col2 = col2 + 1 where pkey = 1", 5);
		Thread thread2 = new ThreadWorker(conn2.createStatement(), "update coupled_branches_tests set col2 = col2 + 1 where pkey = 1", 5);
		
		Util.startThreads(thread1, thread2);
		Util.joinThreads(thread1, thread2);
		
		transaction.commit();
	}
	
	@Test
	public void testOnOracle() throws Exception {
		
		Connection conn1 = ((PoolingDataSource) context.lookup("jdbc/dsOracle")).getConnection();
		Connection conn2 = ((PoolingDataSource) context.lookup("jdbc/dsOracle")).getConnection();
		
		Thread thread1 = new ThreadWorker(conn1.createStatement(), "update coupled_branches_tests set col2 = col2 + 1 where pkey = 1", 5);
		Thread thread2 = new ThreadWorker(conn2.createStatement(), "update coupled_branches_tests set col2 = col2 + 1 where pkey = 1", 5);
		
		Util.startThreads(thread1, thread2);
		Util.joinThreads(thread1, thread2);
		
		transaction.commit();
	}
	
	private class ThreadWorker extends Thread {
		private String sql;
		private int numOfRuns;
		private Statement stmt;

		public ThreadWorker(Statement stmt, String sql, int numOfRuns) {
			this.stmt = stmt;
			this.sql = sql;
			this.numOfRuns = numOfRuns;
		}

		@Override
		public void run() {
			try {
				for (int i = 0; i < numOfRuns; i++) {
					System.out.println("Thread - " + this + ", Executing SQL: " + sql + ", iteration: " + i);
					stmt.executeUpdate(sql);
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					transaction.setRollbackOnly();
				} catch (Exception e1) {
				}
			}
		}
	}
	
	@Test
	public void testRunRecovery() throws Exception {
		TransactionManagerServices.getTransactionManager();
	}
	
}
