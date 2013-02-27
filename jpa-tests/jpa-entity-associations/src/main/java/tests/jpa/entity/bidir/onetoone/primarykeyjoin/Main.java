package tests.jpa.entity.bidir.onetoone.primarykeyjoin;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {

	private static EntityManager em = Persistence.createEntityManagerFactory("jpa-tests02-01").createEntityManager();

	public static void main(String[] args) {
//		creation1();
		creationAndRetrieval();
	}

	private static void creation1() {
		em.getTransaction().begin();
		
		Employee employee1 = new Employee("Empo 1");
		Employee employee2 = new Employee("Empo 2");
		Employee employee3 = new Employee("Empo 3");
		Employee employee4 = new Employee("Empo 4");
		em.persist(employee1);
		em.persist(employee2);
		em.persist(employee3);
		em.persist(employee4);
		
		Cubicle assignedCubicle1 = new Cubicle("numer1");
		Cubicle assignedCubicle2 = new Cubicle("numer2");
		Cubicle assignedCubicle3 = new Cubicle("numer3");
		Cubicle assignedCubicle4 = new Cubicle("numer4");
		employee1.assignCubicle(assignedCubicle1);
		employee2.assignCubicle(assignedCubicle2);
		employee3.assignCubicle(assignedCubicle3);
		employee4.assignCubicle(assignedCubicle4);
		
		em.persist(assignedCubicle1);
		em.persist(assignedCubicle2);
		em.persist(assignedCubicle3);
		em.persist(assignedCubicle4);
		
		em.getTransaction().commit();
	}

	private static void creationAndRetrieval() {
		creation1();
		em.clear();
		
		Query query = em.createQuery("select empo from Employee_BI_O2OP empo where empo.name = 'Empo 1'");
		Employee employee1 = (Employee) query.getSingleResult();
		
		System.out.println(employee1);
	}
}
