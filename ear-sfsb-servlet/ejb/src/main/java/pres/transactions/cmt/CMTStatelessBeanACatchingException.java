package pres.transactions.cmt;

import java.sql.Connection;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import pres.GenericBeanInterface;
import pres.transactions.DBUtils;

@Stateless(mappedName="CMTStatelessBeanACatchingException")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CMTStatelessBeanACatchingException extends CMTStatelessBeanA {

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		Object result;
		try {
			result = super.opp(params, nexts);
		} catch (Exception e) {
			System.out.println("Got exception!:\n");
			e.printStackTrace();
			result = e.getMessage();
		}
		
		System.out.println("Now trying to examine the state of the data...\n");
		
		Connection oracleConn = oracleDs.getConnection();
		DBUtils.executeQuery(oracleConn, "SELECT * FROM TABLE1");
		oracleConn.close();
		
		Connection postgresConn = postgresDs.getConnection();
		DBUtils.executeQuery(postgresConn, "SELECT * FROM public.\"PGTABLE1\"");
		postgresConn.close();
		
		return result;
	}

}
