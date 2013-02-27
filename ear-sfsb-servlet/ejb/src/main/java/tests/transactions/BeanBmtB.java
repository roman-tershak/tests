package tests.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import tests.utils.BeanUtils;

@Stateful(mappedName="BeanBmtB")
@TransactionManagement(TransactionManagementType.BEAN)
public class BeanBmtB extends AbstractTransBean {

	private final int number;
	
	private static int numInsert;
	
	@Resource(mappedName="java:testOracleXaDs")
	private DataSource dataSource;
	
	@Resource
	private SessionContext sessionContext;
	
	@Resource
	private UserTransaction userTransaction;
	
	public BeanBmtB() {
		number = BeanUtils.getBeanInstanceNumber(getClass());
	}

	@Override
	public String opp1(TransRemote next, Map<String, Object> params)
			throws Exception {
		
		String result = BeanUtils.getBeanIdentity(getClass().getSimpleName(), number);
		
		userTransaction.begin();
		result += "userTransaction.begin() called\n";
		
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TABLE1 (COL1, COL2) VALUES (?, ?)");
		int rowId = (int) System.currentTimeMillis();
		preparedStatement.setInt(1, rowId);
		String dbIdentity = BeanUtils.getBeanIdentity(getClass().getSimpleName(), number, numInsert++);
		preparedStatement.setString(2, dbIdentity);
		
		String sql = "INSERT INTO TABLE1 (COL1, COL2) VALUES (" + rowId + ", '" + dbIdentity + "')";
		System.out.println(sql);
		
		preparedStatement.executeUpdate();
		connection.close();
		
		if (next != null) {
			next.opp1(null, params);
		}
		
		userTransaction.commit();
		result += "userTransaction.commit() called\n";
		
		return result;
	}
}
