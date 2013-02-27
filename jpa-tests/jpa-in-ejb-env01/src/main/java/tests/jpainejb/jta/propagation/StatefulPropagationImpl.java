package tests.jpainejb.jta.propagation;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import tests.jpainejb.jta.entities.MsSqlEntity;
import tests.jpainejb.jta.entities.MySqlEntity;
import tests.jpainejb.jta.entities.OracleEntity;
import tests.jpainejb.jta.entities.PGEntity;
import tests.jpainejb.jta.manypersunits.Main;

@Stateful
//@TransactionManagement(TransactionManagementType.BEAN)
public class StatefulPropagationImpl implements StatefulPropagation {

	@EJB
	private StatefulPropagationCallee statefulPropagationCallee;
	
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
	
	public void doSomething(String... names) throws Exception {
		System.out.println("in doSomething(String... names)");
		
		OracleEntity oe = (OracleEntity) emOracle.createQuery(
				"select oe from OracleEntity_MANY_UN oe where oe.name = '" + names[0] + "'").getSingleResult();
		
		MsSqlEntity mse = (MsSqlEntity) emMsSql.createQuery(
				"select mse from MsSqlEntity_MANY_UN mse where mse.name = '" + names[1] + "'").getSingleResult();
		
		MySqlEntity mye = (MySqlEntity) emMySql.createQuery(
				"select mye from MySqlEntity_MANY_UN mye where mye.name = '" + names[2] + "'").getSingleResult();
		MySqlEntity mye2 = (MySqlEntity) emMySql.createQuery(
				"select mye from MySqlEntity_MANY_UN mye where mye.name = '" + names[2] + "'").getSingleResult();
		
		System.out.println(oe);
		System.out.println(mse);
		System.out.println(mye);
		System.out.println(mye2);
		
		statefulPropagationCallee.doSomethingInCallee(names);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void doSomething(Long... ids) throws Exception {
		System.out.println("in doSomething(Long... ids)");
		
//		System.out.println(userTransaction);
//		userTransaction.begin();
		
		System.out.println(((TransactionManager) Main.getInitialContext().lookup("java:openejb/TransactionManager")).getTransaction());

		
		OracleEntity oe = emOracle.find(OracleEntity.class, ids[0]);
		MsSqlEntity mse = emMsSql.find(MsSqlEntity.class, ids[1]);
		MySqlEntity mye = emMySql.find(MySqlEntity.class, ids[2]);
//		PGEntity pge = emPgSql.find(PGEntity.class, ids[2]);
		
		System.out.println(oe);
		System.out.println(mse);
		System.out.println(mye);
//		System.out.println(pge);
		
		try {
			statefulPropagationCallee.doSomethingInCallee(ids);
		} catch (Exception e) {
			System.out.println("Printing the exception...");
			System.err.println(e);
//			throw e;
		}
		
		System.out.println("Before update in the caller...");
		String newName = "caller 2c - " + new Date();
		oe.setName(newName);
		mse.setName(newName);
		mye.setName(newName);
//		pge.setName(newName);

//		System.out.println("Before commit in the caller...");
//		userTransaction.rollback();
		
//		throw new EJBException();
	}
}
