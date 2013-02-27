package tests.transactions;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;

import org.jboss.ejb3.annotation.Clustered;

import tests.utils.BeanUtils;

@Stateful(mappedName="BeanA")
@TransactionManagement(TransactionManagementType.CONTAINER)
@Clustered
public class BeanA extends AbstractTransBean implements SessionSynchronization {

	private final int number;
	
	@Resource(mappedName="java:testOracleXaDs")
	private DataSource dataSource;
	
	public BeanA() {
		number = BeanUtils.getBeanInstanceNumber(getClass());
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String opp1(TransRemote next, Map<String, Object> params) throws Exception {
		
		String result = BeanUtils.getBeanIdentity(getClass().getSimpleName(), number);
		
		Connection connection = dataSource.getConnection();
		
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TABLE1 (COL1, COL2) VALUES (?, ?)");
		int rowId = (int) System.currentTimeMillis();
		preparedStatement.setInt(1, rowId);
		preparedStatement.setString(2, "test 01");
		
		System.out.println("INSERT INTO TABLE1 (COL1, COL2) VALUES (" + rowId + ", 'test 01')");
		
		result += " - " + preparedStatement.executeUpdate();
		
		connection.close();
		
		if (next != null) {
			params.put("rowId", rowId);
			
			try {
				result += "\n" + next.opp1(null, params);
			} catch (Exception e) {
				System.err.println("Catching the exception");
				e.printStackTrace();
			}
		}
		return result;
	}

//	@AfterBegin
	public void afterBegin() throws EJBException, RemoteException {
		System.out.println(BeanUtils.getBeanIdentity(
				getClass().getSimpleName(), number, " - afterBegin()"));
	}

//	@BeforeCompletion
	public void beforeCompletion() throws EJBException, RemoteException {
		System.out.println(BeanUtils.getBeanIdentity(
				getClass().getSimpleName(), number, " - beforeCompletion()"));
	}
	
//	@AfterCompletion
	public void afterCompletion(boolean committed) throws EJBException, RemoteException {
		System.out.println(BeanUtils.getBeanIdentity(
				getClass().getSimpleName(), number, " - afterCompletion(" + committed + ")"));
	}
}
