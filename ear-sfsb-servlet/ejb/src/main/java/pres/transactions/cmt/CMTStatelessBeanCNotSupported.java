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

@Stateless(mappedName="CMTStatelessBeanCNotSupported")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CMTStatelessBeanCNotSupported extends CMTStatelessBeanC {

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Object opp(Map<String, Object> params, GenericBeanInterface... nexts) throws Exception {
		
		Integer rowId = (Integer) params.get("rowId2");
		rowId++;
		String rowDetails = "inserted " + rowId + " in " + getClass().getSimpleName();
		
		Connection oracleConn = oracleDs.getConnection();
		DBUtils.execute(oracleConn, "INSERT INTO TABLE1 (COL1, COL2) VALUES (?, ?)", rowId, rowDetails);
		oracleConn.close();
		
		Connection postgresConn = postgresDs.getConnection();
		DBUtils.execute(postgresConn, "INSERT INTO public.\"PGTABLE1\" (\"COL1\", \"COL2\") VALUES (?, ?)", rowId, rowDetails);
		postgresConn.close();
		
		params.put("rowId3", rowId);
		
		return super.opp(params, nexts);
	}
}
