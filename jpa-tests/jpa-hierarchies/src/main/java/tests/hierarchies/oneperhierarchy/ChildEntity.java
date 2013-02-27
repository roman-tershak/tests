package tests.hierarchies.oneperhierarchy;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("C")
public class ChildEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String addName;
	
	public ChildEntity() {
	}

	public ChildEntity(String name) {
		super(name);
	}

	public String getAddName() {
		return addName;
	}
	public void setAddName(String addName) {
		this.addName = addName;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChildEntity [");
		sb.append("id=").append(getId()).append(", ");
		sb.append("name=").append(getName()).append(", ");
		sb.append("assocEntity.id=").append(getAssocEntity() != null ? getAssocEntity().getId() : null).append(", ");
		sb.append("addName=").append(addName).append(", ");
		sb.append("version=").append(getVersion());
		sb.append("]");
		return sb.toString();
	}
	
	
}
