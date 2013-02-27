package tests.jpainejb.jta.simple;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity(name="PersonEntity_SIMPLE")
public class PersonEntity {

	private Long id;
	private String name;
	private Long version;
	
	public PersonEntity() {
	}

	public PersonEntity(String name) {
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
	
	@Column(unique=true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Version
	public Long getVersion() {
		return version;
	}
	protected void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PersonEntity [");
		sb.append("id=").append(id).append(", ");
		sb.append("name=").append(name).append(", ");
		sb.append("version=").append(version);
		sb.append("]");
		return sb.toString();
	}
	
	
}
