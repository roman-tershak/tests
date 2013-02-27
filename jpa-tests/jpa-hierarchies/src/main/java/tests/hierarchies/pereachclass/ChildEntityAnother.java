package tests.hierarchies.pereachclass;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name="ChildEntityAnother_PEC")
@DiscriminatorValue("CHA")
public class ChildEntityAnother extends BaseEntity {

	private Date tm = new Date();
	
	public ChildEntityAnother() {
	}

	public ChildEntityAnother(String name) {
		super(name);
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
		sb.append("tm=").append(tm);
		sb.append("]");
		return sb.toString();
	}
	
	
}
