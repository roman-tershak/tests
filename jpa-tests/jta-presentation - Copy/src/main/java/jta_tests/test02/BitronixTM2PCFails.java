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

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class BitronixTM2PCFails {
	
	public static void main(String[] args) throws Exception {
		System.setProperty("bitronix.tm.configuration", "D:\\Tests\\jpa-workspace\\jta-presentation\\src\\main\\resources\\btm-config.properties");
		System.setProperty("java.naming.factory.initial", "bitronix.tm.jndi.BitronixInitialContextFactory");
		
		testSimple();
	}
	
	public static void testSimple() throws Exception {
		
		TransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
		
		List<PoolingDataSource> dataSources = getDataSources();
		List<Connection> connections = getConnections(dataSources);
		
		Transaction transaction = null;
		try {
			transactionManager.begin();
			transactionManager.setTransactionTimeout(300);
			transaction = transactionManager.getTransaction();

			long currentTimeMillis = System.currentTimeMillis();
			connections.get(0).createStatement().executeUpdate("insert into jta_pres_tbl (pkey, col2) values (" + currentTimeMillis + ", 'Bitronix TM 2PC Fails')");
			connections.get(1).createStatement().executeUpdate("insert into jta_pres_tbl (pkey, col2) values (" + currentTimeMillis + ", 'Bitronix TM 2PC Fails')");
			connections.get(2).createStatement().executeUpdate("insert into jta_pres_tbl (pkey, col2) values (" + currentTimeMillis + ", 'Bitronix TM 2PC Fails')");
//			connections.get(3).createStatement().executeUpdate("insert into jta_pres_tbl (pkey, col2) values (" + currentTimeMillis + ", 'Bitronix TM 2PC Fails')");
			
			transaction.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			XAUtils.closeConnections(connections);
		}
	}

	private static List<PoolingDataSource> getDataSources() throws NamingException {
		List<PoolingDataSource> xaDataSources = new LinkedList<PoolingDataSource>();
		Context context = new InitialContext();
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsPostgreSql"));
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsMySql"));
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsOracle"));
//		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsSqlServer"));
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
