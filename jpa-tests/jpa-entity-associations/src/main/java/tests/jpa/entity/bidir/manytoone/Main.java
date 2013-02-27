package tests.jpa.entity.bidir.manytoone;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {

	private static EntityManager em = Persistence.createEntityManagerFactory(
			"jpa-tests02-01").createEntityManager();

	public static void main(String[] args) {
//		test01();
//		test02();
//		test03();
//		testAddingEntityFromOwningSideWithoutReverseSideUpdates();
//		testRemoval1();
//		testRemoval2();
//		testRemoval3();
//		testRemoval4();
		testRemoval5();
	}

	private static void creation01() {
		em.getTransaction().begin();
		
		Department department = createDepartment();
		
		createEmployee(department);
		createEmployee(department);
		createEmployee(department);
		
		em.getTransaction().commit();
	}
	
	private static void test01() {
		em.getTransaction().begin();
		
		Department department = new Department();
		EmployeeM2O employee = new EmployeeM2O();
		employee.setDepartment(department);
		department.addEmployee(employee);
		
		em.persist(department);
		em.persist(employee);
		
		em.flush();
		em.getTransaction().commit();
	}

	private static void test02() {
		em.getTransaction().begin();
		
		Department department = createDepartment();
		
		createEmployee(department);
		createEmployee(department);
		createEmployee(department);
		
		Query query = em.createQuery("select dp from DepartmentM2O dp where dp.id = :id");
		query.setParameter("id", 1L);
		Department department2 = (Department) query.getSingleResult();
		System.out.println(department);
		System.out.println(department2);
		
		em.flush();
		em.getTransaction().commit();
	}

	private static void test03() {
		em.getTransaction().begin();
		
		Department department = createDepartment();
		
		createEmployee(department);
		createEmployee(department, false);
//		Without persist child entity won't be added to the database, even if it may appear
//		later in parent instance (department) even retrieved from the cache/detached
//		Department [id=1, employees=[EmployeeM2O [id=2, department.id=1], EmployeeM2O [id=null, department.id=1]]]
//		Please notice 'id=null' in the 2nd employee, it appears when em.clear() is commented out
//		createEmployee(department, true);
//		createEmployee(department);
		
		em.flush();
		em.getTransaction().commit();
		
//		em.clear();
		
		Query query = em.createQuery("select dp from DepartmentM2O dp where dp.id = :id");
		query.setParameter("id", 1L);
		System.out.println(query.getSingleResult());
	}
	
	private static void testAddingEntityFromOwningSideWithoutReverseSideUpdates() {
		em.getTransaction().begin();
		
		Department department = createDepartment();
		
		EmployeeM2O employee1 = new EmployeeM2O();
		
		// The statements below work fine, even if the reverse side don't reflect the owning side changes
//		employee1.setDepartment(department);
//		em.persist(employee1);
		
		// As this employee has not been persisted it will not appear in the department's employee collection
//		employee1.setDepartment(department);
		
		// The statement below, though incorrect, but will work only if em is not cleared,
		// although the result will be this:
		// Department [id=1, employees=[EmployeeM2O [id=null, department.id=null]]]
//		department.getEmployees().add(employee1);
		
		// Even persisted employee will not contain a link to a department, because the owning side
		// was not updated, and it only works if em was not cleared:
		// Department [id=1, employees=[EmployeeM2O [id=2, department.id=null]]]
		// If em has been cleared then we get this:
		// Department [id=1, employees=[]]
		department.getEmployees().add(employee1);
		em.persist(employee1);
		
		em.getTransaction().commit();
		em.clear();
		
		Query query = em.createQuery("select dp from DepartmentM2O dp where dp.id = :id");
		query.setParameter("id", 1L);
		System.out.println(query.getSingleResult());
	}
	
	private static void testRemoval1() {
		creation01();
		em.clear();
		em.getTransaction().begin();
		
		@SuppressWarnings("unchecked")
		List<EmployeeM2O> employees = em.createQuery("select empl from EmployeeM2O empl").getResultList();
		EmployeeM2O employee = employees.get(0);
		em.remove(employee);
		// Without flush, the removed entity (employee) will still appear in the department's 
		// employees collection
//		em.flush();
		
		Department department = (Department) em.createQuery(
				"select dp from DepartmentM2O dp where dp.id = 1").getSingleResult();
		System.out.println(department);
		
		em.getTransaction().commit();
		em.clear();
		
		System.out.println(em.createQuery("select dp from DepartmentM2O dp where dp.id = 1").getSingleResult());
	}

	private static void testRemoval2() {
		creation01();
		em.clear();
		em.getTransaction().begin();
		
		Department department = (Department) em.createQuery(
				"select dp from DepartmentM2O dp where dp.id = 1").getSingleResult();
		
		EmployeeM2O employee = ((List<EmployeeM2O>) em.createQuery(
				"select empl from EmployeeM2O empl").getResultList()).get(0);
		department.getEmployees().remove(employee);
		em.remove(employee);
		// When employee is removed from the department's employees collection and from persistent context
		// flush is not required
//		em.flush();
		
		System.out.println(department);
		
		em.getTransaction().commit();
		em.clear();
		
		System.out.println(em.createQuery("select dp from DepartmentM2O dp where dp.id = 1").getSingleResult());
	}
	
	private static void testRemoval3() {
		creation01();
		em.clear();
		em.getTransaction().begin();
		
		Department department = (Department) em.createQuery(
				"select dp from DepartmentM2O dp where dp.id = 1").getSingleResult();
		System.out.println(department);

		EmployeeM2O employee = department.getEmployees().iterator().next();
		
		// Without the statement below the removed employee will still appear in the department's 
		// employees collection
//		department.getEmployees().remove(employee);
		em.remove(employee);
		
		// Flush does not refreshes department, so department.getEmployees().remove(employee); is a must
//		em.flush();
		
		// If not doing department.getEmployees().remove(employee);
		// then flush in conjunction with refresh can help, although such approach is rather ugly
//		em.flush();
//		System.out.println("Doing refresh...");
//		em.refresh(department);
		
		System.out.println("After employee removal");
		System.out.println(department);
		
		em.getTransaction().commit();
		em.clear();
		
//		System.out.println(em.createQuery("select dp from DepartmentM2O dp where dp.id = 1").getSingleResult());
	}
	
	private static void testRemoval4() {
		creation01();
		em.clear();
		em.getTransaction().begin();
		
		Department department = (Department) em.createQuery(
				"select dp from DepartmentM2O dp where dp.id = 1").getSingleResult();
		System.out.println(department);
		
		EmployeeM2O employee = department.getEmployees().iterator().next();
		
		// Without the statement below, AND WITH cascade from department to employee equal to PERSIST or ALL:
//		department.getEmployees().remove(employee);
		// We will get this:
		// org.hibernate.ObjectDeletedException: deleted entity passed to persist:
		em.remove(employee);
		
		// Re-persist is not allowed (at least by Hibernate):
		// javax.persistence.EntityNotFoundException: deleted entity passed to persist:
		// This differs from what the JPA spec says:
		// If X is a removed entity, it becomes managed.
//		em.remove(employee);
//		em.persist(employee);
		
		department.setName("some other name");
		System.out.println(department);
		
		em.getTransaction().commit();
		em.clear();
		
		System.out.println(em.createQuery("select dp from DepartmentM2O dp where dp.id = 1").getSingleResult());
	}
	
	private static void testRemoval5() {
		creation01();
		em.clear();
		em.getTransaction().begin();
		
		Department department = (Department) em.createQuery(
				"select dp from DepartmentM2O dp where dp.id = 1").getSingleResult();
		
		List<EmployeeM2O> employees = em.createQuery("select empl from EmployeeM2O empl").getResultList();
		EmployeeM2O employee = employees.get(0);
		
		em.remove(employee);
		
		em.getTransaction().commit();
		em.clear();
	}
	
	private static Department createDepartment() {
		Department department = new Department();
		em.persist(department);
		return department;
	}

	private static void createEmployee(Department department) {
		createEmployee(department, true);
	}

	private static void createEmployee(Department department, boolean persist) {
		EmployeeM2O employee = new EmployeeM2O();
		department.addEmployee(employee);
		if (persist) {
			em.persist(employee);
		}
	}
	
}
