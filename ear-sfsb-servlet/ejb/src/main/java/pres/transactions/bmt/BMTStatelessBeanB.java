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

@Stateless(mappedName = "BMTStatelessBeanB")
@TransactionManagement(TransactionManagementType.BEAN)
public class BMTStatelessBeanB extends AbstractBean {

	@Resource
	private UserTransaction userTransaction;

	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts)
			throws Exception {

		Object result = null;
		Connection oracleConn = null;
		Connection postgresConn = null;
		try {
			userTransaction.begin();

			oracleConn = oracleDs.getConnection();
			DBUtils.executeQuery(oracleConn, "SELECT * FROM TABLE1");

			postgresConn = postgresDs.getConnection();
			DBUtils.executeQuery(postgresConn, "SELECT * FROM public.\"PGTABLE1\"");

			throwExceptionIfNeeded(params);
			markForRolbackIfNeeded(userTransaction, params);

			userTransaction.commit();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtils.closeDbConnection(oracleConn);
			DBUtils.closeDbConnection(postgresConn);
		}

		return result;
	}
}
