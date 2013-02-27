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

public class SimultaneousBranchesWhichAreMixedTest {

	public static void main(String[] args) {
		try {
			List<XADataSource> xaDataSources = XAUtils.createXADataSources(
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests", "tests"},
					new Object[] {OracleXADataSource.class, "jdbc:oracle:thin:@localhost:1521:xe", "tests2", "tests"}
			);
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			XAResource xaRes1 = xaRess.get(0);
			XAResource xaRes2 = xaRess.get(1);

			Connection conn1 = conns.get(0);
			Connection conn2 = conns.get(1);

			//-------------------------------------------------------------------------------------------------------------------
			// Oracle does allow several transaction branches executed simultaneously
			Xid xid1 = XAUtils.createXid(1, 1);
			XAUtils.start(xid1, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br1, succeded', 1)");
			XAUtils.end(xid1, xaRes1);
			
			Xid xid2 = XAUtils.createXid(1, 2);
			XAUtils.start(xid2, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br2, succeded', 2)");
			XAUtils.end(xid2, xaRes1);

			//-------------------------------------------------------------------------------------------------------------------
			// It's still allowed to have more than one active branch of a certain global transaction
			// on the same source
			Xid xid3 = XAUtils.createXid(1, 3);
			Xid xid4 = XAUtils.createXid(1, 4);
			XAUtils.start(xid3, xaRes1);
			XAUtils.start(xid4, xaRes1);
			
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br3, succeded', 3)");
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br4, succeded', 4)");
			
			XAUtils.end(xid3, xaRes1);
			XAUtils.end(xid4, xaRes1);

			//-------------------------------------------------------------------------------------------------------------------
			// It is allowed to have one active branch of a certain global transaction with 
			// the rest branches suspended or completed (according to spec)
			Xid xid5 = XAUtils.createXid(1, 5);
			Xid xid6 = XAUtils.createXid(1, 6);
			
			XAUtils.start(xid5, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br5, suspended', 5)");
			XAUtils.suspend(xid5, xaRes1);
			
			XAUtils.start(xid6, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br6, succeded', 6)");
			XAUtils.end(xid6, xaRes1);

			XAUtils.resume(xid5, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br5, resumed', 5)");
			XAUtils.end(xid5, xaRes1);
			
			//-------------------------------------------------------------------------------------------------------------------
			// More than one active branch of a certain global transaction on different sources
			Xid xid13 = XAUtils.createXid(1, 13);
			Xid xid24 = XAUtils.createXid(1, 24);
			XAUtils.start(xid13, xaRes1);
			XAUtils.start(xid24, xaRes2);
			
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br13, succeded', 13)");
			conn2.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br24, succeded', 24)");
			
			XAUtils.end(xid13, xaRes1);
			XAUtils.end(xid24, xaRes2);

			//-------------------------------------------------------------------------------------------------------------------
			// It is allowed to have more than one active branch of a certain global transaction 
			// on different sources however
			Xid xid15 = XAUtils.createXid(1, 15);
			Xid xid26 = XAUtils.createXid(1, 26);
			
			XAUtils.start(xid15, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br15, suspended', 15)");
			XAUtils.suspend(xid15, xaRes1);
			
			XAUtils.start(xid26, xaRes2);
			conn2.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br6, succeded', 6)");
			XAUtils.end(xid26, xaRes2);

			XAUtils.resume(xid15, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2) values ('distr trans 1, br15, resumed', 15)");
			XAUtils.end(xid15, xaRes1);
			
			//-------------------------------------------------------------------------------------------------------------------
			

			int prepXid1 = XAUtils.prepare(xid1, xaRes1);
			int prepXid2 = XAUtils.prepare(xid2, xaRes1);
			int prepXid3 = XAUtils.prepare(xid3, xaRes1);
			int prepXid4 = XAUtils.prepare(xid4, xaRes1);
			int prepXid5 = XAUtils.prepare(xid5, xaRes1);
			int prepXid6 = XAUtils.prepare(xid6, xaRes1);
			int prepXid13 = XAUtils.prepare(xid13, xaRes1);
			int prepXid24 = XAUtils.prepare(xid24, xaRes2);
			int prepXid15 = XAUtils.prepare(xid15, xaRes1);
			int prepXid26 = XAUtils.prepare(xid26, xaRes2);

			XAUtils.commit(xid1, xaRes1, prepXid1);
			XAUtils.commit(xid2, xaRes1, prepXid2);
			XAUtils.commit(xid3, xaRes1, prepXid3);
			XAUtils.commit(xid4, xaRes1, prepXid4);
			XAUtils.commit(xid5, xaRes1, prepXid5);
			XAUtils.commit(xid6, xaRes1, prepXid6);
			XAUtils.commit(xid13, xaRes1, prepXid13);
			XAUtils.commit(xid24, xaRes2, prepXid24);
			XAUtils.commit(xid15, xaRes1, prepXid15);
			XAUtils.commit(xid26, xaRes2, prepXid26);
			
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
