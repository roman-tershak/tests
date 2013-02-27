package tests.jpa.entity.unidir.singleval.manytoone;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {

	private static EntityManager em;

	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-tests02-01");
		em = entityManagerFactory.createEntityManager();
		
//		simple01();
//		orphans();
		lazyFetching();
	}

	private static void simple01() {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		
		Address address = new Address();
		em.persist(address);
		
		// It MAY matter when you assign relations before or after persist operation
		// In either case data will be permanent after commit
		
/*
		Employee employee1 = new Employee();
		employee1.setAddress(address);
		em.persist(employee1);
		
		Employee employee2 = new Employee();
		employee2.setAddress(address);
		em.persist(employee2);
		
		em.flush();
		transaction.commit();
 */
//		Hibernate: select hibernate_sequence.nextval from dual
//		Hibernate: select hibernate_sequence.nextval from dual
//		Hibernate: select hibernate_sequence.nextval from dual
//		Hibernate: insert into Address_U_S_M2O (id) values (?)
//		Hibernate: insert into Employee_U_S_M2O (address_id, id) values (?, ?)
//		Hibernate: insert into Employee_U_S_M2O (address_id, id) values (?, ?)

/*		Employee employee1 = new Employee();
		em.persist(employee1);
		
		Employee employee2 = new Employee();
		em.persist(employee2);
		
		employee1.setAddress(address);
		employee2.setAddress(address);
		
		em.flush();
		transaction.commit();
 */
//		Hibernate: select hibernate_sequence.nextval from dual
//		Hibernate: select hibernate_sequence.nextval from dual
//		Hibernate: select hibernate_sequence.nextval from dual
//		Hibernate: insert into Address_U_S_M2O (id) values (?)
//		Hibernate: insert into Employee_U_S_M2O (address_id, id) values (?, ?)
//		Hibernate: insert into Employee_U_S_M2O (address_id, id) values (?, ?)
//		Hibernate: update Employee_U_S_M2O set address_id=? where id=?
//		Hibernate: update Employee_U_S_M2O set address_id=? where id=?
		
		Employee employee1 = new Employee();
		employee1.setAddress(address);
		em.persist(employee1);
		
		Employee employee2 = new Employee();
		employee2.setAddress(address);
		em.persist(employee2);
		
		
		em.flush();
		transaction.commit();
	}
	
	private static void orphans() {
		em.getTransaction().begin();
		
		Employee orphan = new Employee();
		em.persist(orphan);
		
		// When @ManyToOne(optional=false) on Employee.getAddress(), then we get this:
		// org.hibernate.PropertyValueException: not-null property references a null or transient value: tests.jpa.entity.unidir.singleval.manytoone.Employee.address
		em.getTransaction().commit();
	}
	
	private static void lazyFetching() {
		em.getTransaction().begin();
		
		Address address = new Address();
		em.persist(address);
		
		Employee employee1 = new Employee();
		employee1.setAddress(address);
		em.persist(employee1);
		
		Employee employee2 = new Employee();
		employee2.setAddress(address);
		em.persist(employee2);
		
		em.getTransaction().commit();
		em.clear();
		
		// ---------------------------------------------------------------------------
		System.out.println("Before the 2nd step");
		em.getTransaction().begin();
		
//		When the link is not lazy, then related entity(s) are fetched even if they not are referenced
//		Before the 2nd step
//		Hibernate: select employee0_.id as id6_, employee0_.address_id as address2_6_ from Employee_U_S_M2O employee0_
//		Hibernate: select address0_.id as id7_0_ from Address_U_S_M2O address0_ where address0_.id=?
//		
//		When it is lazy, then the related entities are NOT retrieved.
//		Before the 2nd step
//		Hibernate: select employee0_.id as id6_, employee0_.address_id as address2_6_ from Employee_U_S_M2O employee0_
		@SuppressWarnings("unchecked")
		List<Employee> employees = em.createQuery("select empl from Employee_U_S_M2O empl").getResultList();
		
		System.out.println("Some timeout");
		try { Thread.sleep(3000); } catch (InterruptedException e) {}

//		This is a behavior of persistence provider when lazy is used.
//		Before the 2nd step
//		Hibernate: select employee0_.id as id6_, employee0_.address_id as address2_6_ from Employee_U_S_M2O employee0_
//		Some timeout
//		Hibernate: select address0_.id as id7_0_ from Address_U_S_M2O address0_ where address0_.id=?
//		Address [id=1]
//		Employee [id=2, address.id=1]
//		Address [id=1]
//		Employee [id=3, address.id=1]
		for (Employee employee : employees) {
			System.out.println(employee.getAddress());
			System.out.println(employee);
		}
		
		em.getTransaction().commit();
	}
}
