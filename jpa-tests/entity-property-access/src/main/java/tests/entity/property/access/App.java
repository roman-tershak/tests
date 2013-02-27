package tests.entity.property.access;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class App {
	
	private static EntityManager entityManager;

	public static void main(String[] args) {
		test01();
	}

	private static void test01() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-tests-01");
		entityManager = entityManagerFactory.createEntityManager();
		
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Employee employee = new Employee();
		employee.setId(2L);
		employee.setName("name");
		employee.setTransientName("transientName");
		employee.setSurname("surname");
		
		entityManager.persist(employee);
		entityManager.flush();
		transaction.commit();
	}

}
