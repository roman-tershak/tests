package tests.jpa.entity.unidir.singleval.onetoone;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity(name="Employee_U_S_O2O")
public class Employee {

	private Long id;
	private TravelProfile profile;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne
	public TravelProfile getProfile() {
		return profile;
	}
	public void setProfile(TravelProfile profile) {
		this.profile = profile;
	}
}