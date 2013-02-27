package tests.jpainejb.jta.manypersunits;

import static javax.ejb.TransactionManagementType.BEAN;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import tests.jpainejb.jta.entities.MsSqlEntity;
import tests.jpainejb.jta.entities.MySqlEntity;
import tests.jpainejb.jta.entities.OracleEntity;
import tests.jpainejb.jta.entities.PGEntity;

@Stateless
//@TransactionManagement(BEAN)
public class StatelessManyUnitsBean implements StatelessManyUnitsLocal {

	@PersistenceContext(unitName="jpa-in-ejb-Oracle")
	private EntityManager emOracle;
	
	@PersistenceContext(unitName="jpa-in-ejb-MSSQL")
	private EntityManager emMsSql;
	
//	@PersistenceContext(unitName="jpa-in-ejb-PG")
//	private EntityManager emPG;
	
	@PersistenceContext(unitName="jpa-in-ejb-MySql")
	private EntityManager emMySql;
	
//	@Resource
//	private UserTransaction userTransaction;
	
	public void doSomethingSimple() {
		System.out.println("We are here!");
	}

	public void createEntitiesInManyUnits(String... names) throws Exception {
		createEntitiesInManyUnitsAndReturnIds(names);
	}

	public void createEntitiesInManyUnits() throws Exception {
		String now = new Date().toString();
		createEntitiesInManyUnits(now, now, now);
	}
	
	public Long[] createEntitiesInManyUnitsAndReturnIds(String... names)
			throws Exception {
		int i = 0;
		Long[] ids = new Long[names.length];
//		System.out.println("The UserTransaction is - " + userTransaction);
		
		// UserTransaction lookup or UserTransaction @Resource injection only works
		// when @TransactionManagement(BEAN) is specified:
		// javax.naming.NameNotFoundException: Name "comp/UserTransaction" not found.
//		userTransaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
//		System.out.println("The UserTransaction is - " + userTransaction);
		
//		userTransaction.begin();
		
		OracleEntity oracleEntity = new OracleEntity(names[0]);
		emOracle.persist(oracleEntity);
		ids[i++] = oracleEntity.getId();
		emOracle.flush();
		
		MsSqlEntity msSqlEntity = new MsSqlEntity(names[1]);
		emMsSql.persist(msSqlEntity);
		ids[i++] = msSqlEntity.getId();
		emMsSql.flush();
		
		MySqlEntity mySqlEntity = new MySqlEntity(names[2]);
		emMySql.persist(mySqlEntity);
		ids[i++] = mySqlEntity.getId();
		emMySql.flush();
		
//		PGEntity pgEntity = new PGEntity(names[2]);
//		emPG.persist(pgEntity);
//		ids[i++] = pgEntity.getId();
//		emPG.flush();
		
//		userTransaction.commit();
		return ids;
	}

}
