package tests.jta.atomikos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;

import tests.jpainejb.jta.util.Utils;

import jta_tests.test02.XAUtils;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;

public class Main {

	public static void main(String[] args) throws Exception {
		List<AtomikosDataSourceBean> dss = initDataSources();
		
		UserTransaction userTransaction = new UserTransactionImp();

		userTransaction.begin();
		userTransaction.setTransactionTimeout(300);

		try {
			
			String num = "a20";
			int i = 0;
			dss.get(i++).getConnection().createStatement().executeUpdate("insert into table1 (col1, col2, col3) values ('atomikos tests "+num+"', '"+num+"', 1)");
			dss.get(i++).getConnection().createStatement().executeUpdate("insert into table1 (col1, col2, col3) values ('atomikos tests "+num+"', '"+num+"', 1)");
			dss.get(i++).getConnection().createStatement().executeUpdate("insert into table1 (col1, col2, col3) values ('atomikos tests "+num+"', '"+num+"', 1)");
			dss.get(i++).getConnection().createStatement().executeUpdate("insert into table1 (col1, col2) values ('atomikos tests "+num+"', 1)");

			userTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			userTransaction.rollback();
		}
	}

	private static List<AtomikosDataSourceBean> initDataSources() throws Exception {
		
		List<XADataSource> xaDataSources = XAUtils.getXADataSources(true, false, false, true, true, true);
		List<XADataSource> xaDataSourceProxies = wrapToProxies2(xaDataSources);
		
		String[] dsNames = new String[] {"dsOracle", "dsMySql", "dsSqlServer", "dsPostgreSql"};
		
		List<AtomikosDataSourceBean> dataSourceBeans=  new LinkedList<AtomikosDataSourceBean>();
		int i = 0;
		for (XADataSource xaDataSource : xaDataSourceProxies) {
			
			AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
			dataSourceBean.setUniqueResourceName(dsNames[i++]);
			dataSourceBean.setXaDataSource(xaDataSource);
			dataSourceBean.setPoolSize(5);
			
			dataSourceBeans.add(dataSourceBean);
		}
		
//		Context ctx = new InitialContext();
//		i = 0;
//		for (AtomikosDataSourceBean dataSourceBean : dataSourceBeans) {
//			ctx.rebind(dsNames[i++], dataSourceBean);
//		}
//		ctx.close();
		Collections.reverse(dataSourceBeans);
		return dataSourceBeans;
	}

	private static List<XADataSource> wrapToProxies(List<XADataSource> xaDataSources) {
		List<XADataSource> xaDataSourceProxies = new LinkedList<XADataSource>();
		for (XADataSource xaDataSource : xaDataSources) {
			xaDataSourceProxies.add(Utils.createXaDataSourceProxy(xaDataSource, "getXAResource"));
		}
//		return xaDataSourceProxies;
		return xaDataSources;
	}

	private static List<XADataSource> wrapToProxies2(List<XADataSource> xaDataSources) {
		List<XADataSource> xaDataSourceProxies = new LinkedList<XADataSource>();
		xaDataSourceProxies.add(Utils.createXaDataSourceProxy(xaDataSources.get(0), XAConnection.class, PooledConnection.class, XAResource.class));
		xaDataSourceProxies.add(xaDataSources.get(1));
		xaDataSourceProxies.add(xaDataSources.get(2));
		xaDataSourceProxies.add(xaDataSources.get(3));
		return xaDataSourceProxies;
	}

}
