/**
 * 
 */
package jta_tests.test02;

import static jta_tests.test02.TransUtil.delistSuccess;
import static jta_tests.test02.TransUtil.enlist;

import java.sql.Connection;
import java.util.List;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

final class ThreadWorker extends Thread {
	private final Transaction trans;
	private final XADataSource xaDataSource;
	private final String sqlString;
	private final int numOfRuns;
	private final List<XAConnection> xaConns;

	ThreadWorker(Transaction trans, XADataSource xaDataSource, String sqlString, int numOfRuns, List<XAConnection> xaConns) {
		this.trans = trans;
		this.xaDataSource = xaDataSource;
		this.sqlString = sqlString;
		this.numOfRuns = numOfRuns;
		this.xaConns = xaConns;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < numOfRuns; i++) {
				XAConnection xaConn = xaDataSource.getXAConnection();
				xaConns.add(xaConn);
				
				XAResource xaRes = xaConn.getXAResource();
				Connection conn = xaConn.getConnection();
				
				enlist(trans, xaRes);
				conn.createStatement().execute(sqlString);
				delistSuccess(trans, xaRes);
				
				conn.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}