package tests.entity.property.access;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/* 
 * From JPA Specification : 
 * "The behavior is unspecified if mapping annotations are applied to both persistent fields and 
 * properties or if the XML descriptor specifies use of different access types within a class hierarchy."
 * OpenJPA, however allows mixing of access types, when Hibernate just takes the first ones.
 */
@Entity
@Access(AccessType.FIELD)
public class Employee {

	@Id
	private Long id;
	
	private String name;
	
//	@Transient
	private String transientName;
	
	private transient String surname;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Basic
	@Access(AccessType.PROPERTY)
	public String getTransientName() {
		return transientName;
	}
	public void setTransientName(String transientName) {
		this.transientName = transientName;
	}
	
	@Basic
	@Access(AccessType.PROPERTY)
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
}
