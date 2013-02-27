package jta_tests.test01;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

import com.arjuna.ats.arjuna.recovery.RecoveryManager;

import jta_tests.test02.TransUtil;
import jta_tests.test02.XAUtils;

public class RecoveryProcess {

	static {
		System.setProperty("com.arjuna.common.util.propertyservice.verbosePropertyManager", "ON");
		System.setProperty("com.arjuna.ats.jta.common.propertiesFile", "jbossjts-properties.xml");
	}
	
	public static void main(String[] args) throws Exception {
		TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
		RecoveryManager recoveryManager = RecoveryManager.manager();
		recoveryManager.startRecoveryManagerThread();
		
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		List<XADataSource> xaDataSources = XAUtils.getXADataSources(true, false, true, true, true, true);
		
		int uniqueNum = 6;
		for (int i = 0; i < 1000; i++) {
			
			while (true) {
				if ("go".equals(input.readLine())) {
					break;
				}
				Thread.yield();
			}
			
			List<XAConnection> xaConnections = XAUtils.getXAConnections(xaDataSources);
			List<Connection> connections = XAUtils.getConnections(xaConnections);
			List<XAResource> xaResources = XAUtils.getXaResourcesWithProxies(xaConnections);
			
			UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();

			try {
				userTransaction.begin();
				userTransaction.setTransactionTimeout(600);
				Transaction transaction = transactionManager.getTransaction();

				TransUtil.enlist(transaction, xaResources);

				connections.get(0).createStatement().execute("insert into table1 (col1, col2) values ('test recovery "+uniqueNum+"_"+i+"', 2)");
				connections.get(1).createStatement().execute("insert into table1 (col1, col2, col3) values ('test recovery "+uniqueNum+"_"+i+"', 'test04', 3)");
				connections.get(2).createStatement().execute("insert into table1 (col1, col2, col3) values ('test recovery "+uniqueNum+"_"+i+"', 'test04', 3)");
				connections.get(3).createStatement().execute("insert into table1 (col1, col2, col3) values ('test recovery "+uniqueNum+"_"+i+"', 'test04', 3)");
				connections.get(4).createStatement().execute("insert into table1 (col1, col2, col3) values ('test recovery "+uniqueNum+"_"+i+"', 'test04', 3)");

				TransUtil.delistSuccess(transaction, xaResources);

				userTransaction.rollback();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					userTransaction.rollback();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} finally {
				XAUtils.closeConnections(connections);
				XAUtils.closeXAConnections(xaConnections);
			}
		}
		
		recoveryManager.stop();
	}
}
