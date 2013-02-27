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

public class MixOfBranchesAndLocalTransTest {

	public static void main(String[] args) throws SQLException, XAException {
		List<XADataSource> xaDataSources = XAUtils.getXADataSources();
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
		Xid xid3 = XAUtils.createXid(1, 3);
		
		//-------------------------------------------------------------------------------------------------------------------
		
		conn1.setAutoCommit(false);
		conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('local trans 11, committed', 11)");
		conn1.commit();
		
		xaRes1.start(xid1, XAResource.TMNOFLAGS);
		conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br1, suspended', 21)");
		xaRes1.end(xid1, XAResource.TMSUSPEND);
		
//		Execution of local transactions in a connection associated with a suspended branch is not supported.
//		conn1.close();
//		conns.set(0, xaConns.get(0).getConnection());
//		conn1 = conns.get(0);
//		System.out.println("conn1.getAutoCommit() - " + conn1.getAutoCommit());
//		conn1.setAutoCommit(false);
//		conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('local trans 12, committed', 12)");
//		Not closed local transactions prevent starting or resuming global transaction branches
		
		xaRes1.start(xid1, XAResource.TMRESUME);
		conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('distr trans, br1, resumed, ended for commit', 21)");
		xaRes1.end(xid1, XAResource.TMSUCCESS);
		
//		conn1.commit();
		System.out.println("conn1.getAutoCommit() - " + conn1.getAutoCommit());
		conn1.setAutoCommit(false);
		conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('local trans 13, rolled back', 13)");
		conn1.rollback();
		
		conn1.setAutoCommit(false);
		conn1.createStatement().execute("insert into tests.table1 (col1, col2) values ('local trans 11a, committed', 11)");
		conn1.commit();
		
		//-------------------------------------------------------------------------------------------------------------------
		
		xaRes2.start(xid2, XAResource.TMNOFLAGS);
		conn2.createStatement().execute("insert into tests2.table1 (col1, col2) values ('distr trans, br2, ended for rollback', 22)");
		xaRes2.end(xid2, XAResource.TMSUCCESS);
		
		//-------------------------------------------------------------------------------------------------------------------
		
//		Suspend not supported in MySql (in 5.5 also)
//		xaRes3.start(xid3, XAResource.TMNOFLAGS);
//		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans, br3', 'suspended', 23)");
//		xaRes3.end(xid3, XAResource.TMSUSPEND);
		
		System.out.println("conn3.getAutoCommit() - " + conn3.getAutoCommit());
		conn3.setAutoCommit(false);
		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('local trans 31', 'committed', 14)");
		conn3.commit();
		System.out.println("conn3.getAutoCommit() - " + conn3.getAutoCommit());
		conn3.setAutoCommit(false);
		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('local trans 32', 'rolled back', 15)");
		conn3.rollback();
		
//		Not supported in 5.1 ... 5.5
//		xaRes3.start(xid3, XAResource.TMRESUME);
		xaRes3.start(xid3, XAResource.TMNOFLAGS);
		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans, br3', 'resumed, ended for commit', 24)");
		xaRes3.end(xid3, XAResource.TMSUCCESS);
		
		//-------------------------------------------------------------------------------------------------------------------
		
		Xid xid31 = XAUtils.createXid(1, 4);
		
		xaRes3.start(xid31, XAResource.TMNOFLAGS);
		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans, br4', 'ended for rollback', 25)");
		xaRes3.end(xid31, XAResource.TMSUCCESS);
		
//		"Can't call commit() on an XAConnection associated with a global transaction"
//		System.out.println("conn3.getAutoCommit() - " + conn3.getAutoCommit());
//		conn3.setAutoCommit(false);
//		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('local trans 33', 'committed', 16)");
//		conn3.commit();
//		System.out.println("conn3.getAutoCommit() - " + conn3.getAutoCommit());
//		conn3.setAutoCommit(false);
//		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('local trans 34', 'rolled back', 17)");
//		conn3.rollback();
		
//		xaRes3.start(xid31, XAResource.TMNOFLAGS);
//		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans, br4', 'resumed, ended for rollback', 26)");
//		xaRes3.end(xid31, XAResource.TMSUCCESS);
		
		//-------------------------------------------------------------------------------------------------------------------
		
//		Xid xid32 = createXid(1, 5);
		
//		xaRes3.start(xid32, XAResource.TMNOFLAGS);
//		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans, br5', 'suspended', 27)");
//		xaRes3.end(xid32, XAResource.TMSUSPEND);
		
//		xaRes3.start(xid32, XAResource.TMNOFLAGS);
//		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('distr trans, br5', 'resumed, ended with fail', 28)");
//		xaRes3.end(xid32, XAResource.TMFAIL);
		
		//-------------------------------------------------------------------------------------------------------------------
		
		int prepXid1 = xaRes1.prepare(xid1);
		System.out.println("xaRes1.prepare(xid1) - " + prepXid1);
		int prepXid2 = xaRes2.prepare(xid2);
		System.out.println("xaRes2.prepare(xid2) - " + prepXid2);
		int prepXid3 = xaRes3.prepare(xid3);
		System.out.println("xaRes3.prepare(xid3) - " + prepXid3);
		int prepXid31 = xaRes3.prepare(xid31);
		System.out.println("xaRes3.prepare(xid31) - " + prepXid31);
//		int prepXid32 = xaRes3.prepare(xid32);
//		System.out.println("xaRes3.prepare(xid32) - " + prepXid32);
		
		if (prepXid1 == XAResource.XA_OK) {
			xaRes1.commit(xid1, false);
		}
		if (prepXid2 == XAResource.XA_OK) {
			xaRes2.commit(xid2, false);
		}
		xaRes3.commit(xid3, false);
		xaRes3.rollback(xid31);
//		xaRes3.rollback(xid32);
		
		System.out.println("conn3.getAutoCommit() - " + conn3.getAutoCommit());
		conn3.setAutoCommit(false);
		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('local trans 33', 'committed', 16)");
		conn3.commit();
		System.out.println("conn3.getAutoCommit() - " + conn3.getAutoCommit());
		conn3.setAutoCommit(false);
		conn3.createStatement().execute("insert into table1 (col1, col2, col3) values ('local trans 34', 'rolled back', 17)");
		conn3.rollback();
		
		XAUtils.closeConnections(conns);
		XAUtils.closeXAConnections(xaConns);
	}

}
