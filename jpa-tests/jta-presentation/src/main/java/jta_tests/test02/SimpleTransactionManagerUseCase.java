package jta_tests.test02;

import jta_tests.datasources.XAMsSqlDataSource;
import jta_tests.datasources.XAMySqlDataSource;
import jta_tests.datasources.XAOracleDataSource;
import jta_tests.datasources.XAPGDataSource;

public class SimpleTransactionManagerUseCase {

	public static void main(String[] args) throws Exception {
		javax.sql.XAConnection xaConn1 = null;
		javax.sql.XAConnection xaConn2 = null;
		javax.sql.XAConnection xaConn3 = null;
		java.sql.Connection conn1 = null;
		java.sql.Connection conn2 = null;
		java.sql.Connection conn3 = null;
		javax.transaction.UserTransaction userTransaction = null;
		
		try {
			javax.transaction.TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
			userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();

			System.out.println("Getting datasources");
			javax.sql.XADataSource xaDataSource1 = new XAOracleDataSource();
//			javax.sql.XADataSource xaDataSource2 = new XAMsSqlDataSource();
			javax.sql.XADataSource xaDataSource3 = new XAPGDataSource();
			
			System.out.println("Openning physical connections");
			xaConn1 = xaDataSource1.getXAConnection();
//			xaConn2 = xaDataSource2.getXAConnection();
			xaConn3 = xaDataSource3.getXAConnection();
			
			System.out.println("Getting XA Resources");
			javax.transaction.xa.XAResource xaRes1 = xaConn1.getXAResource();
//			javax.transaction.xa.XAResource xaRes2 = xaConn2.getXAResource();
			javax.transaction.xa.XAResource xaRes3 = xaConn3.getXAResource();

			System.out.println("Getting logical connections");
			conn1 = xaConn1.getConnection();
//			conn2 = xaConn2.getConnection();
			conn3 = xaConn3.getConnection();

			//------------------------------------------------------------------------------------------------------
			
			System.out.println("Starting transaction");
			userTransaction.begin();
			javax.transaction.Transaction trans = transactionManager.getTransaction();

			System.out.println("Enlisting resources to " + trans);
			trans.enlistResource(xaRes1);
//			trans.enlistResource(xaRes2);
			trans.enlistResource(xaRes3);
			
			//------------------------------------------------------------------------------------------------------
			
			long currentTimeMillis = System.currentTimeMillis();
			
			System.out.println("Executing SQL statements");
			conn1.createStatement().execute("insert into jta_pres_tbl (pkey, col2) values (" + currentTimeMillis + ", 'simple DTP use case')");
//			conn2.createStatement().execute("insert into jta_pres_tbl (pkey, col2) values (" + currentTimeMillis + ", 'simple DTP use case')");
			conn3.createStatement().execute("insert into jta_pres_tbl (pkey, col2) values (" + currentTimeMillis + ", 'simple DTP use case')");

			//------------------------------------------------------------------------------------------------------
			
			System.out.println("Delisting resources from " + trans); 
			trans.delistResource(xaRes1, javax.transaction.xa.XAResource.TMSUCCESS);
//			trans.delistResource(xaRes2, javax.transaction.xa.XAResource.TMSUCCESS);
			trans.delistResource(xaRes3, javax.transaction.xa.XAResource.TMSUCCESS);
			
			System.out.println("Committing transaction");
			userTransaction.commit();
			
		} catch (Throwable e) {
			e.printStackTrace();
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			conn1.close();
//			conn2.close();
			conn3.close();
			
			xaConn1.close();
//			xaConn2.close();
			xaConn3.close();
		}
	}

}
