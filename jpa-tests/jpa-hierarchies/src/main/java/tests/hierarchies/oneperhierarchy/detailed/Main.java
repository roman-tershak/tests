package tests.hierarchies.oneperhierarchy.detailed;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {

	private static EntityManager em = Persistence.createEntityManagerFactory("jpa-hierarchies-01").createEntityManager();
	
	public static void main(String[] args) {
//		creation1();
//		creationChildren1();
//		creationChildren2();
		fetchingEntities();
//		removingEntities();
//		removingEntities2();
//		updatingEntities1();
	}

	private static void creation1() {
		em.getTransaction().begin();
		
		AssociatedEntityA assocEntityA1 = new AssociatedEntityA(1);
		em.persist(assocEntityA1);
		AssociatedEntityA assocEntityA2 = new AssociatedEntityA(2);
		em.persist(assocEntityA2);
		
		BaseEntity baseEntity1 = new BaseEntity("base entity 1");
		baseEntity1.setAssocEntityA(assocEntityA1);
		em.persist(baseEntity1);
		BaseEntity baseEntity2 = new BaseEntity("base entity 2");
		baseEntity2.setAssocEntityA(assocEntityA1);
		em.persist(baseEntity2);
		BaseEntity baseEntity3 = new BaseEntity("base entity 3");
		baseEntity3.setAssocEntityA(assocEntityA2);
		em.persist(baseEntity3);
		
		em.getTransaction().commit();
	}
	
	@SuppressWarnings("unchecked")
	private static List<AssociatedEntityA> getAssocEntities() {
		return em.createQuery("select assocs from AssociatedEntityA_PH_WD assocs").getResultList();
	}

	private static void creationChildren1() {
		creation1();
		
		List<AssociatedEntityA> assocEntities = getAssocEntities();
		
		em.getTransaction().begin();
		
		ChildEntity childEntity1 = new ChildEntity("child entity 1", "sec name 1");
		childEntity1.setAssocEntityA(assocEntities.get(0));
		em.persist(childEntity1);
		ChildEntity childEntity2 = new ChildEntity("child entity 2", "sec name 2");
		childEntity2.setAssocEntityA(assocEntities.get(0));
		em.persist(childEntity2);
		ChildEntity childEntity3 = new ChildEntity("child entity 3", "sec name 3");
		childEntity3.setAssocEntityA(assocEntities.get(1));
		em.persist(childEntity3);
		
		ChildEntityAnother childEntityAnother1 = new ChildEntityAnother("child entity another 1");
		childEntityAnother1.setAssocEntityA(assocEntities.get(0));
		em.persist(childEntityAnother1);
		ChildEntityAnother childEntityAnother2 = new ChildEntityAnother("child entity another 2");
		childEntityAnother2.setAssocEntityA(assocEntities.get(1));
		em.persist(childEntityAnother2);
		
		em.getTransaction().commit();
	}
	
	private static void creationChildren2() {
		creationChildren1();
		
		List<AssociatedEntityA> assocEntities = getAssocEntities();
		
		em.getTransaction().begin();
		
		ChildChildEntity childChildEntity1 = new ChildChildEntity("Child Child 1", "sn 1", "data 1");
		childChildEntity1.setAssocEntityA(assocEntities.get(0));
		/*
		 */
		em.persist(childChildEntity1);
		ChildChildEntity childChildEntity2 = new ChildChildEntity("Child Child 2", "sn 2", "data 2");
		childChildEntity2.setAssocEntityA(assocEntities.get(1));
		em.persist(childChildEntity2);
		ChildChildEntity childChildEntity3 = new ChildChildEntity("Child Child 3", "sn 3", "data 3");
		childChildEntity3.setAssocEntityA(assocEntities.get(1));
		em.persist(childChildEntity3);
		
		em.getTransaction().commit();
	}
	
	private static void fetchingEntities() {
		creationChildren2();
		
		System.out.println("Fetching base entities");
		/* Hibernate: 
		 * select 
		 * 	baseentity0_.id as id8_, baseentity0_.assocEntityA_id as assocEnt8_8_, 
		 * 	baseentity0_.name as name8_, baseentity0_.version as version8_, 
		 * 	baseentity0_.secName as secName8_, baseentity0_.data as data8_, baseentity0_.tm as tm8_, 
		 * 	baseentity0_.DISC_TYPE as DISC1_8_ 
		 * from BaseEntity_PH_WD baseentity0_ 
		 * where baseentity0_.name in ('base entity 1' , 'base entity 2')
		 */
		Query baseQuery = em.createQuery(
				"select be from BaseEntity_PH_WD be where be.name in ('base entity 1', 'base entity 2')");
		System.out.println(baseQuery.getResultList());
		
		System.out.println("Fetching child entities");
		/* Hibernate: 
		 * select 
		 * 	childentit0_.id as id8_, childentit0_.assocEntityA_id as assocEnt8_8_, 
		 * 	childentit0_.name as name8_, childentit0_.version as version8_, 
		 * 	childentit0_.secName as secName8_, childentit0_.data as data8_, 
		 * 	childentit0_.DISC_TYPE as DISC1_8_ 
		 * from BaseEntity_PH_WD childentit0_ 
		 * where childentit0_.DISC_TYPE in ('CH', 'CCH') 
		 * 	and (childentit0_.name in ('child entity 1' , 'child entity 2'))
		 */
		Query childQuery = em.createQuery(
			"select ch from ChildEntity_PH_WD ch where ch.name in ('child entity 1', 'child entity 2')");
		System.out.println(childQuery.getResultList());
		
		System.out.println("Fetching another child entities");
		/* Hibernate: 
		 * select 
		 * 	childentit0_.id as id8_, childentit0_.assocEntityA_id as assocEnt8_8_, 
		 * 	childentit0_.name as name8_, childentit0_.version as version8_, childentit0_.tm as tm8_ 
		 * from BaseEntity_PH_WD childentit0_ 
		 * where childentit0_.DISC_TYPE='CHA' 
		 * 	and (childentit0_.name in ('child entity another 1' , 'child entity another 2'))
		 */
		Query childQueryAnother = em.createQuery(
			"select ch from ChildEntityAnother_PH_WD ch where ch.name in ('child entity another 1', 'child entity another 2')");
		System.out.println(childQueryAnother.getResultList());
		
		System.out.println("Fetching child child entities");
		/* Hibernate: 
		 * select 
		 * 	childchild0_.id as id8_, childchild0_.assocEntityA_id as assocEnt8_8_, 
		 * 	childchild0_.name as name8_, childchild0_.version as version8_, 
		 * 	childchild0_.secName as secName8_, childchild0_.data as data8_ 
		 * from BaseEntity_PH_WD childchild0_ 
		 * where childchild0_.DISC_TYPE='CCH' 
		 * 	and (childchild0_.name in ('Child Child 1' , 'Child Child 2' , 'Child Child 3'))
		 */
		Query childChildQuery = em.createQuery(
			"select cch from ChildChildEntity_PH_WD cch where cch.name in ('Child Child 1', 'Child Child 2', 'Child Child 3')");
		System.out.println(childChildQuery.getResultList());
	}
	
	private static void removingEntities() {
		creationChildren2();
		
		em.getTransaction().begin();
		
		System.out.println("Removing base entities");
		/* Removing base entities 
		 * Hibernate: delete from BaseEntity_PH_WD where name='base entity 1'
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from BaseEntity_PH_WD be where be.name = 'base entity 1'").executeUpdate());
		
		System.out.println("Removing child entities");
		/* Removing child entities
		 * Hibernate: delete from BaseEntity_PH_WD where DISC_TYPE in ('CH', 'CCH') and name='child entity 1'
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from ChildEntity_PH_WD chj where chj.name = 'child entity 1'").executeUpdate());
		
		System.out.println("Removing another child entities");
		/* Removing another child entities
		 * Hibernate: delete from BaseEntity_PH_WD where DISC_TYPE='CHA' and (name in ('child entity another 1' , 'child entity another 2'))
		 * 2
		 */
		System.out.println(em.createQuery(
				"delete from ChildEntityAnother_PH_WD cha where cha.name in ('child entity another 1', 'child entity another 2')").executeUpdate());
		
		System.out.println("Removing child child entities");
		/* Removing child child entities
		 * Hibernate: delete from BaseEntity_PH_WD where DISC_TYPE='CCH' and name='Child Child 2'
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from ChildChildEntity_PH_WD cch where cch.name = 'Child Child 2'").executeUpdate());
		
		em.getTransaction().commit();
	}
	
	private static void removingEntities2() {
		creationChildren2();
		
		em.getTransaction().begin();
		
		System.out.println("Removing base entities");
		BaseEntity be = (BaseEntity) em.createQuery(
				"select be from BaseEntity_PH_WD be where be.name = 'base entity 1'").getSingleResult();
		/* Removing base entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select baseentity0_.id as id8_, baseentity0_.assocEntityA_id as assocEnt8_8_, 
		 * 			baseentity0_.name as name8_, baseentity0_.version as version8_, 
		 * 			baseentity0_.secName as secName8_, baseentity0_.data as data8_, baseentity0_.tm as tm8_, 
		 * 			baseentity0_.DISC_TYPE as DISC1_8_ 
		 * 		from BaseEntity_PH_WD baseentity0_ 
		 * 		where baseentity0_.name='base entity 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from BaseEntity_PH_WD where id=? and version=?
		 */
		em.remove(be);
		em.flush();
		
		System.out.println("Removing child entities");
		ChildEntity ch = (ChildEntity) em.createQuery(
				"select ch from ChildEntity_PH_WD ch where ch.name = 'child entity 1'").getSingleResult();
		/* Removing child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childentit0_.id as id8_, childentit0_.assocEntityA_id as assocEnt8_8_, 
		 * 			childentit0_.name as name8_, childentit0_.version as version8_, 
		 * 			childentit0_.secName as secName8_, childentit0_.data as data8_, 
		 * 			childentit0_.DISC_TYPE as DISC1_8_ 
		 * 		from BaseEntity_PH_WD childentit0_ 
		 * 		where childentit0_.DISC_TYPE in ('CH', 'CCH') 
		 * 			and childentit0_.name='child entity 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from BaseEntity_PH_WD where id=? and version=?
		 */
		em.remove(ch);
		em.flush();
		
		System.out.println("Removing another child entities");
		ChildEntityAnother cha = (ChildEntityAnother) em.createQuery(
				"select cha from ChildEntityAnother_PH_WD cha where cha.name = 'child entity another 1'").getSingleResult();
		/* Removing another child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childentit0_.id as id8_, childentit0_.assocEntityA_id as assocEnt8_8_, 
		 * 			childentit0_.name as name8_, childentit0_.version as version8_, 
		 * 			childentit0_.tm as tm8_ 
		 * 		from BaseEntity_PH_WD childentit0_ 
		 * 		where childentit0_.DISC_TYPE='CHA' 
		 * 			and childentit0_.name='child entity another 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from BaseEntity_PH_WD where id=? and version=?
		 */
		em.remove(cha);
		em.flush();
		
		System.out.println("Removing child child entities");
		ChildChildEntity cch = (ChildChildEntity) em.createQuery(
				"select cch from ChildChildEntity_PH_WD cch where cch.name = 'Child Child 1'").getSingleResult();
		/* Removing child child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childchild0_.id as id8_, childchild0_.assocEntityA_id as assocEnt8_8_, 
		 * 			childchild0_.name as name8_, childchild0_.version as version8_, 
		 * 			childchild0_.secName as secName8_, childchild0_.data as data8_ 
		 * 		from BaseEntity_PH_WD childchild0_ 
		 * 		where childchild0_.DISC_TYPE='CCH' 
		 * 			and childchild0_.name='Child Child 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from BaseEntity_PH_WD where id=? and version=?
		 */
		em.remove(cch);
		em.flush();
		
		em.getTransaction().commit();
	}
	
	private static void updatingEntities1() {
		creationChildren2();
		
		em.getTransaction().begin();
		
		System.out.println("Updating base entities");
		BaseEntity be = (BaseEntity) em.createQuery(
		"select be from BaseEntity_PH_WD be where be.name = 'base entity 1'").getSingleResult();
		/* Updating base entities
		 * Hibernate: select * from ( select baseentity0_.id as id8_, baseentity0_.assocEntityA_id as assocEnt8_8_, baseentity0_.name as name8_, baseentity0_.version as version8_, baseentity0_.secName as secName8_, baseentity0_.data as data8_, baseentity0_.tm as tm8_, baseentity0_.DISC_TYPE as DISC1_8_ from BaseEntity_PH_WD baseentity0_ where baseentity0_.name='base entity 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntity_PH_WD 
		 * 	set assocEntityA_id=?, name=?, version=? 
		 * 	where id=? and version=?
		 */
		be.setName("some other name 1");
		em.flush();
		
		System.out.println("Updating child entities");
		ChildEntity ch = (ChildEntity) em.createQuery(
		"select ch from ChildEntity_PH_WD ch where ch.name = 'child entity 1'").getSingleResult();
		/* Updating child entities
		 * Hibernate: select * from ( select childentit0_.id as id8_, childentit0_.assocEntityA_id as assocEnt8_8_, childentit0_.name as name8_, childentit0_.version as version8_, childentit0_.secName as secName8_, childentit0_.data as data8_, childentit0_.DISC_TYPE as DISC1_8_ from BaseEntity_PH_WD childentit0_ where childentit0_.DISC_TYPE in ('CH', 'CCH') and childentit0_.name='child entity 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntity_PH_WD 
		 * 	set assocEntityA_id=?, name=?, version=?, secName=? 
		 * 	where id=? and version=?
		 */
		ch.setSecName("some other sec name 1");
		em.flush();
		
		System.out.println("Updating another child entities");
		ChildEntityAnother cha = (ChildEntityAnother) em.createQuery(
		"select cha from ChildEntityAnother_PH_WD cha where cha.name = 'child entity another 1'").getSingleResult();
		/* Updating another child entities
		 * Hibernate: select * from ( select childentit0_.id as id8_, childentit0_.assocEntityA_id as assocEnt8_8_, childentit0_.name as name8_, childentit0_.version as version8_, childentit0_.tm as tm8_ from BaseEntity_PH_WD childentit0_ where childentit0_.DISC_TYPE='CHA' and childentit0_.name='child entity another 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntity_PH_WD 
		 * 	set assocEntityA_id=?, name=?, version=?, tm=? 
		 * 	where id=? and version=?
		 */
		cha.setTm(new Date());
		em.flush();
		
		System.out.println("Updating child child entities");
		ChildChildEntity cch = (ChildChildEntity) em.createQuery(
		"select cch from ChildChildEntity_PH_WD cch where cch.name = 'Child Child 1'").getSingleResult();
		/* Updating child child entities
		 * Hibernate: select * from ( select childchild0_.id as id8_, childchild0_.assocEntityA_id as assocEnt8_8_, childchild0_.name as name8_, childchild0_.version as version8_, childchild0_.secName as secName8_, childchild0_.data as data8_ from BaseEntity_PH_WD childchild0_ where childchild0_.DISC_TYPE='CCH' and childchild0_.name='Child Child 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntity_PH_WD 
		 * 	set assocEntityA_id=?, name=?, version=?, secName=?, data=? 
		 * 	where id=? and version=?
		 */
		cch.setData("some other data 1");
		em.flush();
		
		em.getTransaction().commit();
	}
}
