package tests.jpa.entity.unidir.singleval.manytoone;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="Address_U_S_M2O")
public class Address {

	private Long id;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Address [id=" + id + "]";
	}
}
