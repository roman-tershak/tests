package tests.jpa.entity.bidir.manytomany;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.UniqueConstraint;

@Entity(name="Project_BI_M2M")
public class Project {
	
	private Long id;
	private String name;
	private String techno;
//	Without the code below we will get this:
//	Exception in thread "main" java.lang.NullPointerException
//	at tests.jpa.entity.bidir.manytomany.Project.addEmployee(Project.java:38)
//	at tests.jpa.entity.bidir.manytomany.Main.simple01(Main.java:33)
//	at tests.jpa.entity.bidir.manytomany.Main.main(Main.java:15)
	private Collection<Employee> employees = new HashSet<Employee>();

	public Project() {
	}
	
	public Project(String name) {
		this(name, null);
	}
	
	public Project(String name, String techno) {
		this.name = name;
		this.techno = techno;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
//	@Basic(optional=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTechno() {
		return techno;
	}
	public void setTechno(String techno) {
		this.techno = techno;
	}
	
//	@ManyToMany(mappedBy="projects")
	@ManyToMany
	@JoinTable(
			name="projects_employees",
			joinColumns=@JoinColumn(name="project_id", nullable=true),
			inverseJoinColumns=@JoinColumn(name="employee_id", nullable=true)/*,
			uniqueConstraints=@UniqueConstraint(columnNames={"project_id", "employee_id"})*/
			)
	public Collection<Employee> getEmployees() {
		return employees;
	}
	protected void setEmployees(Collection<Employee> employees) {
		this.employees = employees;
	}
	public void addEmployee(Employee employee) {
		employees.add(employee);
		employee.getProjects().add(this);
//		employee.getProjectsMap().put(getName(), this);
	}
	public void removeEmployee(Employee employee) {
		employees.remove(employee);
		employee.getProjects().remove(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Project [id=").append(id).append(", name=").append(name);
		Set<Long> employeeIds = new HashSet<Long>();
		for (Employee employee : getEmployees()) {
			employeeIds.add(employee.getId());
		}
		sb.append(", employees=").append(employeeIds);
		return sb.toString();
	}
}