package tests.hierarchies.joined;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity(name="BaseEntityJoined")
@Inheritance(strategy=InheritanceType.JOINED)
public class BaseEntity {

	private Long id;
	private String name;
	private AssociatedEntityA assocEntityA;
	private Long version;
	
	public BaseEntity() {
	}

	public BaseEntity(String name) {
		this.name = name;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne(optional=false)
	public AssociatedEntityA getAssocEntityA() {
		return assocEntityA;
	}
	public void setAssocEntityA(AssociatedEntityA assocEntityA) {
		this.assocEntityA = assocEntityA;
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
		builder.append("BaseEntity [");
		builder.append("id=").append(id).append(", ");
		builder.append("name=").append(name).append(", ");
		builder.append("assocEntityA.id=").append(assocEntityA != null ? getAssocEntityA().getId() : null).append(", ");
		builder.append("version=").append(version);
		builder.append("]");
		return builder.toString();
	}
	
	
}
