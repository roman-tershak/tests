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

import jta_tests.datasources.XAMsSqlDataSource;
import jta_tests.datasources.XAMySqlDataSource;
import jta_tests.datasources.XAOracleDataSource;
import jta_tests.datasources.XAPGDataSource;

public class CoupledBranchesTest {

	public static void main(String[] args) throws SQLException {
		final List<XAConnection> xaConns = new LinkedList<XAConnection>();
		
		UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
		try {
			TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

			XADataSource xaDataSource1 = new XAOracleDataSource();
			XADataSource xaDataSource2 = new XAMySqlDataSource();
			XADataSource xaDataSource3 = new XAMsSqlDataSource();
			XADataSource xaDataSource4 = new XAPGDataSource();

			userTransaction.begin();
			Transaction tran = transactionManager.getTransaction();
			
//			Thread thread1 = new ThreadWorker(tran, xaDataSource1,
//					"update table1 set col2 = col2 + 1", 1, xaConns);
//			Thread thread1a = new ThreadWorker(tran, xaDataSource1, 
//					"update table1 set col2 = col2 + 1", 1, xaConns);
			
//			Thread thread2 = new ThreadWorker(tran, xaDataSource2, 
//					"update table1 set col3 = col3 + 1", 1, xaConns);
//			Thread thread2a = new ThreadWorker(tran, xaDataSource2, 
//					"update table1 set col3 = col3 + 1", 1, xaConns);
//
			Thread thread3 = new ThreadWorker(tran, xaDataSource3, 
					"update table1 set col3 = col3 + 1", 1, xaConns);
			Thread thread3a = new ThreadWorker(tran, xaDataSource3, 
					"update table1 set col3 = col3 + 1", 1, xaConns);
//			
//			Thread thread4 = new ThreadWorker(tran, xaDataSource4, 
//					"update table1 set col3 = col3 + 1", 1, xaConns);
//			Thread thread4a = new ThreadWorker(tran, xaDataSource4, 
//					"update table1 set col3 = col3 + 1", 1, xaConns);
			
			startThreads(/*thread1, thread1athread2, thread2a,*/ thread3, thread3a/*, thread4, thread4a*/);
			joinThreads(/*thread1, thread1a thread2, thread2a*/ thread3, thread3a/*, thread4, thread4a*/);
			
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
