package tests.jpa.entity.unidir.multival.manytomany;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Main {

	private static EntityManager em = Persistence.createEntityManagerFactory("jpa-tests02-01").createEntityManager();

	public static void main(String[] args) {
//		creation01();
//		readOnlyAccess();
//		removingEntitiesFromAssociation();
		removingAssociatedEntities();
		
		System.out.println("Done!");
	}

	private static void creation01() {
		em.getTransaction().begin();
		
		Employee employee1 = new Employee("empo 1");
		Employee employee2 = new Employee("empo 2");
		Employee employee3 = new Employee("empo 3");
		Employee employee4 = new Employee("empo 4");
		// Not a problem here, even if @Column(nullable=false, updatable=false, unique=true) 
//		employee4.setName("empo 4a");
		em.persist(employee1);
		em.persist(employee2);
		em.persist(employee3);
		em.persist(employee4);
		
		// ... and no problem here:
//		employee4.setName("empo 4a");
		
		Patent patent1 = new Patent("about q");
		Patent patent2 = new Patent("about p");
		Patent patent3 = new Patent("about r");
		Patent patent4 = new Patent("about s");
		em.persist(patent1);
		em.persist(patent2);
		em.persist(patent3);
		em.persist(patent4);
		
		employee1.getPatents().add(patent1);
		employee1.getPatents().add(patent2);
		
		employee2.getPatents().add(patent2);
		employee2.getPatents().add(patent4);
		
		employee3.getPatents().add(patent3);
		
		employee4.getPatents().add(patent1);
		
		System.out.println(employee1);
		
		em.getTransaction().commit();
	}
	
	private static void readOnlyAccess() {
		creation01();
		em.clear();
		
		em.getTransaction().begin();
		
		Employee employee = (Employee) em.createQuery(
				"select emp from Employee_UM_M2M emp where name = 'empo 4'").getSingleResult();
		
		// here it does not do an update, just silently ignores setName(..)

//		with updatable=true (or without updatable at all)
//		Hibernate: select * from ( select employee0_.id as id12_, employee0_.name as name12_ from Employee_UM_M2M employee0_ where employee0_.name='empo 4' ) where rownum <= ?
//		Hibernate: update Employee_UM_M2M set name=? where id=?
//		Done!
		
//		and with updatable=false :
//		Hibernate: select * from ( select employee0_.id as id12_, employee0_.name as name12_ from Employee_UM_M2M employee0_ where employee0_.name='empo 4' ) where rownum <= ?
//		Done!
		employee.setName("empo 4a");
		
		em.getTransaction().commit();
	}
	
	private static void removingEntitiesFromAssociation() {
		creation01();
		em.clear();
		
		em.getTransaction().begin();
		
		Employee employee = (Employee) em.createQuery(
				"select emp from Employee_UM_M2M emp where name = 'empo 1'").getSingleResult();
		
		Patent patent = employee.getPatents().iterator().next();
		employee.getPatents().remove(patent);
		
		em.getTransaction().commit();
	}
	
	private static void removingAssociatedEntities() {
		creation01();
		em.clear();
		
		em.getTransaction().begin();
		
		Employee employee = (Employee) em.createQuery(
				"select emp from Employee_UM_M2M emp where name = 'empo 3'").getSingleResult();
		
		Patent patent = employee.getPatents().iterator().next();
//		In order to remove associated entity it is required to remove it 
//		from the associations too, in other case we will get this:
//		java.sql.BatchUpdateException: ORA-02292: integrity constraint (TESTS.FK24A64A2B5D0EF64C) violated - child record found
//		em.remove(patent);
		
//		This works good:
//		Hibernate: select patents0_.Employee_UM_M2M_id as Employee1_1_, patents0_.patents_id as patents2_1_, patent1_.id as id13_0_, patent1_.about as about13_0_ from Employee_UM_M2M_Patent_UM_M2M patents0_ left outer join Patent_UM_M2M patent1_ on patents0_.patents_id=patent1_.id where patents0_.Employee_UM_M2M_id=?
//		Hibernate: delete from Employee_UM_M2M_Patent_UM_M2M where Employee_UM_M2M_id=?
//		Hibernate: delete from Patent_UM_M2M where id=?
		employee.getPatents().remove(patent);
		em.remove(patent);
		
		em.getTransaction().commit();
	}
}
