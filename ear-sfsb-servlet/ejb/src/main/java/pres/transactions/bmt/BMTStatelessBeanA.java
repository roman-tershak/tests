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

@Stateless(mappedName="BMTStatelessBeanA")
@TransactionManagement(TransactionManagementType.BEAN)
public class BMTStatelessBeanA extends AbstractBean {

	@Resource
	private UserTransaction userTransaction;
	
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) {
		
		Object result = null;
		Connection oracleConn = null;
		Connection postgresConn = null;
		try {
			userTransaction.begin();

			Integer rowId = (Integer) params.get("rowId");
			String rowDetails = "inserted " + rowId + " in " + getClass().getSimpleName();

			oracleConn = oracleDs.getConnection();
			DBUtils.execute(oracleConn, "INSERT INTO TABLE1 (COL1, COL2) VALUES (?, ?)", rowId, rowDetails);

			postgresConn = postgresDs.getConnection();
			DBUtils.execute(postgresConn, "INSERT INTO public.\"PGTABLE1\" (\"COL1\", \"COL2\") VALUES (?, ?)", rowId, rowDetails);

			result = super.opp(params, nexts);
			
			System.out.println("Now trying to examine the state of the data...\n");
			
			DBUtils.executeQuery(oracleConn, "SELECT * FROM TABLE1");
			DBUtils.executeQuery(postgresConn, "SELECT * FROM public.\"PGTABLE1\"");

			userTransaction.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.out.println("Rolling the transaction back...\n");
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
