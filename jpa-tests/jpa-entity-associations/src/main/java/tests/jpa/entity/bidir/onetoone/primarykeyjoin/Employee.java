package tests.jpa.entity.bidir.onetoone.primarykeyjoin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity(name="Employee_BI_O2OP")
public class Employee {

	private Long id;
	private String name;
	private Cubicle assignedCubicle;
	
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
	protected void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	
	@OneToOne(mappedBy="residentEmployee")
	public Cubicle getAssignedCubicle() {
		return assignedCubicle;
	}
	protected void setAssignedCubicle(Cubicle assignedCubicle) {
		this.assignedCubicle = assignedCubicle;
	}
	public void assignCubicle(Cubicle cubicle) {
		setAssignedCubicle(cubicle);
		cubicle.setResidentEmployee(this);
		cubicle.setId(getId());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Employee [id=").append(id).append(", name=").append(name);
		sb.append(", assignedCubicle.id=").append(assignedCubicle == null ? null : assignedCubicle.getId());
		sb.append("]");
		return sb.toString();
	}
	
}
