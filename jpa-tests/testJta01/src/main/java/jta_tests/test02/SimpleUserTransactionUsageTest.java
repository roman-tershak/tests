package jta_tests.test02;

import static jta_tests.test02.TransUtil.delistSuccess;
import static jta_tests.test02.TransUtil.enlist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

import oracle.jdbc.xa.client.OracleXADataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class SimpleUserTransactionUsageTest {

	public static void main(String[] args) throws SQLException {
		List<XAConnection> xaConns = null;
		List<Connection> conns = null;
		UserTransaction userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
		try {
			TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();

			List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3306/test", "root", "12345"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3307/test", "root", "12345"}
			);
			xaConns = XAUtils.getXAConnections(xaDataSources);
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			conns = XAUtils.getConnections(xaConns);

			XAResource xaRes1 = xaRess.get(0);
			XAResource xaRes2 = xaRess.get(1);
			XAResource xaRes3 = xaRess.get(2);

			Connection conn1 = conns.get(0);
			Connection conn2 = conns.get(1);
			Connection conn3 = conns.get(2);

			userTransaction.begin();
			Transaction trans = transactionManager.getTransaction();

			enlist(trans, xaRes1);
			enlist(trans, xaRes2);
			enlist(trans, xaRes3);

			conn1.createStatement().execute("insert into table1 (col1, col2) values ('test02', 2)");
			conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('test03', 'test04', 3)");
			conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('test04', 'test05', 4)");
//			Rollback by an exception works fine
//			if (1 == 1) {
//				throw new RuntimeException();
//			}
			
//			When this method is called, then userTransaction.rollback() should be called after,
//			in other case (e.g. on commit) an exception will be thrown and the transaction will became inactive.
//			userTransaction.setRollbackOnly();

			delistSuccess(trans, xaRes1);
			delistSuccess(trans, xaRes2);
			delistSuccess(trans, xaRes3);
			
//			userTransaction.commit();
			userTransaction.rollback();
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			XAUtils.closeConnections(conns);
			XAUtils.closeXAConnections(xaConns);
		}
	}

}
