package tests.jpa.entity.bidir.onetoone;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="CUBICLE_BI_O2O")
public class Cubicle {

	private Long id;
	private String numer;
	private Employee residentEmployee;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNumer() {
		return numer;
	}
	public void setNumer(String numer) {
		this.numer = numer;
	}
	
	@OneToOne(mappedBy = "assignedCubicle")
	public Employee getResidentEmployee() {
		return residentEmployee;
	}
	public void setResidentEmployee(Employee employee) {
		this.residentEmployee = employee;
	}
	
	@Override
	public String toString() {
		return "Cubicle [id=" + id + ", numer=" + numer + ", residentEmployee.id="
				+ residentEmployee.getId() + "]";
	}
}
