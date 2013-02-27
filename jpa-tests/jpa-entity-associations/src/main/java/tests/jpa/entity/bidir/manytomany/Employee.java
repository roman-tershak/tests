package tests.jpa.entity.bidir.manytomany;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.UniqueConstraint;

@Entity(name="Employee_BI_M2M")
public class Employee {
	
	private Long id;
	private String name;
//	Without the code below we will get this:
//	Exception in thread "main" java.lang.NullPointerException
//	at tests.jpa.entity.bidir.manytomany.Project.addEmployee(Project.java:43)
//	at tests.jpa.entity.bidir.manytomany.Main.simple01(Main.java:33)
//	at tests.jpa.entity.bidir.manytomany.Main.main(Main.java:15)
	private Set<Project> projects = new HashSet<Project>();
	private Map<String, Project> projectsMap = new HashMap<String, Project>();

//	Hibernate: insert into Project_BI_M2M_Employee_BI_M2M (projects_id, employees_id) values (?, ?)
//	Hibernate: insert into Project_BI_M2M_Employee_BI_M2M (projects_id, employees_id) values (?, ?)
//	Hibernate: insert into Project_BI_M2M_Employee_BI_M2M (projects_id, employees_id) values (?, ?)
//	Before the 2nd stage.
//	Hibernate: select employee0_.id as id8_, employee0_.name as name8_ from Employee_BI_M2M employee0_
//	Exception in thread "main" javax.persistence.PersistenceException: org.hibernate.InstantiationException: No default constructor for entity: tests.jpa.entity.bidir.manytomany.Employee
	public Employee() {
	}
	
	public Employee(String name) {
		this.name = name;
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
//	@ManyToMany
//	@JoinTable(
//			name="projects_employees",
//			joinColumns=@JoinColumn(name="employee_id"),
//			inverseJoinColumns=@JoinColumn(name="project_id")
//			uniqueConstraints=@UniqueConstraint(columnNames={"project_id", "employee_id"})
//			)
	@ManyToMany(mappedBy = "employees")
	public Set<Project> getProjects() {
		return projects;
	}
	protected void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	public void removeFromAllProjects() {
		for (Project project : projects) {
			project.getEmployees().remove(this);
		}
		projects.clear();
	}
	
//	@ManyToMany
//	@JoinTable(
//			name="projects_employees",
//			joinColumns=@JoinColumn(name="employee_id"),
//			inverseJoinColumns=@JoinColumn(name="project_id")
//			uniqueConstraints=@UniqueConstraint(columnNames={"project_id", "employee_id"})
//			)
//	@ManyToMany(mappedBy="employees", cascade={CascadeType.REMOVE})
//	@MapKey(name="name")
//	public Map<String, Project> getProjectsMap() {
//		return projectsMap;
//	}
//	public void setProjectsMap(Map<String, Project> projectsMap) {
//		this.projectsMap = projectsMap;
//	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Employee [id=").append(id).append(", name=").append(name);
		Set<Long> projectIds = new HashSet<Long>();
		for (Project project : getProjects()) {
			projectIds.add(project.getId());
		}
		sb.append(", projects=").append(projectIds);
		return sb.toString();
	}
}
