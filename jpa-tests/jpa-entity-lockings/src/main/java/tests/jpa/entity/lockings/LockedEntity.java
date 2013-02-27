package tests.jpa.entity.lockings;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

@Entity
public class LockedEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;

	public LockedEntity() {
	}

	public LockedEntity(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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
