package jta_tests.test01;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import jta_tests.test02.XAUtils;

public class MixOfBranchesOfDiffGlobalTransMiscellaneousTest {

	public static void main(String[] args) {
		try {
			List<XADataSource> xaDataSources = XAUtils.getXADataSources(false, false, false, false, true, true);
			List<XAConnection> xaConns = XAUtils.getXAConnections(xaDataSources);
			List<XAResource> xaRess = XAUtils.getXaResources(xaConns);
			List<Connection> conns = XAUtils.getConnections(xaConns);

			XAResource xaRes1 = xaRess.get(0);
			XAResource xaRes2 = xaRess.get(1);
			XAResource xaRes2a = xaDataSources.get(1).getXAConnection().getXAResource();

			Connection conn1 = conns.get(0);
			Connection conn2 = conns.get(1);
			Connection conn2a = xaDataSources.get(1).getXAConnection().getConnection();

			int distrTranNum = 22;
			String distrTran = "distr trans " + distrTranNum;
			
			//-------------------------------------------------------------------------------------------------------------------
			Xid xid11 = XAUtils.createXid(distrTranNum, 1);
			XAUtils.start(xid11, xaRes1);
			conn1.createStatement().execute("insert into table1 (col1, col2, col3) values ('" + distrTran + ", br1', 'succeded', 11)");
			XAUtils.end(xid11, xaRes1);
			
			// SQL Server supports loosely-coupled branches
//			Xid xid11a =  createXid(distrTranNum, 11);
//			start(xid11a, xaRes1);
//			ResultSet rs = conn1.createStatement().executeQuery("select * from table1");
//			while (rs.next()) {
//				System.out.println(rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getInt(3));
//			}
////			conn1.createStatement().execute("insert into table1 (col1, col2, col3) values ('" + distrTran + ", br1', 'succeded', 11)");
//			end(xid11a, xaRes1);
			
//			resume(xid11, xaRes1);
//			conn1.createStatement().execute("insert into table1 (col1, col2, col3) values ('" + distrTran + ", br1a', 'resumed', 11)");
//			end(xid11, xaRes1);
			//-------------------------------------------------------------------------------------------------------------------
			Xid xid12 = XAUtils.createXid(distrTranNum, 2);
			Xid xid12a = XAUtils.createXid(distrTranNum, 21);
			XAUtils.start(xid12, xaRes2);
			conn2.createStatement().execute("insert into table1 (col1, col2, col3) values ('" + distrTran + ", br2', 'test02', 1)");
			// org.postgresql.xa.PGXAException: suspend/resume not implemented
//			suspend(xid12, xaRes2);
			XAUtils.end(xid12, xaRes2);
			
			// org.postgresql.xa.PGXAException: Transaction interleaving not implemented
			// PostgreSQL supports loosely-coupled branches
			XAUtils.start(xid12a, xaRes2a);
			ResultSet rs2 = conn2a.createStatement().executeQuery("select * from table1");
			while (rs2.next()) {
				System.out.println(rs2.getString(1) + ", " + rs2.getString(2) + ", " + rs2.getInt(3));
			}
//			conn2a.createStatement().execute("insert into table1 (col1, col2, col3) values ('" + distrTran + ", br2', 'test02', 1)");
			XAUtils.end(xid12a, xaRes2a);
			
			// Start again is NOT allowed
//			start(xid11, xaRes1);
//			conn1.createStatement().execute("insert into table1 (col1, col2) values ('" + distrTran + ", br1, start again', 12)");
//			end(xid11, xaRes1);

			//-------------------------------------------------------------------------------------------------------------------
//			Xid xid13 = createXid(1, 3);
//			start(xid13, xaRes1);
//			conn1.createStatement().execute("insert into table1 (col1, col2) values ('" + distrTran + ", br3, suspended', 13)");
//			suspend(xid13, xaRes1);
			
			// Resuming by start is not allowed
//			start(xid13, xaRes1);
//			conn1.createStatement().execute("insert into table1 (col1, col2) values ('" + distrTran + ", br3, resumed by start', 13)");
//			end(xid13, xaRes1);

			//-------------------------------------------------------------------------------------------------------------------

			int prepXid11 = XAUtils.prepare(xid11, xaRes1);
//			int prepXid11a = prepare(xid11a, xaRes1);
			int prepXid12 = XAUtils.prepare(xid12, xaRes2);
			int prepXid12a = XAUtils.prepare(xid12a, xaRes2a);
			XAUtils.commit(xid11, xaRes1, prepXid11);
//			commit(xid11a, xaRes1, prepXid11a);
			XAUtils.commit(xid12, xaRes2, prepXid12);
			XAUtils.commit(xid12a, xaRes2a, prepXid12a);
			
//			rollback(xid11, xaRes1);
//			rollback(xid12, xaRes2);
//			rollback(xid13, xaRes1);
			
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
