package tests.jpa.entity.unidir.singleval.onetoone;

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
		
		entityManager.flush();
		transaction.commit();
	}

}
