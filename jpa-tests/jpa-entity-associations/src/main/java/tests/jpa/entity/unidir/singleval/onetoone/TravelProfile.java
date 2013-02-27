package tests.jpa.entity.unidir.singleval.onetoone;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="TravelProfile_U_S_O2O")
public class TravelProfile {
	
	private Long id;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
