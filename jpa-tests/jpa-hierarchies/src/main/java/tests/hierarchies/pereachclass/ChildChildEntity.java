package tests.hierarchies.pereachclass;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="ChildChildEntity_PEC")
@DiscriminatorValue("CCH")
public class ChildChildEntity extends ChildEntity {

	private String data;
	
	public ChildChildEntity() {
	}

	public ChildChildEntity(String name, String secName) {
		super(name, secName);
	}

	public ChildChildEntity(String name, String secName, String data) {
		super(name, secName);
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChildChildEntity [");
		sb.append(super.toString()).append(", ");
		sb.append("data=").append(data);
		sb.append("]");
		return sb.toString();
	}
	
	
}
