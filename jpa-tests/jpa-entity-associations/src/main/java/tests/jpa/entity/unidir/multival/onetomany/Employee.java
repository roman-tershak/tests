package tests.jpa.entity.unidir.multival.onetomany;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity(name="Employee_UM_O2M")
public class Employee {
	
	private Long id;
	private String name;
	private Collection<AnnualReview> annualReviews = new HashSet<AnnualReview>();

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
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinTable(name="employee_annualreview")
	public Collection<AnnualReview> getAnnualReviews() {
		return Collections.unmodifiableCollection(annualReviews);
	}
	// Private works!
	private void setAnnualReviews(Collection<AnnualReview> annualReviews) {
		this.annualReviews = annualReviews;
	}
	public void addAnnualReview(AnnualReview annualReview) {
		this.annualReviews.add(annualReview);
	}
	public void removeAnnualReview(AnnualReview annualReview) {
		this.annualReviews.remove(annualReview);
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", annualReviews="
				+ annualReviews + "]";
	}
}
