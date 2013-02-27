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

public class MultithreadingMixOfBranchesOfDiffGlobalTransTest {

	public static void main(String[] args) {
		try {
			List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3306/test", "root", "12345"},
					new Object[] {MysqlXADataSource.class, "jdbc:mysql://localhost:3307/test", "root", "12345"}
			);
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			final XAResource xaRes1 = xaRess.get(0);
			final XAResource xaRes2 = xaRess.get(1);
			final XAResource xaRes3 = xaRess.get(2);

			final Connection conn1 = conns.get(0);
			final Connection conn2 = conns.get(1);
			final Connection conn3 = conns.get(2);

			//-------------------------------------------------------------------------------------------------------------------
			// Joining is allowed with or even without TMJOIN
			final Xid xid11 = XAUtils.createXid(1, 1);
			XAUtils.start(xid11, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br1, succeded', 11)");
			
			Thread thread1 = new Thread() {
				@Override
				public void run() {
					try {
						XAUtils.join(xid11, xaRes1);	// seems it is optional on Oracle
						for (int i = 0; i < 8; i++) {
							conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br1, joint', " +
									i + ")");
							Thread.sleep(400);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread1.start();
			
			for (int i = 0; i < 8; i++) {
				conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br1, origin', " + i + ")");
				Thread.sleep(400);
			}
			thread1.join();
			
			XAUtils.end(xid11, xaRes1);
			
			//-------------------------------------------------------------------------------------------------------------------
			final Xid xid13 = XAUtils.createXid(1, 3);
			XAUtils.start(xid13, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br3, suspended', 13)");
			XAUtils.suspend(xid13, xaRes1);
			
			// Resuming by another thread
			Thread thread2 = new Thread() {
				@Override
				public void run() {
					try {
						XAUtils.resume(xid13, xaRes1);
						conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br3, resumed by thread', 13)");
						XAUtils.end(xid13, xaRes1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread2.start();
			thread2.join();

			//-------------------------------------------------------------------------------------------------------------------
			// Joining is allowed with or even without TMJOIN
			final Xid xid21 = XAUtils.createXid(1, 51);
			XAUtils.start(xid21, xaRes2);
			conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br51', 'succeded', 21)");
			
			Thread thread3 = new Thread() {
				@Override
				public void run() {
					try {
//						Main.join(xid21, xaRes2);	// seems it is not supported on MySQL 5.1 (XAER_INVAL: Invalid arguments (or unsupported command))
						// But the 'joining' itself is supported.
						for (int i = 0; i < 8; i++) {
							conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br51', 'joint', " +
									i + ")");
							Thread.sleep(400);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread3.start();
			
			for (int i = 0; i < 8; i++) {
				conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br51', 'origin', " + i + ")");
				Thread.sleep(400);
			}
			thread3.join();
			
			XAUtils.end(xid21, xaRes2);
			
			//-------------------------------------------------------------------------------------------------------------------
			// Joining is allowed with or even without TMJOIN
			final Xid xid31 = XAUtils.createXid(1, 55);
			XAUtils.start(xid31, xaRes3);
			conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br55', 'succeded', 31)");
			
			Thread thread4 = new Thread() {
				@Override
				public void run() {
					try {
//						Main.join(xid31, xaRes3);	// seems it is not supported on MySQL 5.5 (XAER_INVAL: Invalid arguments (or unsupported command))
						// But the 'joining' itself is supported.
						for (int i = 0; i < 8; i++) {
							conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br55', 'joint', " +
									i + ")");
							Thread.sleep(400);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread4.start();
			
			for (int i = 0; i < 8; i++) {
				conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br55', 'origin', " + i + ")");
				Thread.sleep(400);
			}
			thread4.join();
			
			XAUtils.end(xid31, xaRes3);
			
			//-------------------------------------------------------------------------------------------------------------------

			int prepXid11 = XAUtils.prepare(xid11, xaRes1);
			int prepXid13 = XAUtils.prepare(xid13, xaRes1);
			int prepXid21 = XAUtils.prepare(xid21, xaRes2);
			int prepXid31 = XAUtils.prepare(xid31, xaRes3);
			
			XAUtils.commit(xid11, xaRes1, prepXid11);
			XAUtils.commit(xid13, xaRes1, prepXid13);
			XAUtils.commit(xid21, xaRes2, prepXid21);
			XAUtils.commit(xid31, xaRes3, prepXid31);
//			rollback(xid21, xaRes2, prepXid21);
//			rollback(xid31, xaRes3, prepXid31);
			
			XAUtils.closeConnections(conns);
			
			XAUtils.closeXAConnections(xaConns);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (XAException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
