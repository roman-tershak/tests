package tests.hierarchies.oneperhierarchy;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import static javax.persistence.InheritanceType.*;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
@Inheritance(strategy=SINGLE_TABLE)
@DiscriminatorValue("B")
// When @DiscriminatorValue("B"), then we get:
// Hibernate: insert into BaseEntity (assocEntity_id, name, version, DTYPE, id) values (?, ?, ?, 'B', ?)
// but default could also be in apply in child entity
// Hibernate: insert into BaseEntity (assocEntity_id, name, version, addName, DTYPE, id) values (?, ?, ?, ?, 'ChildEntity', ?)
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private AssociatedEntityA assocEntity;
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
	
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne
	public AssociatedEntityA getAssocEntity() {
		return assocEntity;
	}
	public void setAssocEntity(AssociatedEntityA assocEntity) {
		this.assocEntity = assocEntity;
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
		sb.append("BaseEntity [");
		sb.append("id=").append(id).append(", ");
		sb.append("name=").append(name).append(", ");
		sb.append("assocEntity.id=").append(assocEntity != null ? assocEntity.getId() : null).append(", ");
		sb.append("version=").append(version);
		sb.append("]");
		return sb.toString();
	}
	
}
