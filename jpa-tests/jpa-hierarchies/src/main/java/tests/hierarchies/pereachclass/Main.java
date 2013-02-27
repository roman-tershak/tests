package tests.hierarchies.pereachclass;

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
//		fetchingEntities();
//		removingEntities();
//		removingEntities2();
		updatingEntities1();
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
		return em.createQuery("select assocs from AssociatedEntityA_PEC assocs").getResultList();
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
		/* Fetching base entities
		 * Hibernate: 
		 * 	select baseentity0_.id as id9_, baseentity0_.assocEntityA_id as assocEnt4_9_, 
		 * 		baseentity0_.name as name9_, baseentity0_.version as version9_, 
		 * 		baseentity0_.secName as secName10_, baseentity0_.data as data11_, 
		 * 		baseentity0_.tm as tm12_, baseentity0_.clazz_ as clazz_ 
		 * 	from ( 
		 * 			select id, null as tm, null as secName, assocEntityA_id, name, null as data, 
		 * 				version, 0 as clazz_ 
		 * 			from BaseEntity_PEC 
		 * 		union all 
		 * 			select id, null as tm, secName, assocEntityA_id, name, data, version, 2 as clazz_ 
		 * 			from ChildChildEntity_PEC 
		 * 		union all 
		 * 			select id, null as tm, secName, assocEntityA_id, name, null as data, version, 
		 * 				1 as clazz_ 
		 * 			from ChildEntity_PEC 
		 * 		union all 
		 * 			select id, tm, null as secName, assocEntityA_id, name, null as data, version, 
		 * 				3 as clazz_ 
		 * 			from ChildEntityAnother_PEC 
		 * 		) baseentity0_ 
		 * 	where baseentity0_.name in ('base entity 1' , 'base entity 2')
		 */
		Query baseQuery = em.createQuery(
				"select be from BaseEntity_PEC be where be.name in ('base entity 1', 'base entity 2')");
		System.out.println(baseQuery.getResultList());
		
		System.out.println("Fetching child entities");
		/* Fetching child entities
		 * Hibernate: 
		 * 	select childentit0_.id as id9_, childentit0_.assocEntityA_id as assocEnt4_9_, 
		 * 		childentit0_.name as name9_, childentit0_.version as version9_, 
		 * 		childentit0_.secName as secName10_, childentit0_.data as data11_, 
		 * 		childentit0_.clazz_ as clazz_ 
		 * 	from ( 
		 * 			select id, secName, assocEntityA_id, name, null as data, version, 1 as clazz_ 
		 * 			from ChildEntity_PEC 
		 * 		union all 
		 * 			select id, secName, assocEntityA_id, name, data, version, 2 as clazz_ 
		 * 			from ChildChildEntity_PEC 
		 * 		) childentit0_ 
		 * 	where childentit0_.name in ('child entity 1' , 'child entity 2')
		 */
		Query childQuery = em.createQuery(
			"select ch from ChildEntity_PEC ch where ch.name in ('child entity 1', 'child entity 2')");
		System.out.println(childQuery.getResultList());
		
		System.out.println("Fetching another child entities");
		/* Fetching another child entities
		 * Hibernate: 
		 * 	select childentit0_.id as id9_, childentit0_.assocEntityA_id as assocEnt4_9_, 
		 * 		childentit0_.name as name9_, childentit0_.version as version9_, childentit0_.tm as tm12_ 
		 * 	from ChildEntityAnother_PEC childentit0_ 
		 * 	where childentit0_.name in ('child entity another 1' , 'child entity another 2')
 		 */
		Query childQueryAnother = em.createQuery(
			"select ch from ChildEntityAnother_PEC ch where ch.name in ('child entity another 1', 'child entity another 2')");
		System.out.println(childQueryAnother.getResultList());
		
		System.out.println("Fetching child child entities");
		/* Fetching child child entities
		 * Hibernate: 
		 * 	select childchild0_.id as id9_, childchild0_.assocEntityA_id as assocEnt4_9_, 
		 * 		childchild0_.name as name9_, childchild0_.version as version9_, 
		 * 		childchild0_.secName as secName10_, childchild0_.data as data11_ 
		 * 	from ChildChildEntity_PEC childchild0_ 
		 * 	where childchild0_.name in ('Child Child 1' , 'Child Child 2' , 'Child Child 3')
		 */
		Query childChildQuery = em.createQuery(
			"select cch from ChildChildEntity_PEC cch where cch.name in ('Child Child 1', 'Child Child 2', 'Child Child 3')");
		System.out.println(childChildQuery.getResultList());
	}
	
	private static void removingEntities() {
		creationChildren2();
		
		em.getTransaction().begin();
		
		System.out.println("Removing base entities");
		/* Removing base entities
		 * Hibernate: 
		 * 	insert into HT_BaseEntity_PEC 
		 * 	select baseentity0_.id as id 
		 * 	from ( 
		 * 		select id, null as tm, null as secName, assocEntityA_id, name, null as data, version, 
		 * 			0 as clazz_ 
		 * 		from BaseEntity_PEC 
		 * 		union all 
		 * 		select id, null as tm, secName, assocEntityA_id, name, data, version, 2 as clazz_ 
		 * 		from ChildChildEntity_PEC 
		 * 		union all 
		 * 		select id, null as tm, secName, assocEntityA_id, name, null as data, version, 1 as clazz_ 
		 * 		from ChildEntity_PEC 
		 * 		union all 
		 * 		select id, tm, null as secName, assocEntityA_id, name, null as data, version, 3 as clazz_ 
		 * 		from ChildEntityAnother_PEC 
		 * 		) baseentity0_ 
		 * 	where name='base entity 1'
		 * Hibernate: delete from BaseEntity_PEC where (id) IN (select id from HT_BaseEntity_PEC)
		 * Hibernate: delete from BaseEntity_PEC where (id) IN (select id from HT_BaseEntity_PEC)
		 * Hibernate: delete from ChildEntity_PEC where (id) IN (select id from HT_BaseEntity_PEC)
		 * Hibernate: delete from ChildChildEntity_PEC where (id) IN (select id from HT_BaseEntity_PEC)
		 * Hibernate: delete from ChildEntityAnother_PEC where (id) IN (select id from HT_BaseEntity_PEC)
		 * Hibernate: delete from HT_BaseEntity_PEC
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from BaseEntity_PEC be where be.name = 'base entity 1'").executeUpdate());
		em.flush();
		
		System.out.println("Removing child entities");
		/* Removing child entities
		 * Hibernate: 
		 * 	insert into HT_ChildEntity_PEC 
		 * 	select childentit0_.id as id 
		 * 	from ( 
		 * 		select id, secName, assocEntityA_id, name, null as data, version, 1 as clazz_ 
		 * 		from ChildEntity_PEC 
		 * 		union all 
		 * 		select id, secName, assocEntityA_id, name, data, version, 2 as clazz_ 
		 * 		from ChildChildEntity_PEC 
		 * 		) childentit0_ 
		 * 	where name='child entity 1'
		 * Hibernate: delete from ChildEntity_PEC where (id) IN (select id from HT_ChildEntity_PEC)
		 * Hibernate: delete from BaseEntity_PEC where (id) IN (select id from HT_ChildEntity_PEC)
		 * Hibernate: delete from ChildEntity_PEC where (id) IN (select id from HT_ChildEntity_PEC)
		 * Hibernate: delete from ChildChildEntity_PEC where (id) IN (select id from HT_ChildEntity_PEC)
		 * Hibernate: delete from HT_ChildEntity_PEC
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from ChildEntity_PEC chj where chj.name = 'child entity 1'").executeUpdate());
		em.flush();
		
		System.out.println("Removing another child entities");
		/* Removing another child entities
		 * Hibernate: 
		 * 	delete from ChildEntityAnother_PEC 
		 * 	where name in ('child entity another 1' , 'child entity another 2')
		 * 2
		 */
		System.out.println(em.createQuery(
				"delete from ChildEntityAnother_PEC cha where cha.name in ('child entity another 1', 'child entity another 2')").executeUpdate());
		em.flush();
		
		System.out.println("Removing child child entities");
		/* Removing child child entities
		 * Hibernate: delete from ChildChildEntity_PEC where name='Child Child 2'
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from ChildChildEntity_PEC cch where cch.name = 'Child Child 2'").executeUpdate());
		em.flush();
		
		em.getTransaction().commit();
	}
	
	private static void removingEntities2() {
		creationChildren2();
		
		em.getTransaction().begin();
		
		System.out.println("Removing base entities");
		BaseEntity be = (BaseEntity) em.createQuery(
				"select be from BaseEntity_PEC be where be.name = 'base entity 1'").getSingleResult();
		/* Removing base entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select baseentity0_.id as id9_, baseentity0_.assocEntityA_id as assocEnt4_9_, 
		 * 			baseentity0_.name as name9_, baseentity0_.version as version9_, 
		 * 			baseentity0_.secName as secName10_, baseentity0_.data as data11_, 
		 * 			baseentity0_.tm as tm12_, baseentity0_.clazz_ as clazz_ 
		 * 		from ( 
		 * 			select id, null as tm, null as secName, assocEntityA_id, name, null as data, version, 
		 * 				0 as clazz_ 
		 * 			from BaseEntity_PEC 
		 * 			union all 
		 * 			select id, null as tm, secName, assocEntityA_id, name, data, version, 2 as clazz_ 
		 * 			from ChildChildEntity_PEC 
		 * 			union all 
		 * 			select id, null as tm, secName, assocEntityA_id, name, null as data, version, 
		 * 				1 as clazz_ 
		 * 			from ChildEntity_PEC 
		 * 			union all 
		 * 			select id, tm, null as secName, assocEntityA_id, name, null as data, version, 3 as clazz_ 
		 * 			from ChildEntityAnother_PEC 
		 * 			) baseentity0_ 
		 * 		where baseentity0_.name='base entity 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from BaseEntity_PEC where id=? and version=?
		 */
		em.remove(be);
		em.flush();
		
		System.out.println("Removing child entities");
		ChildEntity ch = (ChildEntity) em.createQuery(
				"select ch from ChildEntity_PEC ch where ch.name = 'child entity 1'").getSingleResult();
		/* Removing child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childentit0_.id as id9_, childentit0_.assocEntityA_id as assocEnt4_9_, 
		 * 			childentit0_.name as name9_, childentit0_.version as version9_, 
		 * 			childentit0_.secName as secName10_, childentit0_.data as data11_, 
		 * 			childentit0_.clazz_ as clazz_ 
		 * 		from ( 
		 * 			select id, secName, assocEntityA_id, name, null as data, version, 1 as clazz_ 
		 * 			from ChildEntity_PEC 
		 * 			union all 
		 * 			select id, secName, assocEntityA_id, name, data, version, 2 as clazz_ 
		 * 			from ChildChildEntity_PEC 
		 * 			) childentit0_ 
		 * 		where childentit0_.name='child entity 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from ChildEntity_PEC where id=? and version=?
		 */
		em.remove(ch);
		em.flush();
		
		System.out.println("Removing another child entities");
		ChildEntityAnother cha = (ChildEntityAnother) em.createQuery(
				"select cha from ChildEntityAnother_PEC cha where cha.name = 'child entity another 1'").getSingleResult();
		/* Removing another child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childentit0_.id as id9_, childentit0_.assocEntityA_id as assocEnt4_9_, 
		 * 			childentit0_.name as name9_, childentit0_.version as version9_, childentit0_.tm as tm12_ 
		 * 		from ChildEntityAnother_PEC childentit0_ 
		 * 		where childentit0_.name='child entity another 1' 
		 * 		) 
		 * 	where rownum <= ?
		 * Hibernate: delete from ChildEntityAnother_PEC where id=? and version=?
		 */
		em.remove(cha);
		em.flush();
		
		System.out.println("Removing child child entities");
		ChildChildEntity cch = (ChildChildEntity) em.createQuery(
				"select cch from ChildChildEntity_PEC cch where cch.name = 'Child Child 1'").getSingleResult();
		/* Removing child child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childchild0_.id as id9_, childchild0_.assocEntityA_id as assocEnt4_9_, 
		 * 			childchild0_.name as name9_, childchild0_.version as version9_, 
		 * 			childchild0_.secName as secName10_, childchild0_.data as data11_ 
		 * 		from ChildChildEntity_PEC childchild0_ 
		 * 		where childchild0_.name='Child Child 1' 
		 * 		) 
		 * 	where rownum <= ?
		 * Hibernate: delete from ChildChildEntity_PEC where id=? and version=?
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
		"select be from BaseEntity_PEC be where be.name = 'base entity 1'").getSingleResult();
		/* Updating base entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select baseentity0_.id as id9_, baseentity0_.assocEntityA_id as assocEnt4_9_, 
		 * 			baseentity0_.name as name9_, baseentity0_.version as version9_, 
		 * 			baseentity0_.secName as secName10_, baseentity0_.data as data11_, 
		 * 			baseentity0_.tm as tm12_, baseentity0_.clazz_ as clazz_ 
		 * 		from ( 
		 * 			select id, null as tm, null as secName, assocEntityA_id, name, null as data, version, 0 as clazz_ 
		 * 			from BaseEntity_PEC 
		 * 			union all 
		 * 			select id, null as tm, secName, assocEntityA_id, name, data, version, 2 as clazz_ 
		 * 			from ChildChildEntity_PEC 
		 * 			union all 
		 * 			select id, null as tm, secName, assocEntityA_id, name, null as data, version, 1 as clazz_ 
		 * 			from ChildEntity_PEC 
		 * 			union all 
		 * 			select id, tm, null as secName, assocEntityA_id, name, null as data, version, 3 as clazz_ 
		 * 			from ChildEntityAnother_PEC 
		 * 		) baseentity0_ 
		 * 		where baseentity0_.name='base entity 1' 
		 * 	) 
		 * 	where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntity_PEC 
		 * 	set assocEntityA_id=?, name=?, version=? 
		 * 	where id=? and version=?
		 */
		be.setName("some other name 1");
		em.flush();
		
		System.out.println("Updating child entities");
		ChildEntity ch = (ChildEntity) em.createQuery(
		"select ch from ChildEntity_PEC ch where ch.name = 'child entity 1'").getSingleResult();
		/* Updating child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childentit0_.id as id9_, childentit0_.assocEntityA_id as assocEnt4_9_, 
		 * 			childentit0_.name as name9_, childentit0_.version as version9_, 
		 * 			childentit0_.secName as secName10_, childentit0_.data as data11_, 
		 * 			childentit0_.clazz_ as clazz_ 
		 * 		from ( 
		 * 			select id, secName, assocEntityA_id, name, null as data, version, 1 as clazz_ 
		 * 			from ChildEntity_PEC 
		 * 			union all 
		 * 			select id, secName, assocEntityA_id, name, data, version, 2 as clazz_ 
		 * 			from ChildChildEntity_PEC 
		 * 		) childentit0_ 
		 * 		where childentit0_.name='child entity 1' 
		 * 	) 
		 * 	where rownum <= ?
		 * Hibernate: 
		 * 	update ChildEntity_PEC 
		 * 	set assocEntityA_id=?, name=?, version=?, secName=? 
		 * 	where id=? and version=?
		 */
		ch.setSecName("some other sec name 1");
		em.flush();
		
		System.out.println("Updating another child entities");
		ChildEntityAnother cha = (ChildEntityAnother) em.createQuery(
		"select cha from ChildEntityAnother_PEC cha where cha.name = 'child entity another 1'").getSingleResult();
		/* Updating another child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childentit0_.id as id9_, childentit0_.assocEntityA_id as assocEnt4_9_, 
		 * 			childentit0_.name as name9_, childentit0_.version as version9_, childentit0_.tm as tm12_ 
		 * 		from ChildEntityAnother_PEC childentit0_ 
		 * 		where childentit0_.name='child entity another 1' 
		 * 	) 
		 * 	where rownum <= ?
		 * Hibernate: 
		 * 	update ChildEntityAnother_PEC 
		 * 	set assocEntityA_id=?, name=?, version=?, tm=? 
		 * 	where id=? and version=?
		 */
		cha.setTm(new Date());
		em.flush();
		
		System.out.println("Updating child child entities");
		ChildChildEntity cch = (ChildChildEntity) em.createQuery(
		"select cch from ChildChildEntity_PEC cch where cch.name = 'Child Child 1'").getSingleResult();
		/* Updating child child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childchild0_.id as id9_, childchild0_.assocEntityA_id as assocEnt4_9_, 
		 * 			childchild0_.name as name9_, childchild0_.version as version9_, 
		 * 			childchild0_.secName as secName10_, childchild0_.data as data11_ 
		 * 		from ChildChildEntity_PEC childchild0_ 
		 * 		where childchild0_.name='Child Child 1' 
		 * 	) 
		 * 	where rownum <= ?
		 * Hibernate: 
		 * 	update ChildChildEntity_PEC 
		 * 	set assocEntityA_id=?, name=?, version=?, secName=?, data=? 
		 * 	where id=? and version=?
		 */
		cch.setData("some other data 1");
		em.flush();
		
		System.out.println("Updating child child entities 2");
		ChildChildEntity cch2 = (ChildChildEntity) em.createQuery(
		"select cch from ChildChildEntity_PEC cch where cch.name = 'Child Child 1'").getSingleResult();
		/* Updating child child entities 2
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childchild0_.id as id9_, childchild0_.assocEntityA_id as assocEnt4_9_, 
		 * 			childchild0_.name as name9_, childchild0_.version as version9_, 
		 * 			childchild0_.secName as secName10_, childchild0_.data as data11_ 
		 * 		from ChildChildEntity_PEC childchild0_ 
		 * 		where childchild0_.name='Child Child 1' 
		 * 	) 
		 * 	where rownum <= ?
		 * Hibernate: 
		 * 	update ChildChildEntity_PEC 
		 * 	set assocEntityA_id=?, name=?, version=?, secName=?, data=? 
		 * 	where id=? and version=?
		 */
		cch2.setData("some other data 2");
		cch2.setSecName("some other sec name 2");
		em.flush();
		
		em.getTransaction().commit();
	}
}
