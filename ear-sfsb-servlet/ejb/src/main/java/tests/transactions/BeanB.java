package tests.transactions;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;

import tests.utils.BeanUtils;

@Stateful(mappedName="BeanB")
@TransactionManagement(TransactionManagementType.CONTAINER)
//@AccessTimeout(value=5, unit=TimeUnit.SECONDS) - EJB 3.1 feature
public class BeanB extends AbstractTransBean implements SessionSynchronization {

	private final int number;
	
	@Resource(mappedName="java:testOracleXaDs")
	private DataSource dataSource;
	
	@Resource
	private SessionContext sessionContext;
	
	public BeanB() {
		number = BeanUtils.getBeanInstanceNumber(getClass());
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String opp1(TransRemote next, Map<String, Object> params) throws Exception {
		
		String result = BeanUtils.getBeanIdentity(getClass().getSimpleName(), number);
		
		Connection connection = dataSource.getConnection();
//		
//		PreparedStatement preparedStatement;
//		if (Boolean.valueOf((String) params.get("byRowId"))) {
//			preparedStatement = connection.prepareStatement("SELECT * FROM TABLE1 WHERE COL1 = ?");
//			Integer rowId = (Integer) params.get("rowId");
//			preparedStatement.setInt(1, rowId);
//			
//			System.out.println("SELECT * FROM TABLE1 WHERE COL1 = " + rowId);
//		} else {
//			preparedStatement = connection.prepareStatement("SELECT * FROM TABLE1");
//			
//			System.out.println("SELECT * FROM TABLE1");
//		}
//		ResultSet rs = preparedStatement.executeQuery();
//		
//		while (rs.next()) {
//			result += rs.getInt(1) + ", " + rs.getString(2) + "\n";
//		}
		
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TABLE1 (COL1, COL2) VALUES (?, ?)");
		int rowId = (int) System.currentTimeMillis();
		preparedStatement.setInt(1, rowId);
		String dbIdentity = BeanUtils.getBeanIdentity(getClass().getSimpleName(), number);
		preparedStatement.setString(2, dbIdentity);
		
		String sql = "INSERT INTO TABLE1 (COL1, COL2) VALUES (" + rowId + ", '" + dbIdentity + "')";
		System.out.println(sql);
		
		preparedStatement.executeUpdate();
		
		connection.close();
		
		int mode = 0;
		if (params.containsKey("mode")) {
			mode = new Integer((String) params.get("mode"));
		}
		
		switch (mode) {
		case 1:
			// 1. When transactions are not propagated to/from ejb containers, sessionContext.setRollbackOnly() does not propagate back 
			// to the calling node, and so, does not rollback the parent transaction (if any)
			// Although on one server node everything works fine.
			
			// When JTS is used the propagation works fine (transactions propagate and setRollbackOnly too)
			sessionContext.setRollbackOnly();
			break;
		case 2:
			// 2. Checked Exception does not rollback the underlying transaction by default
			throw new Exception("Some My Exception");
		case 3:
			// 3. Runtime Exception DOES rollback the underlying transaction by default
			throw new RuntimeException("Some My RuntimeException");
		case 4:
			// 4. Application Exception either rolls back or not the transaction depending on
			// @javax.ejb.ApplicationException(rollback=true) or false
			throw new ApplicationException("Some app exception");
		}
		
		return result;
	}
	
//	@AfterBegin - not supported
	public void afterBegin() throws EJBException, RemoteException {
		System.out.println(BeanUtils.getBeanIdentity(
				getClass().getSimpleName(), number, " - afterBegin()"));
	}

//	@BeforeCompletion - not supported
	public void beforeCompletion() throws EJBException, RemoteException {
		System.out.println(BeanUtils.getBeanIdentity(
				getClass().getSimpleName(), number, " - beforeCompletion()"));
	}
	
//	@AfterCompletion - not supported
	public void afterCompletion(boolean committed) throws EJBException, RemoteException {
		System.out.println(BeanUtils.getBeanIdentity(
				getClass().getSimpleName(), number, " - afterCompletion(" + committed + ")"));
	}
}
