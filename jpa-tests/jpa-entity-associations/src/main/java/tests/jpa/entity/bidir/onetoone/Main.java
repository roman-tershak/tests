package tests.jpa.entity.bidir.onetoone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {

	private static EntityManager entityManager;

	public static void main(String[] args) {
		test01();
	}

	private static void test01() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-tests02-01");
		entityManager = entityManagerFactory.createEntityManager();
		
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Employee employee = new Employee();
		Cubicle assignedCubicle = new Cubicle();
		
		assignedCubicle.setNumer("numer");
		employee.setAssignedCubicle(assignedCubicle);
		assignedCubicle.setResidentEmployee(employee);
		
		entityManager.persist(assignedCubicle);
		entityManager.persist(employee);
		
		entityManager.flush();
		transaction.commit();
	}

}
