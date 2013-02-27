package tests.hierarchies.oneperhierarchy.detailed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity(name="AssociatedEntityA_PH_WD")
public class AssociatedEntityA {

	private Long id;
	private Integer num;
	private Long version;
	
	public AssociatedEntityA() {
	}

	public AssociatedEntityA(Integer num) {
		this.num = num;
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	protected void setId(Long id) {
		this.id = id;
	}
	
	@Column(unique=true)
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
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
		sb.append("num=").append(num).append(", ");
		sb.append("version=").append(version);
		sb.append("]");
		return sb.toString();
	}
	
	
}
