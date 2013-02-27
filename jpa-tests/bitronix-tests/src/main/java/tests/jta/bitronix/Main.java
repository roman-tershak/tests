package tests.jta.bitronix;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import jta_tests.test02.XAUtils;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class Main {
	
	public static void main(String[] args) throws Exception {
		System.setProperty("bitronix.tm.configuration", "D:\\Tests\\jpa-workspace\\bitronix-tests\\src\\main\\resources\\btm-config.properties");
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

			String num = "tests A15";
			connections.get(0).createStatement().executeUpdate("insert into table1 (col1, col2, col3) values ('btw tests "+num+"', '"+num+"', 1)");
			connections.get(1).createStatement().executeUpdate("insert into table1 (col1, col2, col3) values ('btw tests "+num+"', '"+num+"', 1)");
			connections.get(2).createStatement().executeUpdate("insert into table1 (col1, col2) values ('btw tests "+num+"', 1)");
			connections.get(3).createStatement().executeUpdate("insert into table1 (col1, col2, col3) values ('btw tests "+num+"', '"+num+"', 1)");
			
			transaction.commit();
			
			Thread.sleep(200000);
			
		} catch (Exception e) {
			System.out.println("Exception found:\n\n\n");
			
			e.printStackTrace();
			try {
				transaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			XAUtils.closeConnections(connections);
		}
		Thread.sleep(200000);
	}

	private static List<PoolingDataSource> getDataSources() throws NamingException {
		List<PoolingDataSource> xaDataSources = new LinkedList<PoolingDataSource>();
		Context context = new InitialContext();
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsMySql"));
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsSqlServer"));
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsOracle"));
		xaDataSources.add((PoolingDataSource) context.lookup("jdbc/dsPostgreSql"));
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
