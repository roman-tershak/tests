package tests.hierarchies.oneperhierarchy.detailed;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="ChildChildEntity_PH_WD")
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

	// Caused by: org.hibernate.MappingException: Repeated column in mapping for entity: tests.hierarchies.oneperhierarchy.detailed.ChildChildEntity column: secName (should be mapped with insert="false" update="false")
	@Column(unique=true, insertable=false, updatable=false)
	@Override
	public String getSecName() {
		return super.getSecName();
	}
	@Override
	public void setSecName(String secName) {
		super.setSecName(secName);
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
