package tests.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import tests.utils.BeanUtils;

@Stateful(mappedName="BeanBmtA")
//@Clustered
@TransactionManagement(TransactionManagementType.BEAN)
public class BeanBmtA extends AbstractTransBean {

	private final int number;
	
	private static int numInsert;
	
	@Resource(mappedName="java:testOracleXaDs")
	private DataSource dataSource;
	
	@Resource
//	When a bean is @Clustered UserTransaction does not move along with the bean replication between nodes in the cluster
	private UserTransaction userTransaction;

	public BeanBmtA() {
		number = BeanUtils.getBeanInstanceNumber(getClass());
	}
	
	@Override
	public String bmtOpp1(TransRemote next, Map<String, Object> params)
			throws Exception {
		userTransaction.begin();
		return BeanUtils.getBeanIdentity(getClass().getSimpleName(), number, " - userTransaction started.");
	}
	
	@Override
	public String bmtOpp2(TransRemote next, Map<String, Object> params)
			throws Exception {
		
		String sql = null;
		for (int i = 0; i < 7; i++) {

			bmtOpp1(null, params);
			
			Connection connection = dataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TABLE1 (COL1, COL2) VALUES (?, ?)");
			int rowId = (int) System.currentTimeMillis();
			preparedStatement.setInt(1, rowId);
			String dbIdentity = BeanUtils.getBeanIdentity(getClass().getSimpleName(), number, numInsert++);
			preparedStatement.setString(2, dbIdentity);

			sql = "INSERT INTO TABLE1 (COL1, COL2) VALUES (" + rowId + ", '" + dbIdentity + "')";
			System.out.println(sql);
			preparedStatement.executeUpdate();

			connection.close();

			if (next != null) {
				
				if (i == 1 || i == 4) {
					params.put("mode", "3");
				} else {
					params.put("mode", "0");
				}
				
				try {
					next.opp1(null, params);
					System.out.println(userTransaction.getStatus());
				} catch (RuntimeException e) {
					System.out.println("RuntimeException caugth:");
					e.printStackTrace();
					//				userTransaction.rollback();
				} catch (Exception e) {
					System.out.println("Exception caugth:");
					e.printStackTrace();
				}
			}
			
			bmtOpp3(null, params);
		}
		return sql;
	}
	
	@Override
	public String bmtOpp3(TransRemote next, Map<String, Object> params)
			throws Exception {
		try {
			userTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				userTransaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return BeanUtils.getBeanIdentity(getClass().getSimpleName(), number, " - userTransaction stopped.");
	}
}
