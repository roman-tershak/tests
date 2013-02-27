package tests.jpa.entity.unidir.multival.manytomany;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="Patent_UM_M2M")
public class Patent {
	
	private Long id;
	private String about;

	public Patent() {
	}
	
	public Patent(String about) {
		this.about = about;
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	protected void setId(Long id) {
		this.id = id;
	}
	
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	@Override
	public String toString() {
		return "Patent [id=" + id + ", about=" + about + "]";
	}
}
