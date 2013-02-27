package tests.hierarchies.oneperhierarchy.detailed;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name="ChildEntityAnother_PH_WD")
@DiscriminatorValue("CHA")
public class ChildEntityAnother extends BaseEntity {

	private Date tm = new Date();
	private String secName;
	
	public ChildEntityAnother() {
	}

	public ChildEntityAnother(String name) {
		super(name);
	}

	@Column(unique=true)
	public String getSecName() {
		return secName;
	}
	public void setSecName(String secName) {
		this.secName = secName;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getTm() {
		return tm;
	}
	public void setTm(Date tm) {
		this.tm = tm;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChildEntityAnother [");
		sb.append(super.toString()).append(", ");
		sb.append("secName=").append(secName).append(", ");
		sb.append("tm=").append(tm);
		sb.append("]");
		return sb.toString();
	}
	
	
}
