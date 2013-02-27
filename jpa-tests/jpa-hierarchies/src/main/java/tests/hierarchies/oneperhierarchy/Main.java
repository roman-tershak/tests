package tests.hierarchies.oneperhierarchy;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class Main {
	
	private static EntityManager em = Persistence.createEntityManagerFactory("jpa-hierarchies-01").createEntityManager();
	
	public static void main(String[] args) {
//		creation1();
//		creationChild1();
		polymorphicQuery();
	}

	// When default discriminator is used, then we get this:
	// Hibernate: insert into BaseEntity (assocEntity_id, name, version, DTYPE, id) values (?, ?, ?, 'BaseEntity', ?)
	// Hibernate: insert into BaseEntity (assocEntity_id, name, version, DTYPE, id) values (?, ?, ?, 'BaseEntity', ?)
	// ...
	// Hibernate: insert into BaseEntity (assocEntity_id, name, version, addName, DTYPE, id) values (?, ?, ?, ?, 'ChildEntity', ?)
	
	// When @DiscriminatorValue("B") public class BaseEntity
	// and @DiscriminatorValue("C") public class ChildEntity
	// then we get this:
	// Hibernate: insert into BaseEntity (assocEntity_id, name, version, DTYPE, id) values (?, ?, ?, 'B', ?)
	// Hibernate: insert into BaseEntity (assocEntity_id, name, version, DTYPE, id) values (?, ?, ?, 'B', ?)
	// ...
	// Hibernate: insert into BaseEntity (assocEntity_id, name, version, addName, DTYPE, id) values (?, ?, ?, ?, 'C', ?)
	
	
	private static void creation1() {
		em.getTransaction().begin();
		
		AssociatedEntityA associatedEntityA = new AssociatedEntityA("assoc entity 1");
		em.persist(associatedEntityA);
		
		BaseEntity baseEntity1 = new BaseEntity("base entity 1");
		baseEntity1.setAssocEntity(associatedEntityA);
		em.persist(baseEntity1);
		
		BaseEntity baseEntity2 = new BaseEntity("base entity 2");
		baseEntity2.setAssocEntity(associatedEntityA);
		em.persist(baseEntity2);
		
		em.getTransaction().commit();
	}

	private static void creationChild1() {
		creation1();
		em.getTransaction().begin();

		AssociatedEntityA assocEntity = em.find(AssociatedEntityA.class, 1L);
		
		ChildEntity childEntity1 = new ChildEntity("child entiry 1");
		childEntity1.setAssocEntity(assocEntity);
		childEntity1.setAddName("addName some 1");
		em.persist(childEntity1);
		
		em.getTransaction().commit();
	}
	
	private static void polymorphicQuery() {
		creationChild1();
		
		@SuppressWarnings("unchecked")
		List<BaseEntity> list = em.createQuery("select be from BaseEntity be").getResultList();
		
		System.out.println(list);
	}
}
