package tests.jpa.entity.lockings.versions;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

@Entity
public class LockedVersionedEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private Long version;

	public LockedVersionedEntity() {
	}

	public LockedVersionedEntity(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return this.id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
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
		StringBuilder builder = new StringBuilder();
		builder.append("LockedEntity [id=").append(id).append(", ");
		builder.append("name=").append(name);
		builder.append("]");
		return builder.toString();
	}

}
