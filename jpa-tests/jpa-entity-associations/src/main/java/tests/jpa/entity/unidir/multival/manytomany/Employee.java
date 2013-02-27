package tests.jpa.entity.unidir.multival.manytomany;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name="Employee_UM_M2M")
public class Employee {
	
	private Long id;
	private String name;
	private Collection<Patent> patents = new HashSet<Patent>();

	public Employee() {
	}
	
	public Employee(String name) {
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
	
	@Basic
	@Column(nullable=false, updatable=false, unique=true)
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	
	@ManyToMany
	public Collection<Patent> getPatents() {
		return patents;
	}
	protected void setPatents(Collection<Patent> patents) {
		this.patents = patents;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Employee [id=").append(id).append(", name=").append(name).append(", patent.ids=");
		Set<Long> patentsIds = new HashSet<Long>();
		for (Patent patent : getPatents()) {
			patentsIds.add(patent.getId());
		}
		sb.append(patentsIds);
		sb.append("]");
		return sb.toString();
	}
}
