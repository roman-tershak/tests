package tests.jpa.entity.bidir.manytoone;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name="DepartmentM2O")
@Table(name="DEPARTMENT_M2O")
public class Department {

	private Long id;
	private String name;
	private Collection<EmployeeM2O> employees = new HashSet<EmployeeM2O>();

	@Id
	@GeneratedValue	// uses AUTO by default
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Basic
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany(mappedBy = "department")
	public Collection<EmployeeM2O> getEmployees() {
		return employees;
	}
	public void setEmployees(Collection<EmployeeM2O> employees) {
		this.employees = employees;
	}
	public void addEmployee(EmployeeM2O employee) {
		getEmployees().add(employee);
		employee.setDepartment(this);
	}
	
	@Override
	public String toString() {
		return "Department [id=" + id + ", name=" + name + ", employees=" + getEmployees() + "]";
	}
}
