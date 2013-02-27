package pres.transactions.bmt;

import java.sql.Connection;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import pres.AbstractBean;
import pres.GenericBeanInterface;
import pres.transactions.DBUtils;

@Stateless(mappedName = "BMTStatelessBeanBWithoutTransaction")
@TransactionManagement(TransactionManagementType.BEAN)
public class BMTStatelessBeanBWithoutTransaction extends AbstractBean {

	@Resource
	private UserTransaction userTransaction;
	
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {

		Connection oracleConn = null;
		Connection postgresConn = null;
			
		try {
			System.out.println("Transaction status - " + DBUtils.convertTransactionStatus(userTransaction.getStatus()));
			
			oracleConn = oracleDs.getConnection();
			DBUtils.executeQuery(oracleConn, "SELECT * FROM TABLE1");

			postgresConn = postgresDs.getConnection();
			DBUtils.executeQuery(postgresConn, "SELECT * FROM public.\"PGTABLE1\"");

			throwExceptionIfNeeded(params);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeDbConnection(oracleConn);
			DBUtils.closeDbConnection(postgresConn);
		}

		return null;
	}
}
