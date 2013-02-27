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

public class SimultaneousActiveBranchesOfDiffGlobalTransWhichAreMixedTest {

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

			XAConnection xaConn21 = xaDataSources.get(1).getXAConnection();
			XAConnection xaConn31 = xaDataSources.get(2).getXAConnection();
			
			XAResource xaRes1 = xaRess.get(0);
			XAResource xaRes2 = xaRess.get(1);
//			XAResource xaRes21 = xaConn21.getXAResource();
			XAResource xaRes3 = xaRess.get(2);
			XAResource xaRes31 = xaConn31.getXAResource();

			Connection conn1 = conns.get(0);
			Connection conn2 = conns.get(1);
			Connection conn21 = xaConn21.getConnection();
			Connection conn3 = conns.get(2);
			Connection conn31 = xaConn31.getConnection();

			//-------------------------------------------------------------------------------------------------------------------
			// Seems that the second start overrides the first, which in this case is ignored.
			// (Something like this)
//			Xid xid11 = createXid(1, 1);
//			Xid xid21 = createXid(2, 1);
//			
//			start(xid11, xaRes1);
//			start(xid21, xaRes1);
//			
//			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br1, succeded', 11)");
//			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 2, br1, succeded', 21)");
//			
//			end(xid11, xaRes1);
//			end(xid21, xaRes1);
//			
			//-------------------------------------------------------------------------------------------------------------------
			Xid xid11 = XAUtils.createXid(1, 1);
			XAUtils.start(xid11, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br1, succeded', 11)");
			XAUtils.end(xid11, xaRes1);
			
			Xid xid21 = XAUtils.createXid(2, 1);
			XAUtils.start(xid21, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 2, br1, succeded', 21)");
			XAUtils.end(xid21, xaRes1);
			
			Xid xid12 = XAUtils.createXid(1, 2);
			XAUtils.start(xid12, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br2, succeded', 12)");
			XAUtils.end(xid12, xaRes1);

			//-------------------------------------------------------------------------------------------------------------------
			Xid xid13 = XAUtils.createXid(1, 3);
			XAUtils.start(xid13, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br3, suspended', 13)");
			XAUtils.suspend(xid13, xaRes1);
			
			Xid xid22 = XAUtils.createXid(2, 2);
			XAUtils.start(xid22, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 2, br2, succeded', 22)");
			XAUtils.end(xid22, xaRes1);
			
			XAUtils.resume(xid13, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br3, resumed', 13)");
			XAUtils.end(xid13, xaRes1);

			//-------------------------------------------------------------------------------------------------------------------
			Xid xid14 = XAUtils.createXid(1, 4);
			XAUtils.start(xid14, xaRes2);
			conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br4', 'succeded', 14)");
			XAUtils.end(xid14, xaRes2);
			int prepXid14 = XAUtils.prepare(xid14, xaRes2);
			XAUtils.commit(xid14, xaRes2, prepXid14);
			
			// XAER_RMFAIL: The command cannot be executed when global transaction is in the  IDLE state
			// XAER_RMFAIL: The command cannot be executed when global transaction is in the  PREPARED state
			// This is about transaction branches on the same physical connection
			// It does not matter whether the branches belong to the same distributed transaction or to 
			// to different, until the active branch is not in committed or rolled back, 
			// other branch cannot be started.
			// It means that only one open transaction (distributed or local) can be active at a time
//			The next statements demonstrate this
			Xid xid23 = XAUtils.createXid(2, 3);
			XAUtils.start(xid23, xaRes2);
			conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 2, br3', 'succeded', 23)");
			XAUtils.end(xid23, xaRes2);
			
			// These statements below work, because they use different physical connection
//			Xid xid23 = createXid(2, 3);
//			start(xid23, xaRes21);
//			conn21.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 2, br3', 'succeded', 23)");
//			end(xid23, xaRes21);
			
			//-------------------------------------------------------------------------------------------------------------------
			Xid xid15 = XAUtils.createXid(1, 5);
			XAUtils.start(xid15, xaRes3);
			conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 1, br5', 'succeded', 15)");
			XAUtils.end(xid15, xaRes3);
			
			// XAER_RMFAIL: The command cannot be executed when global transaction is in the  IDLE state
			// This is about transaction branches on the same physical connection
			
			Xid xid24 = XAUtils.createXid(2, 4);
			XAUtils.start(xid24, xaRes31);
			conn31.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans 2, br4', 'succeded', 24)");
			XAUtils.end(xid24, xaRes31);
			
			//-------------------------------------------------------------------------------------------------------------------

			int prepXid11 = XAUtils.prepare(xid11, xaRes1);
			int prepXid12 = XAUtils.prepare(xid12, xaRes1);
			int prepXid13 = XAUtils.prepare(xid13, xaRes1);
//			int prepXid14 = prepare(xid14, xaRes2);
			int prepXid15 = XAUtils.prepare(xid15, xaRes3);
			int prepXid21 = XAUtils.prepare(xid21, xaRes1);
			int prepXid22 = XAUtils.prepare(xid22, xaRes1);
//			int prepXid23 = prepare(xid23, xaRes21);
			int prepXid24 = XAUtils.prepare(xid24, xaRes31);
			
			XAUtils.commit(xid11, xaRes1, prepXid11);
			XAUtils.commit(xid12, xaRes1, prepXid12);
			XAUtils.commit(xid13, xaRes1, prepXid13);
			XAUtils.commit(xid15, xaRes3, prepXid15);
//			commit(xid21, xaRes1, prepXid21);
//			commit(xid22, xaRes1, prepXid22);
//			commit(xid23, xaRes21, prepXid23);
//			commit(xid24, xaRes31, prepXid24);
			XAUtils.rollback(xid21, xaRes1, prepXid21); 
			XAUtils.rollback(xid22, xaRes1, prepXid22); 
//			rollback(xid23, xaRes21, prepXid23);
			XAUtils.rollback(xid24, xaRes31, prepXid24);
			
			XAUtils.closeConnections(conns);
			conn21.close();
			conn31.close();
			
			XAUtils.closeXAConnections(xaConns);
			xaConn21.close();
			xaConn31.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (XAException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
