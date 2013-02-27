package pres.transactions.cmt;

import java.sql.Connection;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import pres.AbstractBean;
import pres.GenericBeanInterface;
import pres.transactions.DBUtils;

@Stateless(mappedName="CMTStatelessBeanC")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CMTStatelessBeanC extends AbstractBean {

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		
		Connection oracleConn = oracleDs.getConnection();
		DBUtils.executeQuery(oracleConn, "SELECT * FROM TABLE1");
		oracleConn.close();
		
		Connection postgresConn = postgresDs.getConnection();
		DBUtils.executeQuery(postgresConn, "SELECT * FROM public.\"PGTABLE1\"");
		postgresConn.close();
		
		throwExceptionIfNeeded(params);
		markForRolbackIfNeeded(params);
		
		return super.opp(params, nexts);
	}
}
