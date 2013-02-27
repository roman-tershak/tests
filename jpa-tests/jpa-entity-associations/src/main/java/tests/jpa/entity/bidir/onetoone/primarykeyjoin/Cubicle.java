package tests.jpa.entity.bidir.onetoone.primarykeyjoin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity(name="Cubicle_BI_O2OP")
public class Cubicle {

	private Long id;
	private String numer;
	private Employee residentEmployee;

	public Cubicle() {
	}
	
	public Cubicle(String numer) {
		this.numer = numer;
	}
	
	@Id
//	@GeneratedValue
	public Long getId() {
		return id;
	}
	protected void setId(Long id) {
		this.id = id;
	}
	
	public String getNumer() {
		return numer;
	}
	public void setNumer(String numer) {
		this.numer = numer;
	}
	
	@OneToOne
	@PrimaryKeyJoinColumn(name="ID", referencedColumnName="ID")
	public Employee getResidentEmployee() {
		return residentEmployee;
	}
	public void setResidentEmployee(Employee employee) {
		this.residentEmployee = employee;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Cubicle [id=").append(id).append(", numer=").append(numer);
		sb.append(", residentEmployee.id=");
		sb.append(residentEmployee == null ? null : residentEmployee.getId());
		sb.append("]");
		return sb.toString();
	}
}
