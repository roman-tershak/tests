package jta_tests.test01;

import static jta_tests.test01.Main.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import jta_tests.test02.XAUtils;

public class MixOfSuspendedAndResumedBranchesTest {

	public static void main(String[] args) {
		try {
			List<XADataSource> xaDataSources = XAUtils.getXADataSources();
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			XAResource xaRes1 = xaRess.get(0);
			XAResource xaRes2 = xaRess.get(1);

			Connection conn1 = conns.get(0);
			Connection conn2 = conns.get(1);

			Xid xid1 = XAUtils.createXid(1, 1);
			Xid xid2 = XAUtils.createXid(1, 2);

			//-------------------------------------------------------------------------------------------------------------------

			xaRes1.start(xid1, XAResource.TMNOFLAGS);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br1, suspended', 1)");
			xaRes1.end(xid1, XAResource.TMSUSPEND);

			Xid xid11 = XAUtils.createXid(1, 4);
			xaRes1.start(xid11, XAResource.TMNOFLAGS);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br11, created', 11)");
			xaRes1.end(xid11, XAResource.TMSUCCESS);

			xaRes1.start(xid1, XAResource.TMRESUME);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br1, resumed, ended for commit', 1)");
			xaRes1.end(xid1, XAResource.TMSUCCESS);

			Xid xid12 = XAUtils.createXid(1, 5);
			xaRes1.start(xid12, XAResource.TMNOFLAGS);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br12, created, suspended', 12)");
			xaRes1.end(xid12, XAResource.TMSUSPEND);

			xaRes1.start(xid12, XAResource.TMRESUME);
			conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br12, resumed, ended', 12)");
			xaRes1.end(xid12, XAResource.TMSUCCESS);
			
			//-------------------------------------------------------------------------------------------------------------------

			xaRes2.start(xid2, XAResource.TMNOFLAGS);
			conn2.createStatement().execute("insert into tests2.table1 (col1, col2) values ('distr trans, br2, ended for commit', 22)");
			xaRes2.end(xid2, XAResource.TMSUCCESS);

			//-------------------------------------------------------------------------------------------------------------------

			int prepXid1 = xaRes1.prepare(xid1);
			System.out.println("xaRes1.prepare(xid1) - " + prepXid1);
			int prepXid2 = xaRes2.prepare(xid2);
			System.out.println("xaRes2.prepare(xid2) - " + prepXid2);
			int prepXid11 = xaRes1.prepare(xid11);
			System.out.println("xaRes1.prepare(xid11) - " + prepXid11);
			int prepXid12 = xaRes1.prepare(xid12);
			System.out.println("xaRes1.prepare(xid12) - " + prepXid12);

			if (prepXid1 == XAResource.XA_OK) {
				xaRes1.commit(xid1, false);
			}
			if (prepXid11 == XAResource.XA_OK) {
				xaRes1.commit(xid11, false);
			}
			if (prepXid2 == XAResource.XA_OK) {
				xaRes2.commit(xid2, false);
			}
			if (prepXid12 == XAResource.XA_OK) {
				xaRes1.commit(xid12, false);
			}

			XAUtils.closeConnections(conns);
			XAUtils.closeXAConnections(xaConns);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (XAException e) {
			e.printStackTrace();
		}
	}

}
