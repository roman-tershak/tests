package jta_tests.test01;

import java.sql.Connection;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import jta_tests.test02.XAUtils;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;
import oracle.jdbc.xa.client.OracleXADataSource;

public class MixOfSuspendedAndResumedBranchesWhichArePartiallyCommittedAndRolledBackTest {

	public static void main(String[] args) {
		try {
			List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests2", "tests"},
					new Object[] {JtdsDataSource.class, "jdbc:jtds:sqlserver://localhost:1433/test", "sa", "sasasa!2"}
			);
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			XAResource xaRes1 = xaRess.get(0);
			XAResource xaRes2 = xaRess.get(1);
			XAResource xaRes3 = xaRess.get(2);

			Connection conn1 = conns.get(0);
			Connection conn2 = conns.get(1);
			Connection conn3 = conns.get(2);

			Xid xid1 = XAUtils.createXid(1, 1);
			Xid xid2 = XAUtils.createXid(1, 2);

			//-------------------------------------------------------------------------------------------------------------------

			XAUtils.start(xid1, xaRes1);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br1, suspended', 1)");
			XAUtils.suspend(xid1, xaRes1);

			Xid xid11 = XAUtils.createXid(1, 4);
			XAUtils.start(xid11, xaRes1);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br11, created for commit', 11)");
			XAUtils.end(xid11, xaRes1);

			XAUtils.resume(xid1, xaRes1);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br1, resumed, ended for rollback', 1)");
			XAUtils.end(xid1, xaRes1);

			Xid xid12 = XAUtils.createXid(1, 5);
			XAUtils.start(xid12, xaRes1);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br12, created, suspended', 12)");
			XAUtils.suspend(xid12, xaRes1);

			XAUtils.resume(xid12, xaRes1);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br12, resumed, ended for commit', 12)");
			XAUtils.end(xid12, xaRes1);
			
			//-------------------------------------------------------------------------------------------------------------------

			XAUtils.start(xid2, xaRes2);
			conn2.createStatement().execute("insert into tests2.table1 (col1, col2) values ('distr trans, br2, ended for commit', 22)");
			XAUtils.end(xid2, xaRes2);

			//-------------------------------------------------------------------------------------------------------------------

			Xid xid3 = XAUtils.createXid(1, 7);
			XAUtils.start(xid3, xaRes3);
//			conn3.createStatement().execute("insert into table1 (col1, col2) values ('distr trans, br7, suspended', 1)");
			conn3.createStatement().execute("delete from table1");
			XAUtils.end(xid3, xaRes3);
			
//			MS SQL 2008 does not seem to support suspending and resuming branches of distributed transactions
//			javax.transaction.xa.XAException: XAER_INVAL: Invalid arguments were given.
//			suspend(xid3, xaRes3);

//			It also seems that MS SQL 2008 does not support multiple branches even though they were
//			ended in a proper way. Something like it was on MySQL 5.x, i.e. only one active branch 
//			at a time on given connection (till the end (commit or rollback) of the branch).
//			javax.transaction.xa.XAException: XAER_PROTO: Function invoked in an improper context.
//			Xid xid31 = createXid(1, 71);
//			start(xid31, xaRes3);
//			conn3.createStatement().execute("insert into table1 (col1, col2) values ('distr trans, br71, created for commit', 11)");
//			end(xid31, xaRes3);

//			Even two branches of different distributed transactions cannot be executed one MS SQL 2008
//			javax.transaction.xa.XAException: XAER_PROTO: Function invoked in an improper context.
//			Xid xid4 = createXid(4, 71);
//			start(xid4, xaRes3);
//			conn3.createStatement().execute("insert into table1 (col1, col2) values ('distr trans, br71, created for commit', 11)");
//			end(xid4, xaRes3);
			
//			Even after prepare only one branch can be held on one connection on MS SQL 2008
//			javax.transaction.xa.XAException: XAER_PROTO: Function invoked in an improper context.
//			Xid xid4 = createXid(4, 71);
//			start(xid4, xaRes3);
//			conn3.createStatement().execute("insert into table1 (col1, col2) values ('distr trans, br71, created for commit', 11)");
//			end(xid4, xaRes3);
			
//			resume(xid3, xaRes3);
//			conn3.createStatement().execute("insert into table1 (col1, col2) values ('distr trans, br7, resumed, ended for rollback', 1)");
//			end(xid3, xaRes3);

			//-------------------------------------------------------------------------------------------------------------------

//			rollback(xid1, xaRes1, 0);
			
			int prepXid1 = XAUtils.prepare(xid1, xaRes1);
			int prepXid2 = XAUtils.prepare(xid2, xaRes2);
			int prepXid11 = XAUtils.prepare(xid11, xaRes1);
			int prepXid12 = XAUtils.prepare(xid12, xaRes1);
			int prepXid3 = XAUtils.prepare(xid3, xaRes3);
//			int prepXid4 = prepare(xid4, xaRes3);

//			Even though two connections with different users were used Oracle considers all branches of one
//			distributed transaction like one logically united unit (one big branch)
//			prepare branch([1], [1]) - 3
//			prepare branch([1], [2]) - 3
//			prepare branch([1], [4]) - 3
//			prepare branch([1], [5]) - 0
			
			XAUtils.commit(xid1, xaRes1, prepXid1);
			XAUtils.commit(xid11, xaRes1, prepXid11);
			XAUtils.commit(xid12, xaRes1, prepXid12);
			XAUtils.commit(xid2, xaRes2, prepXid2);
			XAUtils.commit(xid3, xaRes3, prepXid3);
//			commit(xid4, xaRes3, prepXid4);

			XAUtils.closeConnections(conns);
			XAUtils.closeXAConnections(xaConns);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
