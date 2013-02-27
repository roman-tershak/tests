package tests.hierarchies.pereachclass;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name="ChildEntity_PEC")
@DiscriminatorValue("CH")
public class ChildEntity extends BaseEntity {

	private String secName;
	
	public ChildEntity() {
	}

	public ChildEntity(String name, String secName) {
		super(name);
		this.secName = secName;
	}

	@Column(unique=true)
	// @Column(unique=true, nullable=false)
	// Caused by: java.sql.BatchUpdateException: ORA-01400: cannot insert NULL into ("TESTS2"."BASEENTITY_J_WD"."SECNAME")
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
