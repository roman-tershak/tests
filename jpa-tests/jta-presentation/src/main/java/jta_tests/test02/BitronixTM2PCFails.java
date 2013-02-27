package jta_tests.test02;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import jta_tests.datasources.TransUtils;

import org.junit.Before;
import org.junit.Test;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class BitronixTM2PCFails {
	
	@Before
	public void before() {
		System.setProperty("bitronix.tm.configuration", "D:\\Tests\\jpa-workspace\\jta-presentation\\src\\main\\resources\\btm-config.properties");
		System.setProperty("java.naming.factory.initial", "bitronix.tm.jndi.BitronixInitialContextFactory");
		
		TransUtils.pauseOnPrepare = false;
		TransUtils.pauseOnCommit = false;
	}
	
	@Test
	public void testSimple() throws Exception {
		
		TransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
		
		List<PoolingDataSource> dataSources = getDataSources();
		List<Connection> connections = getConnections(dataSources);
		
		Transaction transaction = null;
		try {
			transactionManager.begin();
			transactionManager.setTransactionTimeout(600);
			transaction = transactionManager.getTransaction();

			String sql = "insert into jta_pres_tbl (pkey, col2) values (" + System.currentTimeMillis() + ", 'Bitronix TM 2PC Fail Tests')";
			System.out.println(sql);
			
			connections.get(0).createStatement().executeUpdate(sql);
			connections.get(1).createStatement().executeUpdate(sql);
			connections.get(2).createStatement().executeUpdate(sql);
			
			transaction.commit();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			XAUtils.closeConnections(connections);
		}
	}
	
	@Test
	public void testFailureOnPrepare() throws Exception {
		TransUtils.pauseOnPrepare = true;
		
		testSimple();
	}
	
	@Test
	public void testFailureOnCommit() throws Exception {
		TransUtils.pauseOnCommit = true;
		
		testSimple();
	}

	@Test
	public void testRunRecovery() throws Exception {
		TransactionManagerServices.getTransactionManager();
	}
	
	private static List<PoolingDataSource> getDataSources() throws NamingException {
		List<PoolingDataSource> xaDataSources = new LinkedList<PoolingDataSource>();
		Context context = new InitialContext();
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsPostgreSql"));
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsMySql"));
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsOracle"));
		return xaDataSources;
	}

	private static List<Connection> getConnections(List<PoolingDataSource> xaDataSources) throws SQLException {
		List<Connection> connections = new LinkedList<Connection>();
		for (PoolingDataSource poolingDataSource : xaDataSources) {
			connections.add(poolingDataSource.getConnection());
		}
		return connections;
	}

}
