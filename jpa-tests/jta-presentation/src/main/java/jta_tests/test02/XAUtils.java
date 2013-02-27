package jta_tests.test02;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import oracle.jdbc.xa.client.OracleXADataSource;

import org.postgresql.xa.PGXADataSource;

import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlXid;

public class XAUtils {

	public static List<XADataSource> getXADataSources(
			boolean oracle1, boolean oracle2, boolean mySql50, boolean mySql55, boolean sqlServer, boolean postgres) 
			throws SQLException {
		List<XADataSource> dataSources = new LinkedList<XADataSource>();
		
		if (oracle1) {
			OracleXADataSource oxds1 = new OracleXADataSource();
			oxds1.setURL("jdbc:oracle:thin:@localhost:1521:xe");
			oxds1.setUser("tests");
			oxds1.setPassword("tests");
			dataSources.add(oxds1);
		}
	    
		if (oracle2) {
			OracleXADataSource oxds2 = new OracleXADataSource();
			oxds2.setURL("jdbc:oracle:thin:@localhost:1521:xe");
			oxds2.setUser("tests");
			oxds2.setPassword("tests");
			dataSources.add(oxds2);
		}
	
		if (mySql50) {
			MysqlXADataSource myxds1 = new MysqlXADataSource();
			myxds1.setUrl("jdbc:mysql://localhost:3306/test");
			myxds1.setUser("root");
			myxds1.setPassword("12345");
			dataSources.add(myxds1);
		}
	
		if (mySql55) {
			MysqlXADataSource myxds2 = new MysqlXADataSource();
			myxds2.setUrl("jdbc:mysql://localhost:3307/test");
			myxds2.setUser("root");
			myxds2.setPassword("12345");
			dataSources.add(myxds2);
		}
	    
		if (sqlServer) {
			SQLServerXADataSource sqlxds4 = new SQLServerXADataSource();
			sqlxds4.setServerName("localhost");
			sqlxds4.setPortNumber(1433);
			sqlxds4.setDatabaseName("test");
			sqlxds4.setUser("test");
			sqlxds4.setPassword("test!2");
			dataSources.add(sqlxds4);
		}
	
		if (postgres) {
			PGXADataSource pgxds5 = new PGXADataSource();
			pgxds5.setServerName("localhost");
			pgxds5.setPortNumber(5432);
			pgxds5.setDatabaseName("postgres");
			pgxds5.setUser("postgres");
			pgxds5.setPassword("12345");
			dataSources.add(pgxds5);
		}
		
		return dataSources;
	}

	public static List<XADataSource> getXADataSources() throws SQLException {
			List<XADataSource> dataSources = new LinkedList<XADataSource>();
			
			OracleXADataSource oxds1 = new OracleXADataSource();
	        oxds1.setURL("jdbc:oracle:thin:@localhost:1521:xe");
	        oxds1.setUser("tests");
	        oxds1.setPassword("tests");
	        dataSources.add(oxds1);
	
	//		OracleXADataSource oxds2 = new OracleXADataSource();
	//        oxds2.setURL("jdbc:oracle:thin:@localhost:1521:xe");
	//        oxds2.setUser("tests2");
	//        oxds2.setPassword("tests");
	//        dataSources.add(oxds2);
	        
	        MysqlXADataSource myxds1 = new MysqlXADataSource();
	        myxds1.setUrl("jdbc:mysql://localhost:3307/test");
	        myxds1.setUser("root");
	        myxds1.setPassword("12345");
			dataSources.add(myxds1);
			return dataSources;
		}

	public static List<XADataSource> createXADataSources(XADataSource... xaDataSources) 
	throws SQLException {
		List<XADataSource> dataSources = new LinkedList<XADataSource>();
		for (XADataSource xaDataSource : xaDataSources) {
			dataSources.add(xaDataSource);
		}
		return dataSources;
	}

	public static List<XADataSource> createXADataSources(Object[]... dsParamss) 
		throws SQLException, InstantiationException, IllegalAccessException {
			List<XADataSource> dataSources = new LinkedList<XADataSource>();
			
			for (int i = 0; i < dsParamss.length; i++) {
				Object[] dsParams = dsParamss[i];
				
				@SuppressWarnings("unchecked")
				Class<XADataSource> dsClass = (Class<XADataSource>) dsParams[0];
				XADataSource xaDataSource = dsClass.newInstance();
				
				String url = (String) dsParams[1];
				String userName = (String) dsParams[2];
				String password = (String) dsParams[3];
				
				if (xaDataSource instanceof OracleXADataSource) {
					OracleXADataSource oracleXADataSource = (OracleXADataSource) xaDataSource;
	//				oracleXADataSource.setDriverType("oci");
					oracleXADataSource.setURL(url);
					oracleXADataSource.setUser(userName);
					oracleXADataSource.setPassword(password);
				} else if (xaDataSource instanceof MysqlXADataSource) {
					MysqlXADataSource mysqlXADataSource = (MysqlXADataSource) xaDataSource;
					mysqlXADataSource.setUrl(url);
					mysqlXADataSource.setUser(userName);
					mysqlXADataSource.setPassword(password);
//				} else if (xaDataSource instanceof JtdsDataSource) {
//					JtdsDataSource jtdsDataSource = (JtdsDataSource) xaDataSource;
//					// jdbc:jtds:sqlserver://localhost:1433/test
//					String[] parts = url.split("://");
//					String[] srvPortDb = parts[1].split("/");
//					jtdsDataSource.setServerName(srvPortDb[0].split(":")[0]);
//					jtdsDataSource.setPortNumber(Integer.valueOf(srvPortDb[0].split(":")[1]));
//					jtdsDataSource.setDatabaseName(srvPortDb[1]);
//					jtdsDataSource.setUser(userName);
//					jtdsDataSource.setPassword(password);
//					jtdsDataSource.setXaEmulation(false);
				} else if (xaDataSource instanceof SQLServerXADataSource) {
					SQLServerXADataSource sqlServerXADataSource = (SQLServerXADataSource) xaDataSource;
					// jdbc:jtds:sqlserver://localhost:1433/test
					String[] parts = url.split("://");
					String[] srvPortDb = parts[1].split("/");
					sqlServerXADataSource.setServerName(srvPortDb[0].split(":")[0]);
					sqlServerXADataSource.setPortNumber(Integer.valueOf(srvPortDb[0].split(":")[1]));
					sqlServerXADataSource.setDatabaseName(srvPortDb[1]);
					sqlServerXADataSource.setUser(userName);
					sqlServerXADataSource.setPassword(password);
				} else if (xaDataSource instanceof PGXADataSource) {
					PGXADataSource pgxaDataSource = (PGXADataSource) xaDataSource;
					// jdbc:postgresql://localhost:5432/mydb
					String[] parts = url.split("://");
					String[] srvPortDb = parts[1].split("/");
					pgxaDataSource.setServerName(srvPortDb[0].split(":")[0]);
					pgxaDataSource.setPortNumber(Integer.valueOf(srvPortDb[0].split(":")[1]));
					pgxaDataSource.setDatabaseName(srvPortDb[1]);
					pgxaDataSource.setUser(userName);
					pgxaDataSource.setPassword(password);
				} else {
					throw new IllegalArgumentException(xaDataSource.getClass().getName());
				}
				dataSources.add(xaDataSource);
			}
			return dataSources;
		}

	public static List<XAConnection> getXAConnections(List<XADataSource> xaDataSources) 
	throws SQLException {
		List<XAConnection> xaConnections = new LinkedList<XAConnection>();
		for (XADataSource xaDataSource : xaDataSources) {
			xaConnections.add(xaDataSource.getXAConnection());
		}
		return xaConnections;
	}

	public static List<XAResource> getXaResources(List<XAConnection> xaConnections) 
	throws SQLException {
		List<XAResource> xaResources = new LinkedList<XAResource>();
		for (XAConnection xaConnection : xaConnections) {
			xaResources.add(xaConnection.getXAResource());
		}
		return xaResources;
	}

	public static List<XAResource> getXaResourcesWithProxies(List<XAConnection> xaConnections) 
	throws SQLException {
		List<XAResource> xaResources = new LinkedList<XAResource>();
		for (XAConnection xaConnection : xaConnections) {
			xaResources.add(getXaResourceWithProxy(xaConnection));
		}
		return xaResources;
	}

	public static XAResource getXaResourceWithProxy(final XAConnection xaConnection) throws SQLException {
			return new XAResource() {
				
				private XAResource delegate = xaConnection.getXAResource();
				
				public void start(Xid xid, int flags) throws XAException {
					System.err.println("start(Xid ..., int "+flags+") called on " + delegate);
					delegate.start(xid, flags);
				}
				public boolean setTransactionTimeout(int seconds) throws XAException {
					System.err.println("setTransactionTimeout(int "+seconds+") called on " + delegate);
					return delegate.setTransactionTimeout(seconds);
				}
				public void rollback(Xid xid) throws XAException {
					System.err.println("rollback(Xid "+xid+") called on " + delegate);
					try {
						delegate.rollback(xid);
					} catch (XAException e) {
						e.printStackTrace();
						throw e;
					}
				}
				public Xid[] recover(int flag) throws XAException {
					try {
						Xid[] xids = delegate.recover(flag);
						System.err.println("recover(int "+flag+") called on " + delegate + "\t\t\t" + Arrays.toString(xids));
						return xids;
					} catch (XAException e) {
						e.printStackTrace();
	//					throw e;
						return new Xid[] {};
					}
				}
				public int prepare(Xid xid) throws XAException {
					try {
						int prepare = delegate.prepare(xid);
						System.err.println(prepare+" = prepare(Xid ...) called on " + delegate);
						return prepare;
					} catch (XAException e) {
						e.printStackTrace();
						throw e;
					}
				}
				public boolean isSameRM(XAResource xares) throws XAException {
					boolean sameRM = delegate.isSameRM(xares);
					System.err.println(sameRM+" = isSameRM(XAResource "+xares+") called on " + delegate);
					return sameRM;
				}
				public int getTransactionTimeout() throws XAException {
					System.err.println("getTransactionTimeout() called on " + delegate);
					return delegate.getTransactionTimeout();
				}
				public void forget(Xid xid) throws XAException {
					System.err.println("forget(Xid "+xid+") called on " + delegate);
					delegate.forget(xid);
				}
				public void end(Xid xid, int flags) throws XAException {
					System.err.println("end(Xid ..., int "+flags+") called on " + delegate);
					delegate.end(xid, flags);
				}
				public void commit(Xid xid, boolean onePhase) throws XAException {
					System.err.println("commit(Xid "+xid+", boolean "+onePhase+") called on " + delegate);
					try {
						delegate.commit(xid, onePhase);
					} catch (XAException e) {
						e.printStackTrace();
						throw e;
					}
				}
			};
		}

	public static List<Connection> getConnections(List<XAConnection> xaConnections) 
	throws SQLException {
		List<Connection> connections = new LinkedList<Connection>();
		for (XAConnection xaConnection : xaConnections) {
			connections.add(xaConnection.getConnection());
		}
		return connections;
	}

	public static void closeConnections(List<Connection> connections) 
	throws SQLException {
		for (Connection connection : connections) {
			connection.close();
		}
	}

	public static void closeXAConnections(List<XAConnection> xaConnections) 
	throws SQLException {
		for (XAConnection xaConnection : xaConnections) {
			xaConnection.close();
		}
	}

	public static void closeConnections(Connection... connections) 
	throws SQLException {
		for (Connection connection : connections) {
			connection.close();
		}
	}
	
	public static void closeXAConnections(XAConnection... xaConnections) 
	throws SQLException {
		for (XAConnection xaConnection : xaConnections) {
			xaConnection.close();
		}
	}
	
	public static Xid createXid(int gtrid, int bqual) {
		return new MysqlXid(new byte[] {(byte)gtrid}, new byte[] {(byte)bqual}, 0);
	}

	public static void start(Xid xid, XAResource xaResource) throws XAException {
		xaResource.start(xid, XAResource.TMNOFLAGS);
	}

	public static void end(Xid xid, XAResource xaResource) throws XAException {
		xaResource.end(xid, XAResource.TMSUCCESS);
	}

	public static void suspend(Xid xid, XAResource xaResource) throws XAException {
		xaResource.end(xid, XAResource.TMSUSPEND);
	}

	public static void resume(Xid xid, XAResource xaResource) throws XAException {
		xaResource.start(xid, XAResource.TMRESUME);
	}

	public static void fail(Xid xid, XAResource xaResource) throws XAException {
		xaResource.end(xid, XAResource.TMFAIL);
	}

	public static void join(Xid xid, XAResource xaResource) throws XAException {
		xaResource.start(xid, XAResource.TMJOIN);
	}

	public static int prepare(Xid xid, XAResource xaResource) throws XAException {
		int prepare = xaResource.prepare(xid);
		System.out.println(
				prepare + " = prepare branch(" + Arrays.toString(xid.getGlobalTransactionId()) +
				", " + Arrays.toString(xid.getBranchQualifier()) + 
				") on " + xaResource);
		return prepare;
	}

	public static void commit(Xid xid, XAResource xaResource, int prepareVal) throws XAException {
		if (prepareVal == XAResource.XA_OK) {
			System.out.println("Committing branch " + xid + " on " + xaResource);
			xaResource.commit(xid, false);
		}
	}

	public static void rollback(Xid xid, XAResource xaResource, int prepareVal) throws XAException {
		if (prepareVal == XAResource.XA_OK) {
			System.out.println("Rolling back branch " + xid + " on " + xaResource);
			xaResource.rollback(xid);
		}
	}

	public static void rollback(Xid xid, XAResource xaResource) {
		System.out.println("Rolling back branch " + xid + " on " + xaResource);
		try {
			xaResource.rollback(xid);
		} catch (XAException e) {
			System.err.println(e);
		}
	}

}
