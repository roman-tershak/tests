package tests.jpainejb.jta.propagation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import tests.jpainejb.jta.entities.MsSqlEntity;
import tests.jpainejb.jta.entities.MySqlEntity;
import tests.jpainejb.jta.entities.OracleEntity;
import tests.jpainejb.jta.entities.PGEntity;
import tests.jpainejb.jta.manypersunits.Main;

@Stateful
//@TransactionManagement(TransactionManagementType.BEAN)
public class StatefulPropagationCalleeImpl implements StatefulPropagationCallee {

	@PersistenceContext(unitName="jpa-in-ejb-Oracle", type=PersistenceContextType.EXTENDED)
	private EntityManager emOracle;
	
	@PersistenceContext(unitName="jpa-in-ejb-MSSQL", type=PersistenceContextType.EXTENDED)
	private EntityManager emMsSql;
	
	@PersistenceContext(unitName="jpa-in-ejb-MySql", type=PersistenceContextType.EXTENDED)
	private EntityManager emMySql;
	
//	@PersistenceContext(unitName="jpa-in-ejb-PG", type=PersistenceContextType.EXTENDED)
//	private EntityManager emPgSql;
	
//	@Resource
//	private UserTransaction userTransaction;
	
	@Resource(mappedName="XAOracleDS")
	private DataSource xaOracleDS;
	
//	@Resource(mappedName="XAMsSqlDS")
//	private DataSource xaMsSqlDS;
	
	@Resource(mappedName="XAMySqlDS")
	private DataSource xaMySqlDS;
	
//	@Resource
//	private TransactionManager transactionManager;
	
	public void doSomethingInCallee(String... names) throws Exception {
		System.out.println("in doSomethingInCallee");
		
		OracleEntity oe = (OracleEntity) emOracle.createQuery(
				"select oe from OracleEntity_MANY_UN oe where oe.name = '" + names[0] + "'").getSingleResult();
		
		MsSqlEntity mse = (MsSqlEntity) emMsSql.createQuery(
				"select mse from MsSqlEntity_MANY_UN mse where mse.name = '" + names[1] + "'").getSingleResult();
		
		MySqlEntity mye = (MySqlEntity) emMySql.createQuery(
				"select mye from MySqlEntity_MANY_UN mye where mye.name = '" + names[2] + "'").getSingleResult();
		
		System.out.println(oe);
		System.out.println(mse);
		System.out.println(mye);
	}
	
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void doSomethingInCallee(Long... ids) throws Exception {
		System.out.println("in doSomethingInCallee(Long... ids)");
		
//		System.out.println(userTransaction);
//		userTransaction.begin();
		
//		emOracle.joinTransaction();
//		emMsSql.joinTransaction();
//		emMySql.joinTransaction();
//		UserTransaction ut = (UserTransaction) Main.getInitialContext().lookup("java:comp/UserTransaction");
//		ut.setRollbackOnly();
			
//		try {
//			transactionManager = (TransactionManager) Main.getInitialContext().lookup("java:openejb/TransactionManager");
//			System.out.println(transactionManager.getTransaction().getStatus());
//		} catch (Exception e) {
//			System.err.println(e);
//		}
		
		OracleEntity oe = emOracle.find(OracleEntity.class, ids[0]);
		MsSqlEntity mse = emMsSql.find(MsSqlEntity.class, ids[1]);
		MySqlEntity mye = emMySql.find(MySqlEntity.class, ids[2]);
//		PGEntity pge = emPgSql.find(PGEntity.class, ids[2]);

//		System.out.println(xaOracleDS);
//		System.out.println(xaMsSqlDS);
//		System.out.println(xaMySqlDS);
		
		System.out.println(oe);
		System.out.println(mse);
		System.out.println(mye);
//		System.out.println(pge);
		
		Date now = new Date();
		String newName = "callee 1c - " + now;
		oe.setName(newName);
		mse.setName(newName);
		mye.setName(newName);
//		pge.setName(newName);
		
		System.out.println("Before flush...");
		emOracle.flush();
		emMsSql.flush();
		emMySql.flush();
//		emPgSql.flush();
		System.out.println("After flush...");
		
		ResultSet rs;
		PreparedStatement stmt;
		stmt = xaOracleDS.getConnection().prepareStatement("select * from OracleEntity_MANY_UN where name = ?");
		stmt.setString(1, newName);
		rs = stmt.executeQuery();
		while (rs.next()) {
			System.out.println(rs.getLong(1) + ", " + rs.getString(2) + ", " + rs.getLong(3));
		}
		
//		stmt = xaMsSqlDS.getConnection().prepareStatement("select * from MsSqlEntity_MANY_UN where name = ?");
//		stmt.setString(1, newName);
//		rs = stmt.executeQuery();
//		while (rs.next()) {
//			System.out.println(rs.getLong(1) + ", " + rs.getString(2) + ", " + rs.getLong(3));
//		}
		
		stmt = xaMySqlDS.getConnection().prepareStatement("select * from MySqlEntity_MANY_UN where name = ?");
		stmt.setString(1, newName);
		rs = stmt.executeQuery();
		while (rs.next()) {
			System.out.println(rs.getLong(1) + ", " + rs.getString(2) + ", " + rs.getLong(3));
		}
		
//		throw new EJBException("Exception from callee");
//		userTransaction.rollback();
	}

}
