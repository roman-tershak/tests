package tests.jpainejb.jta.simple;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class StatelessBean implements StatelessLocal {

	@PersistenceContext(unitName="jpa-in-ejb-Oracle")
	private EntityManager em;
	
	public void doSomethingSimple() {
		System.out.println("We are here!");
	}

	public void createPerson(String name) {
		PersonEntity person = new PersonEntity(name);
		em.persist(person);
	}

}
