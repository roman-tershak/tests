package tests.hierarchies.oneperhierarchy;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class AssociatedEntityA implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private Long version;
	
	public AssociatedEntityA() {
	}

	public AssociatedEntityA(String name) {
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
		sb.append("AssociatedEntityA [");
		sb.append("id=").append(id).append(", ");
		sb.append("name=").append(name).append(", ");
		sb.append("version=").append(version);
		sb.append("]");
		return sb.toString();
	}
}
