package tests.jpa.entity.bidir.onetoone;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="EMPLOYEE_BI_O2O")
public class Employee {

	private Long id;
	private Cubicle assignedCubicle;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne
	public Cubicle getAssignedCubicle() {
		return assignedCubicle;
	}
	public void setAssignedCubicle(Cubicle assignedCubicle) {
		this.assignedCubicle = assignedCubicle;
	}
	public void assignCubicle(Cubicle cubicle) {
		setAssignedCubicle(cubicle);
		cubicle.setResidentEmployee(this);
	}
	
	@Override
	public String toString() {
		return "Employee [id=" + id + ", assignedCubicle.id=" + assignedCubicle.getId() + "]";
	}
	
}
