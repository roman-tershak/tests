package pres.transactions.cmt;

import java.sql.Connection;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.jboss.ejb3.annotation.Clustered;

import pres.GenericBeanInterface;
import pres.transactions.DBUtils;

@Stateless(mappedName="CMTStatelessBeanAClustered")
@TransactionManagement(TransactionManagementType.CONTAINER)
@Clustered
public class CMTStatelessBeanAClustered extends CMTStatelessBeanA {
	
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts)
			throws Exception {
		Object result = super.opp(params, nexts);
		
		System.out.println("Now trying to examine the state of the data...\n");
		
		Connection oracleConn = oracleDs.getConnection();
		Connection postgresConn = postgresDs.getConnection();
		
		DBUtils.executeQuery(oracleConn, "SELECT * FROM TABLE1");
		DBUtils.executeQuery(postgresConn, "SELECT * FROM public.\"PGTABLE1\"");

		oracleConn.close();
		postgresConn.close();
		
		return result;
	}
}
