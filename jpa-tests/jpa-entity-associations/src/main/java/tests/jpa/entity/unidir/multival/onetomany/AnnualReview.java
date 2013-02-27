package tests.jpa.entity.unidir.multival.onetomany;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name="AnnualReview_UM_O2M")
public class AnnualReview {

	private Long id;
	private String name;
	private Date date;
	
	public AnnualReview() {
		this(null);
	}
	
	public AnnualReview(String name) {
		this.name = name;
		this.date = new Date();
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
	
	@Temporal(TemporalType.DATE)
	@Column(name="date_")
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "AnnualReview [id=" + id + ", date=" + date + "]";
	}
}
