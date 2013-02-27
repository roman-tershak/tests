package tests.hierarchies.joined;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name="ChildEntityJoined")
public class ChildEntity extends BaseEntity {

	private String secName;
	
	public ChildEntity() {
	}

	public ChildEntity(String name, String secName) {
		super(name);
		this.secName = secName;
	}

	@Column(unique=true)
	public String getSecName() {
		return secName;
	}
	public void setSecName(String secName) {
		this.secName = secName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChildEntity [");
		sb.append(super.toString()).append(", ");
		sb.append("secName=").append(secName);
		sb.append("]");
		return sb.toString();
	}
	
	
}
