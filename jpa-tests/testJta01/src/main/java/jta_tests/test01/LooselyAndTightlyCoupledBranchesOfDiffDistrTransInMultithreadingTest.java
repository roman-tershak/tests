package jta_tests.test01;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import jta_tests.test02.XAUtils;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;
import oracle.jdbc.xa.client.OracleXADataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class LooselyAndTightlyCoupledBranchesOfDiffDistrTransInMultithreadingTest {

	public static void main(String[] args) {
		try {
			List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3306/test", "root", "12345"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3306/test", "root", "12345"},	// !!! change it back to 3307 port
					new Object[] {JtdsDataSource.class, "jdbc:jtds:sqlserver://localhost:1433/test", "sa", "sasasa!2"}
			);
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			XAConnection xaConn1b = xaDataSources.get(0).getXAConnection();
//			XAConnection xaConn2b = xaDataSources.get(1).getXAConnection();
//			XAConnection xaConn3b = xaDataSources.get(2).getXAConnection();
			XAConnection xaConn4b = xaDataSources.get(3).getXAConnection();
			
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			XAResource xaRes1 = xaRess.get(0);
			final XAResource xaRes1b = xaConn1b.getXAResource();
//			XAResource xaRes2 = xaRess.get(1);
//			final XAResource xaRes2b = xaConn2b.getXAResource();
//			XAResource xaRes3 = xaRess.get(2);
//			final XAResource xaRes3b = xaConn3b.getXAResource();
			XAResource xaRes4 = xaRess.get(3);
			final XAResource xaRes4b = xaConn4b.getXAResource();

			Connection conn1 = conns.get(0);
			conn1.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			final Connection conn1b = xaConn1b.getConnection();
			conn1b.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//			Connection conn2 = conns.get(1);
//			conn2.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
//			final Connection conn2b = xaConn2b.getConnection();
//			conn2b.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
//			Connection conn3 = conns.get(2);
//			conn3.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
//			final Connection conn3b = xaConn3b.getConnection();
//			conn3b.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			Connection conn4 = conns.get(3);
//			conn4.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			final Connection conn4b = xaConn4b.getConnection();
//			conn4b.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			//-------------------------------------------------------------------------------------------------------------------
			Xid xid112 = XAUtils.createXid(13, 116);
			XAUtils.start(xid112, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, succeded', 1)");
			XAUtils.end(xid112, xaRes1);
			
			Xid xid113 = XAUtils.createXid(13, 117);
			XAUtils.start(xid113, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, succeded', 1)");
			XAUtils.end(xid113, xaRes1);
			
			int prepXid112  = XAUtils.prepare(xid112, xaRes1);
			int prepXid113  = XAUtils.prepare(xid113, xaRes1);
//			closeXAConnections(xaConns);
			
//			Its not allowed to start another branch of the distributed transaction when one of its branches
//			has already been prepared
			final Xid xid21 = XAUtils.createXid(2, 121);
			Thread thread1 = new Thread() {
				@Override
				public void run() {
					try {
						XAUtils.start(xid21, xaRes1b);
						conn1b.createStatement().execute("delete from table1");
						XAUtils.end(xid21, xaRes1b);
						XAUtils.prepare(xid21, xaRes1b);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread1.start();
			thread1.join();
			
			//-------------------------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------------------------
//			Xid xid11a = createXid(1, 11);
//			final Xid xid11b = createXid(1, 12);
			
//			start(xid11a, xaRes1);
//			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, succeded', 1)");
//			System.out.println("insert into table1 (col1, col2) values ('distr trans 1, br11, succeded', 1)");
			
//			Thread thread1 = new Thread() {
//				@Override
//				public void run() {
//					try {
//						Main.start(xid11b, xaRes1b);
//						for (int i = 0; i < 8; i++) {
//							// Oracle seems to isolate branches of different distributed transactions
//							// Delete has not removed any rows
//							// As for the branches of the same global transaction, they are visible to each other
//							
//							// After prepare 'prepared' data must survive crash or reboot
//							// I got this ORA-01591: lock held by in-doubt distributed transaction 14.19.340
//							// , when the previous distributed transaction was not completed (neither committed nor rolled back), 
//							// but had been already prepared, then after its branch was left in an uncompleted state it continued to block
//							// this branch (this delete statement) in the next runs
//							// After reboot lock was preserved, and after termination of Oracle process it also
//							// was preserved!!
//							
//							// After 3 days of waiting:
////							Error starting at line 1 in command:
////								delete from table1
////								Error report:
////								SQL Error: ORA-01591: lock held by in-doubt distributed transaction 14.19.340
////								01591. 00000 -  "lock held by in-doubt distributed transaction %s"
////								*Cause:    Trying to access resource that is locked by a dead two-phase commit
////								           transaction that is in prepared state.
////								*Action:   DBA should query the pending_trans$ and related tables, and attempt
////								           to repair network connection(s) to coordinator and commit point.
////								           If timely repair is not possible, DBA should contact DBA at commit
////								           point if known or end user for correct outcome, or use heuristic
////								           default if given to issue a heuristic commit or abort command to
////								           finalize the local portion of the distributed transaction.
//							
//							conn1b.createStatement().execute("delete from table1");
//							System.out.println("delete from table1");
//							Thread.sleep(800);
//						}
//						Main.end(xid11b, xaRes1b);
//						System.out.println("Ended xid11b branch");
//						
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			};
//			thread1.start();
			
//			for (int i = 0; i < 2; i++) {
//				conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, origin', " + (i + 1) + ")");
//				System.out.println("insert into table1 (col1, col2) values ('distr trans 1, br11, origin', " + (i + 1) + ")");
//				Thread.sleep(800);
//			}
			// It does not matter whether a branch was ended or not, until then it has not been committed 
			// its changes are not visible to other global transactions.
			// But, again, branches of the same global transaction are visible to each other.
//			end(xid11a, xaRes1);
//			System.out.println("Ended xid11a branch");
			
//			thread1.join();
			
			//-------------------------------------------------------------------------------------------------------------------
//			Xid xid21a = createXid(1, 511);
//			final Xid xid21b = createXid(1, 512);
//			
//			start(xid21a, xaRes2);
//			conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br511', 'succeded', 1)");
//			
//			Thread thread2 = new Thread() {
//				@Override
//				public void run() {
//					try {
//						Main.start(xid21b, xaRes2b);
//						for (int i = 0; i < 8; i++) {
//							// java.sql.SQLException: Lock wait timeout exceeded; try restarting transaction
//							// connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); does not help either
//							// Also it does not matter whether branches belong to the same global transaction
//							// or to different ones, the exception is still thrown
//							conn2b.createStatement().execute("delete from table1 where col3 = " + i);
//							Thread.sleep(800);
//						}
//						Main.end(xid21b, xaRes2b);
//						
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			};
//			thread2.start();
//			
//			for (int i = 0; i < 8; i++) {
//				conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br511', 'origin', " + (i + 1) + ")");
//				Thread.sleep(800);
//			}
//			thread2.join();
//			
//			end(xid21a, xaRes2);
			
			//-------------------------------------------------------------------------------------------------------------------
//			Xid xid31a = createXid(1, 551);
//			final Xid xid31b = createXid(1, 552);
//			
//			start(xid31a, xaRes3);
//			conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br551', 'succeded', 1)");
//			
//			Thread thread3 = new Thread() {
//				@Override
//				public void run() {
//					try {
//						Main.start(xid31b, xaRes3b);
//						for (int i = 0; i < 8; i++) {
//							// java.sql.SQLException: Lock wait timeout exceeded; try restarting transaction
//							// connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); does not help either
//							// Also it does not matter whether branches belong to the same global transaction
//			                // or to different ones, the exception is still thrown
//							conn3b.createStatement().execute("delete from table1 where col3 = " + i);
//							Thread.sleep(800);
//						}
//						Main.end(xid31b, xaRes3b);
//						
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			};
//			thread3.start();
//			
//			for (int i = 0; i < 8; i++) {
//				conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br551', 'origin', " + (i + 1) + ")");
//				Thread.sleep(800);
//			}
//			thread3.join();
//			
//			end(xid31a, xaRes3);
			
			//-------------------------------------------------------------------------------------------------------------------
//			Xid xid41 = createXid(1, 41);
//			final Xid xid42 = createXid(1, 42);
//			
//			start(xid41, xaRes4);
//			conn4.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, succeded', 1)");
//			
//			Thread thread4 = new Thread() {
//				@Override
//				public void run() {
//					try {
//						Main.start(xid42, xaRes4b);
//						for (int i = 0; i < 8; i++) {
//							// MS SQL Server seems to be locking branches of the same distributed transaction
//							// The statement below was locked by the previous insert on conn4
//							// TRANSACTION_SERIALIZABLE isolation level didn't help
////							conn4b.createStatement().execute("delete from table1");
//							conn4b.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br42, succeded', 1)");
//							Thread.sleep(800);
//						}
//						Main.end(xid42, xaRes4b);
//						
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			};
//			thread4.start();
//			
//			for (int i = 0; i < 2; i++) {
//				conn4.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, origin', " + (i + 1) + ")");
//				Thread.sleep(800);
//			}
//			end(xid41, xaRes4);
//			
//			thread4.join();
//			
			//-------------------------------------------------------------------------------------------------------------------
			Xid xid41 = XAUtils.createXid(1, 41);
			
			XAUtils.start(xid41, xaRes4);
			conn4.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, succeded', 1)");
			XAUtils.end(xid41, xaRes4);
			int prepXid41  = XAUtils.prepare(xid41, xaRes4);
			XAUtils.closeXAConnections(xaConns);
			
			
			final Xid xid52 = XAUtils.createXid(2, 42);
			Thread thread4 = new Thread() {
				@Override
				public void run() {
					try {
						XAUtils.start(xid52, xaRes4b);
						conn4b.createStatement().execute("delete from table1");
						XAUtils.end(xid52, xaRes4b);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread4.start();
			thread4.join();
			
			//-------------------------------------------------------------------------------------------------------------------
			
//			int prepXid11a = prepare(xid11a, xaRes1);
//			int prepXid11b = prepare(xid11b, xaRes1b);
//			int prepXid21a = prepare(xid21a, xaRes2);
//			int prepXid21b = prepare(xid21b, xaRes2b);
//			int prepXid31a = prepare(xid31a, xaRes3);
//			int prepXid31b = prepare(xid31b, xaRes3b);
//			int prepXid41  = prepare(xid41, xaRes4);
			int prepXid42  = XAUtils.prepare(xid52, xaRes4b);
			xaConn4b.close();
			
//			commit(xid11a, xaRes1, prepXid11a);
//			commit(xid11b, xaRes1b, prepXid11b);
//			commit(xid21a, xaRes2, prepXid21a);
//			commit(xid21b, xaRes2b, prepXid21b);
//			commit(xid31a, xaRes3, prepXid31a);
//			commit(xid31b, xaRes3b, prepXid31b);
//			commit(xid41, xaRes4, prepXid41);
//			commit(xid42, xaRes4b, prepXid42);
			
//			closeConnections(conns);
//			conn1b.close();
//			conn2b.close();
//			conn3b.close();
//			conn4b.close();
			
//			closeXAConnections(xaConns);
//			xaConn1b.close();
//			xaConn2b.close();
//			xaConn3b.close();
//			xaConn4b.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (XAException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
