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
import oracle.jdbc.xa.client.OracleXADataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class LooselyAndTightlyCoupledBranchesInMultithreadingTest {

	public static void main(String[] args) {
		try {
			List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
//					new Object[] {OracleXADataSource.class, "jdbc:oracle:oci@xe", "tests", "tests"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3306/test", "root", "12345"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3307/test", "root", "12345"}
			);
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			XAConnection xaConn1b = xaDataSources.get(0).getXAConnection();
			XAConnection xaConn2b = xaDataSources.get(1).getXAConnection();
			XAConnection xaConn3b = xaDataSources.get(2).getXAConnection();
			
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			XAResource xaRes1 = xaRess.get(0);
			final XAResource xaRes1b = xaConn1b.getXAResource();
			XAResource xaRes2 = xaRess.get(1);
			final XAResource xaRes2b = xaConn2b.getXAResource();
			XAResource xaRes3 = xaRess.get(2);
			final XAResource xaRes3b = xaConn3b.getXAResource();

			Connection conn1 = conns.get(0);
			final Connection conn1b = xaConn1b.getConnection();
			Connection conn2 = conns.get(1);
			final Connection conn2b = xaConn2b.getConnection();
			Connection conn3 = conns.get(2);
			final Connection conn3b = xaConn3b.getConnection();

			//-------------------------------------------------------------------------------------------------------------------
			Xid xid11a = XAUtils.createXid(1, 11);
			final Xid xid11b = XAUtils.createXid(1, 12);
			
			XAUtils.start(xid11a, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, succeded', 1)");
			
			Thread thread1 = new Thread() {
				@Override
				public void run() {
					try {
						XAUtils.start(xid11b, xaRes1b);
						for (int i = 0; i < 8; i++) {
							// Seems that Oracle does support tightly coupled transaction branches
							conn1b.createStatement().execute("delete from table1 where col2 = " + i);
							Thread.sleep(400);
						}
						XAUtils.end(xid11b, xaRes1b);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread1.start();
			
			for (int i = 0; i < 8; i++) {
				conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br11, origin', " + (i + 1) + ")");
				Thread.sleep(400);
			}
			thread1.join();
			
			XAUtils.end(xid11a, xaRes1);
			
			//-------------------------------------------------------------------------------------------------------------------
//			Xid xid21a = createXid(1, 511);
			final Xid xid21b = XAUtils.createXid(1, 512);
			
//			start(xid21a, xaRes2);
//			conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br511', 'succeded', 1)");
			
			Thread thread2 = new Thread() {
				@Override
				public void run() {
					try {
						XAUtils.start(xid21b, xaRes2b);
						for (int i = 0; i < 8; i++) {
							// java.sql.SQLException: Lock wait timeout exceeded; try restarting transaction
							conn2b.createStatement().execute("delete from table1 where col3 = " + i);
							Thread.sleep(400);
						}
						XAUtils.end(xid21b, xaRes2b);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
//			thread2.start();
			
//			for (int i = 0; i < 8; i++) {
//				conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br511', 'origin', " + (i + 1) + ")");
//				Thread.sleep(400);
//			}
//			thread2.join();
			
//			end(xid21a, xaRes2);
			
			//-------------------------------------------------------------------------------------------------------------------
//			Xid xid31a = createXid(1, 551);
			final Xid xid31b = XAUtils.createXid(1, 552);
			
//			start(xid31a, xaRes3);
//			conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br551', 'succeded', 1)");
			
			Thread thread3 = new Thread() {
				@Override
				public void run() {
					try {
						for (int i = 0; i < 8; i++) {
							XAUtils.start(xid31b, xaRes3b);
							// java.sql.SQLException: Lock wait timeout exceeded; try restarting transaction
							conn3b.createStatement().execute("delete from table1 where col3 = " + i);
							XAUtils.end(xid31b, xaRes3b);
							Thread.sleep(400);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
//			thread3.start();
			
//			for (int i = 0; i < 8; i++) {
//				conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br551', 'origin', " + (i + 1) + ")");
//				Thread.sleep(400);
//			}
//			thread3.join();
			
//			end(xid31a, xaRes3);
			
			//-------------------------------------------------------------------------------------------------------------------

			int prepXid11a = XAUtils.prepare(xid11a, xaRes1);
			int prepXid11b = XAUtils.prepare(xid11b, xaRes1b);
//			int prepXid21a = prepare(xid21a, xaRes2);
			int prepXid21b = XAUtils.prepare(xid21b, xaRes2b);
//			int prepXid31a = prepare(xid31a, xaRes3);
			int prepXid31b = XAUtils.prepare(xid31b, xaRes3b);
			
			XAUtils.commit(xid11a, xaRes1, prepXid11a);
			XAUtils.commit(xid11b, xaRes1b, prepXid11b);
//			commit(xid21a, xaRes2, prepXid21a);
			XAUtils.commit(xid21b, xaRes2b, prepXid21b);
//			commit(xid31a, xaRes3, prepXid31a);
			XAUtils.commit(xid31b, xaRes3b, prepXid31b);
			
			XAUtils.closeConnections(conns);
			conn1b.close();
			conn2b.close();
			conn3b.close();
			
			XAUtils.closeXAConnections(xaConns);
			xaConn1b.close();
			xaConn2b.close();
			xaConn3b.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (XAException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
