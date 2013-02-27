package tests.jpa.entity.bidir.manytomany;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ManyToMany;
import javax.persistence.Persistence;

public class Main {
	
	private static EntityManager em;

	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpa-tests02-01");
		em = entityManagerFactory.createEntityManager();
		
//		simple01();
//		eagerFetching();
//		mapAssociations();
//		addingRemovingAssociations();
//		revertingAssociation();
//		removingEntities();
		removingEntitiesFromInverseSideWithoutCascade();
//		removingEntitiesFromOwningSideWithoutCascade();
	}

	private static void simple01() {
		em.getTransaction().begin();
		
		Employee employee1 = new Employee("Emplo 1");
		Employee employee2 = new Employee("Emplo 2");
		Employee employee3 = new Employee("Emplo 3");
		Employee employee4 = new Employee("Emplo 4");
		Employee employee5 = new Employee("Emplo 5");
		Employee employee6 = new Employee("Emplo 6");
		Employee employee7 = new Employee("Emplo 7");
		Employee employee8 = new Employee("Emplo 8");
		em.persist(employee1);
		em.persist(employee2);
		em.persist(employee3);
		em.persist(employee4);
		em.persist(employee5);
		em.persist(employee6);
		em.persist(employee7);
		em.persist(employee8);
		
		Project project1 = new Project("test project 1", "tech 1");
		Project project2 = new Project("test project 2", "tech 2");
		Project project3 = new Project("test project 3", "tech 3");
		Project project4 = new Project("test project 4", "tech 4");
		Project project5 = new Project("test project 5", "tech 5");
		em.persist(project1);
		em.persist(project2);
		em.persist(project3);
		em.persist(project4);
		em.persist(project5);
	
		project1.addEmployee(employee1);
		project1.addEmployee(employee3);
		project1.addEmployee(employee5);
		project1.addEmployee(employee7);
		
		project2.addEmployee(employee1);
		project2.addEmployee(employee2);
		project2.addEmployee(employee6);
		
		project3.addEmployee(employee3);
		project3.addEmployee(employee4);
		project3.addEmployee(employee5);
		project3.addEmployee(employee6);
		
		project4.addEmployee(employee1);
		project4.addEmployee(employee5);
		project4.addEmployee(employee7);
		
		project5.addEmployee(employee5);
		project5.addEmployee(employee7);
		project5.addEmployee(employee8);
		
		em.getTransaction().commit();
	}

	private static void eagerFetching() {
		simple01();
		em.clear();
		
		System.out.println("Before the 2nd stage.");
		em.getTransaction().begin();
		
//		With EAGER fetching we will get this:
//		Before the 2nd stage.
//		Hibernate: select project0_.id as id9_, project0_.name as name9_ from Project_BI_M2M project0_
//		Hibernate: select employees0_.projects_id as projects1_1_, employees0_.employees_id as employees2_1_, employee1_.id as id8_0_, employee1_.name as name8_0_ from Project_BI_M2M_Employee_BI_M2M employees0_ left outer join Employee_BI_M2M employee1_ on employees0_.employees_id=employee1_.id where employees0_.projects_id=?
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
//		Hibernate: select employees0_.projects_id as projects1_1_, employees0_.employees_id as employees2_1_, employee1_.id as id8_0_, employee1_.name as name8_0_ from Project_BI_M2M_Employee_BI_M2M employees0_ left outer join Employee_BI_M2M employee1_ on employees0_.employees_id=employee1_.id where employees0_.projects_id=?
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
		
//		With LAZY fetching:
//		Before the 2nd stage.
//		Hibernate: select project0_.id as id9_, project0_.name as name9_ from Project_BI_M2M project0_

//		With EAGER fetching from the opposite side (from Employee, the inverse side)
//		Before the 2nd stage.
//		Hibernate: select employee0_.id as id8_, employee0_.name as name8_ from Employee_BI_M2M employee0_
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
//		Before System.out.println
//		[Employee [id=1, name=null, projects=[4, 5], Employee [id=2, name=null, projects=[5], Employee [id=3, name=null, projects=[4]]		@SuppressWarnings("unchecked")

//		With LAZY fetching from the opposite side (from Employee, the inverse side)
//		Before the 2nd stage.
//		Hibernate: select employee0_.id as id8_, employee0_.name as name8_ from Employee_BI_M2M employee0_
//		Before System.out.println
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
//		Hibernate: select projects0_.employees_id as employees2_1_, projects0_.projects_id as projects1_1_, project1_.id as id9_0_, project1_.name as name9_0_ from Project_BI_M2M_Employee_BI_M2M projects0_ left outer join Project_BI_M2M project1_ on projects0_.projects_id=project1_.id where projects0_.employees_id=?
//		[Employee [id=1, name=null, projects=[4, 5], Employee [id=2, name=null, projects=[5], Employee [id=3, name=null, projects=[4]]
		List<Employee> employees = em.createQuery("select empl from  Employee_BI_M2M empl").getResultList();
		System.out.println("Before System.out.println");
		System.out.println(employees);
		
		em.getTransaction().commit();
	}
	
	private static void mapAssociations() {
		simple01();
		em.clear();
		
		System.out.println("Before the 2nd stage.");
		em.getTransaction().begin();
		
		Employee employee = (Employee) em.createQuery("select empl from  Employee_BI_M2M empl where empl.name='Emplo 1'").getSingleResult();
		
		System.out.println("Before employee.getProjects()");
		System.out.println(employee.getProjects());
		System.out.println("Before employee.getProjectsMap()");
//		System.out.println(employee.getProjectsMap());
		
		em.getTransaction().commit();
	}
	
	private static void addingRemovingAssociations() {
		simple01();
		em.clear();
		
		System.out.println("Before the 2nd stage.");
		em.getTransaction().begin();
		
		Employee employee = (Employee) em.createQuery("select empl from  Employee_BI_M2M empl where empl.name='Emplo 1'").getSingleResult();
		
		System.out.println("Before adding/removing");
		
//		Adding / removing on the inverse side has no effect, unless doing the same on owning side
		Set<Project> projects = employee.getProjects();
		Project project = projects.iterator().next();
		System.out.println(project);
//		projects.remove(project);
//		employee.setProjects(projects);
//		
//		Map<String, Project> projectsMap = employee.getProjectsMap();
//		projectsMap.remove(project.getName());
//		employee.setProjectsMap(projectsMap);
		
//		Even without related updates on the inverse side, 
//		changes on the owning side DO HAVE effect
//		Set<Project> projects = employee.getProjects();
//		Project project = projects.iterator().next();
//		projects.remove(project);
//		
//		Map<String, Project> projectsMap = employee.getProjectsMap();
//		projectsMap.remove(project.getName());
		
		project.getEmployees().remove(employee);
		
		em.getTransaction().commit();
		em.clear();
		
		System.out.println("Before the 3rd stage.");
		
		employee = (Employee) em.createQuery("select empl from  Employee_BI_M2M empl where empl.name='Emplo 1'").getSingleResult();
		System.out.println(employee);
	}
	
	private static void revertingAssociation() {
		simple01();
		em.clear();
		
		System.out.println("Before the 2nd stage.");
//		em.getTransaction().begin();
		
		Employee employee = (Employee) em.createQuery("select empl from  Employee_BI_M2M empl where empl.name='Emplo 1'").getSingleResult();
		System.err.println(employee.getProjects());
//		System.err.println(employee.getProjectsMap());
		
		em.getTransaction().begin();
		
		Project project = new Project("pro add", "techno");
		em.persist(project);

//		Having DUPLICATED BOTH OWNING sided link with updates to both of them:
//			employee.getProjectsMap().put(project.getName(), project);
//			employee.getProjects().add(project);
//		will lead to this:
//		Hibernate: insert into projects_employees (employee_id, project_id) values (?, ?)
//		Hibernate: insert into projects_employees (employee_id, project_id) values (?, ?)
//		1966 [main] WARN org.hibernate.util.JDBCExceptionReporter - SQL Error: 1, SQLState: 23000
//		1966 [main] ERROR org.hibernate.util.JDBCExceptionReporter - ORA-00001: unique constraint (TESTS.SYS_C0094555) violated
//		employee.getProjectsMap().put(project.getName(), project);
		employee.getProjects().add(project);
		
		em.getTransaction().commit();
	}
	
	private static void removingEntities() {
		simple01();
		em.clear();
		
		System.out.println("Before the 2nd stage.");
		em.getTransaction().begin();

//		With cascade=remove, i.e.
//		@ManyToMany(mappedBy = "employees", cascade={CascadeType.REMOVE})
//		it's possible to remove the opposite entities (with links, of course)
//		Before the 2nd stage.
//		Hibernate: select * from ( select employee0_.id as id8_, employee0_.name as name8_ from Employee_BI_M2M employee0_ where employee0_.name='Emplo 1' ) where rownum <= ?
//		Hibernate: select projects0_.employee_id as employee2_1_, projects0_.project_id as project1_1_, project1_.id as id9_0_, project1_.name as name9_0_, project1_.techno as techno9_0_ from projects_employees projects0_ left outer join Project_BI_M2M project1_ on projects0_.project_id=project1_.id where projects0_.employee_id=?
//		Hibernate: delete from projects_employees where project_id=?
//		Hibernate: delete from projects_employees where project_id=?
//		Hibernate: delete from projects_employees where project_id=?
//		Hibernate: delete from Project_BI_M2M where id=?
//		Hibernate: delete from Project_BI_M2M where id=?
//		Hibernate: delete from Project_BI_M2M where id=?
//		Hibernate: delete from Employee_BI_M2M where id=?
		
//		Without cascade=remove, we will get this:
//		Hibernate: delete from Employee_BI_M2M where id=?
//		2799 [main] WARN org.hibernate.util.JDBCExceptionReporter - SQL Error: 2292, SQLState: 23000
//		2799 [main] ERROR org.hibernate.util.JDBCExceptionReporter - ORA-02292: integrity constraint (TESTS.FKEB73320018E830FC) violated - child record found
		
		Employee employee = (Employee) em.createQuery("select empl from  Employee_BI_M2M empl where empl.name='Emplo 1'").getSingleResult();
		em.remove(employee);
		
		em.getTransaction().commit();
	}
	
	private static void removingEntitiesFromInverseSideWithoutCascade() {
		simple01();
		em.clear();
		
		System.out.println("Before the 2nd stage.");
		em.getTransaction().begin();
		
		Employee employee = (Employee) em.createQuery("select empl from  Employee_BI_M2M empl where empl.name='Emplo 1'").getSingleResult();
		
		employee.removeFromAllProjects();
		em.remove(employee);
		
		em.getTransaction().commit();
	}
	
	private static void removingEntitiesFromOwningSideWithoutCascade() {
		simple01();
		em.clear();
		
		System.out.println("Before the 2nd stage.");
		em.getTransaction().begin();
		
		Project project = (Project) em.createQuery("select pro from  Project_BI_M2M pro where pro.name='test project 1'").getSingleResult();
		
		em.remove(project);
		
		em.getTransaction().commit();
	}
}
