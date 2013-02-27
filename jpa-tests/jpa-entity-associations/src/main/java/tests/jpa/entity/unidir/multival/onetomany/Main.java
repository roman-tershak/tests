package tests.jpa.entity.unidir.multival.onetomany;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Main {

	private static EntityManager em = Persistence.createEntityManagerFactory("jpa-tests02-01").createEntityManager();

	public static void main(String[] args) {
//		creation01();
		removingAssociatedEntities();
	}

	private static void creation01() {
		em.getTransaction().begin();
		
		Employee employee1 = new Employee("Emplo1");
		Employee employee2 = new Employee("Emplo2");
		em.persist(employee1);
		em.persist(employee2);
		
		AnnualReview annualReview1 = new AnnualReview("AR 1");
		AnnualReview annualReview2 = new AnnualReview("AR 2");
		AnnualReview annualReview3 = new AnnualReview("AR 3");
		AnnualReview annualReview4 = new AnnualReview("AR 4");
		AnnualReview annualReview5 = new AnnualReview("AR 5");
		AnnualReview annualReview6 = new AnnualReview("AR 6");
		em.persist(annualReview1);
		em.persist(annualReview2);
		em.persist(annualReview3);
		em.persist(annualReview4);
		em.persist(annualReview5);
		em.persist(annualReview6);
		
		employee1.addAnnualReview(annualReview1);
		employee1.addAnnualReview(annualReview2);
		employee1.addAnnualReview(annualReview3);
		employee1.addAnnualReview(annualReview4);
		
		employee2.addAnnualReview(annualReview5);
		employee2.addAnnualReview(annualReview6);
		
		em.getTransaction().commit();
	}

	private static void removingAssociatedEntities() {
		creation01();
		em.clear();
		
		em.getTransaction().begin();
		
		Employee employee = (Employee) em.createQuery(
				"select empl from Employee_UM_O2M empl where empl.name = 'Emplo1'").getSingleResult();
		
		AnnualReview annualReview = employee.getAnnualReviews().iterator().next();
		
//		Without the code right below:
		employee.removeAnnualReview(annualReview);
//		we will get this:
//		Hibernate: select * from ( select employee0_.id as id10_, employee0_.name as name10_ from Employee_UM_O2M employee0_ where employee0_.name='Emplo1' ) where rownum <= ?
//		Hibernate: select annualrevi0_.Employee_UM_O2M_id as Employee1_1_, annualrevi0_.annualReviews_id as annualRe2_1_, annualrevi1_.id as id11_0_, annualrevi1_.date_ as date2_11_0_, annualrevi1_.name as name11_0_ from employee_annualreview annualrevi0_ left outer join AnnualReview_UM_O2M annualrevi1_ on annualrevi0_.annualReviews_id=annualrevi1_.id where annualrevi0_.Employee_UM_O2M_id=?
//		Hibernate: delete from AnnualReview_UM_O2M where id=?
//		5532 [main] WARN org.hibernate.util.JDBCExceptionReporter - SQL Error: 2292, SQLState: 23000
//		5532 [main] ERROR org.hibernate.util.JDBCExceptionReporter - ORA-02292: integrity constraint (TESTS.FK3FCD13E8C62E3D23) violated - child record found
		em.remove(annualReview);
		
		em.getTransaction().commit();
		em.clear();
		
		System.out.println(em.createQuery(
				"select empl from Employee_UM_O2M empl where empl.name = 'Emplo1'")
				.getSingleResult());
	}
}
