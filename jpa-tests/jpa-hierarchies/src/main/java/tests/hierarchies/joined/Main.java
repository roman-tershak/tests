package tests.hierarchies.joined;

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
		return em.createQuery("select assocs from AssociatedEntityAJoined assocs").getResultList();
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
		 * Hibernate: insert into BaseEntityJoined (assocEntityA_id, name, version, id) values (?, ?, ?, ?)
		 * Hibernate: insert into ChildEntityJoined (secName, id) values (?, ?)
		 * Hibernate: insert into ChildChildEntity (data, id) values (?, ?)
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
		 * select baseentity0_.id as id3_, baseentity0_.assocEntityA_id as assocEnt4_3_, 
		 * 	baseentity0_.name as name3_, baseentity0_.version as version3_, 
		 * 	baseentity0_1_.secName as secName4_, baseentity0_2_.tm as tm5_, 
		 * 	baseentity0_3_.data as data6_, 
		 * 	case 
		 * 		when baseentity0_3_.id is not null then 3 
		 * 		when baseentity0_1_.id is not null then 1 
		 * 		when baseentity0_2_.id is not null then 2 
		 * 		when baseentity0_.id is not null then 0 end as clazz_ 
		 * from BaseEntityJoined baseentity0_ 
		 * 	left outer join ChildEntityJoined baseentity0_1_ on baseentity0_.id=baseentity0_1_.id 
		 * 	left outer join ChildEntityAnother baseentity0_2_ on baseentity0_.id=baseentity0_2_.id 
		 * 	left outer join ChildChildEntity baseentity0_3_ on baseentity0_.id=baseentity0_3_.id 
		 * where baseentity0_.name in ('base entity 1' , 'base entity 2')
		 */
		Query baseQuery = em.createQuery(
				"select be from BaseEntityJoined be where be.name in ('base entity 1', 'base entity 2')");
		System.out.println(baseQuery.getResultList());
		
		System.out.println("Fetching child entities");
		/* If ChildChildEntity did not exist
		 * Hibernate: 
		 * select childentit0_.id as id3_, childentit0_1_.assocEntityA_id as assocEnt4_3_, 
		 * 	childentit0_1_.name as name3_, childentit0_1_.version as version3_, childentit0_.secName as secName4_ 
		 * from ChildEntityJoined childentit0_ 
		 * 	inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id 
		 * where childentit0_1_.name in ('child entity 1' , 'child entity 2')
		 */
		/* Hibernate: 
		 * select childentit0_.id as id3_, childentit0_1_.assocEntityA_id as assocEnt4_3_, 
		 * 	childentit0_1_.name as name3_, childentit0_1_.version as version3_, childentit0_.secName as secName4_, 
		 * 	childentit0_2_.data as data6_, 
		 * 	case 
		 * 		when childentit0_2_.id is not null then 3 
		 * 		when childentit0_.id is not null then 1 end as clazz_ 
		 * from ChildEntityJoined childentit0_ 
		 * 	inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id 
		 * 	left outer join ChildChildEntity childentit0_2_ on childentit0_.id=childentit0_2_.id 
		 * where childentit0_1_.name in ('child entity 1' , 'child entity 2')
		 */
		Query childQuery = em.createQuery(
			"select ch from ChildEntityJoined ch where ch.name in ('child entity 1', 'child entity 2')");
		System.out.println(childQuery.getResultList());
		
		System.out.println("Fetching another child entities");
		/* Hibernate: 
		 * select childentit0_.id as id3_, childentit0_1_.assocEntityA_id as assocEnt4_3_, 
		 * 	childentit0_1_.name as name3_, childentit0_1_.version as version3_, childentit0_.tm as tm5_ 
		 * from ChildEntityAnother childentit0_ 
		 * 	inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id 
		 * where childentit0_1_.name in ('child entity another 1' , 'child entity another 2')*/
		Query childQueryAnother = em.createQuery(
			"select ch from ChildEntityAnother ch where ch.name in ('child entity another 1', 'child entity another 2')");
		System.out.println(childQueryAnother.getResultList());
		
		System.out.println("Fetching child child entities");
		/* Hibernate: 
		 * select childchild0_.id as id3_, childchild0_2_.assocEntityA_id as assocEnt4_3_, 
		 * 	childchild0_2_.name as name3_, childchild0_2_.version as version3_, childchild0_1_.secName as secName4_, 
		 * 	childchild0_.data as data6_ 
		 * from ChildChildEntity childchild0_ 
		 * 	inner join ChildEntityJoined childchild0_1_ on childchild0_.id=childchild0_1_.id 
		 * 	inner join BaseEntityJoined childchild0_2_ on childchild0_.id=childchild0_2_.id 
		 * where childchild0_2_.name in ('Child Child 1' , 'Child Child 2' , 'Child Child 3')
		 */
		Query childChildQuery = em.createQuery(
			"select cch from ChildChildEntity cch where cch.name in ('Child Child 1', 'Child Child 2', 'Child Child 3')");
		System.out.println(childChildQuery.getResultList());
	}
	
	private static void removingEntities() {
		creationChildren2();
		
		em.getTransaction().begin();
		
		System.out.println("Removing base entities");
		/* Removing base entities 
		 * Hibernate: 
		 * 	insert into HT_BaseEntityJoined 
		 * 	select baseentity0_.id as id 
		 * 	from BaseEntityJoined baseentity0_ 
		 * 	where name='base entity 1'
		 * Hibernate: delete from ChildChildEntity where (id) IN (select id from HT_BaseEntityJoined)
		 * Hibernate: delete from ChildEntityAnother where (id) IN (select id from HT_BaseEntityJoined)
		 * Hibernate: delete from ChildEntityJoined where (id) IN (select id from HT_BaseEntityJoined)
		 * Hibernate: delete from BaseEntityJoined where (id) IN (select id from HT_BaseEntityJoined)
		 * Hibernate: delete from HT_BaseEntityJoined
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from BaseEntityJoined be where be.name = 'base entity 1'").executeUpdate());
		
		System.out.println("Removing child entities");
		/*
		 * Removing child entities
		 * Hibernate: 
		 * 	insert into HT_ChildEntityJoined 
		 * 	select childentit0_.id as id 
		 * 	from ChildEntityJoined childentit0_ 
		 * 		inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id 
		 * 	where name='child entity 1'
		 * Hibernate: delete from ChildChildEntity where (id) IN (select id from HT_ChildEntityJoined)
		 * Hibernate: delete from ChildEntityJoined where (id) IN (select id from HT_ChildEntityJoined)
		 * Hibernate: delete from BaseEntityJoined where (id) IN (select id from HT_ChildEntityJoined)
		 * Hibernate: delete from HT_ChildEntityJoined
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from ChildEntityJoined chj where chj.name = 'child entity 1'").executeUpdate());
		
		System.out.println("Removing another child entities");
		/*
		 * Removing another child entities
		 * Hibernate: 
		 * 	insert into HT_ChildEntityAnother 
		 * 	select childentit0_.id as id from ChildEntityAnother childentit0_ 
		 * 		inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id 
		 * 	where name in ('child entity another 1' , 'child entity another 2')
		 * Hibernate: delete from ChildEntityAnother where (id) IN (select id from HT_ChildEntityAnother)
		 * Hibernate: delete from BaseEntityJoined where (id) IN (select id from HT_ChildEntityAnother)
		 * Hibernate: delete from HT_ChildEntityAnother
		 * 2
		 */
		System.out.println(em.createQuery(
				"delete from ChildEntityAnother cha where cha.name in ('child entity another 1', 'child entity another 2')").executeUpdate());
		
		System.out.println("Removing child child entities");
		/*
		 * Removing child child entities
		 * Hibernate: 
		 * 	insert into HT_ChildChildEntity 
		 * 	select childchild0_.id as id 
		 * 	from ChildChildEntity childchild0_ 
		 * 		inner join ChildEntityJoined childchild0_1_ on childchild0_.id=childchild0_1_.id 
		 * 		inner join BaseEntityJoined childchild0_2_ on childchild0_.id=childchild0_2_.id 
		 * 	where name='Child Child 2'
		 * Hibernate: delete from ChildChildEntity where (id) IN (select id from HT_ChildChildEntity)
		 * Hibernate: delete from ChildEntityJoined where (id) IN (select id from HT_ChildChildEntity)
		 * Hibernate: delete from BaseEntityJoined where (id) IN (select id from HT_ChildChildEntity)
		 * Hibernate: delete from HT_ChildChildEntity
		 * 1
		 */
		System.out.println(em.createQuery(
				"delete from ChildChildEntity cch where cch.name = 'Child Child 2'").executeUpdate());
		
		em.getTransaction().commit();
	}
	
	private static void removingEntities2() {
		creationChildren2();
		
		em.getTransaction().begin();
		
		System.out.println("Removing base entities");
		BaseEntity be = (BaseEntity) em.createQuery(
				"select be from BaseEntityJoined be where be.name = 'base entity 1'").getSingleResult();
		/*
		 * Removing base entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select baseentity0_.id as id3_, baseentity0_.assocEntityA_id as assocEnt4_3_, 
		 * 			baseentity0_.name as name3_, baseentity0_.version as version3_, 
		 * 			baseentity0_1_.secName as secName4_, baseentity0_2_.tm as tm5_, 
		 * 			baseentity0_3_.data as data6_, 
		 * 			case 
		 * 				when baseentity0_3_.id is not null then 3 
		 * 				when baseentity0_1_.id is not null then 1 
		 * 				when baseentity0_2_.id is not null then 2 
		 * 				when baseentity0_.id is not null then 0 end as clazz_ 
		 * 		from BaseEntityJoined baseentity0_ 
		 * 			left outer join ChildEntityJoined baseentity0_1_ on baseentity0_.id=baseentity0_1_.id 
		 * 			left outer join ChildEntityAnother baseentity0_2_ on baseentity0_.id=baseentity0_2_.id 
		 * 			left outer join ChildChildEntity baseentity0_3_ on baseentity0_.id=baseentity0_3_.id 
		 * 		where baseentity0_.name='base entity 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from BaseEntityJoined where id=? and version=?
		 */
		em.remove(be);
		em.flush();
		
		System.out.println("Removing child entities");
		ChildEntity ch = (ChildEntity) em.createQuery(
				"select ch from ChildEntityJoined ch where ch.name = 'child entity 1'").getSingleResult();
		/*
		 * Removing child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childentit0_.id as id3_, childentit0_1_.assocEntityA_id as assocEnt4_3_, 
		 * 			childentit0_1_.name as name3_, childentit0_1_.version as version3_, 
		 * 			childentit0_.secName as secName4_, childentit0_2_.data as data6_, 
		 * 			case 
		 * 				when childentit0_2_.id is not null then 3 
		 * 				when childentit0_.id is not null then 1 end as clazz_ 
		 * 		from ChildEntityJoined childentit0_ 
		 * 			inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id 
		 * 			left outer join ChildChildEntity childentit0_2_ on childentit0_.id=childentit0_2_.id 
		 * 		where childentit0_1_.name='child entity 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from ChildEntityJoined where id=?
		 * Hibernate: delete from BaseEntityJoined where id=? and version=?
		 */
		em.remove(ch);
		em.flush();
		
		System.out.println("Removing another child entities");
		ChildEntityAnother cha = (ChildEntityAnother) em.createQuery(
				"select cha from ChildEntityAnother cha where cha.name = 'child entity another 1'").getSingleResult();
		/*
		 * Removing another child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childentit0_.id as id3_, childentit0_1_.assocEntityA_id as assocEnt4_3_, 
		 * 			childentit0_1_.name as name3_, childentit0_1_.version as version3_, 
		 * 			childentit0_.tm as tm5_ 
		 * 		from ChildEntityAnother childentit0_ 
		 * 			inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id 
		 * 		where childentit0_1_.name='child entity another 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from ChildEntityAnother where id=?
		 * Hibernate: delete from BaseEntityJoined where id=? and version=?
		 */
		em.remove(cha);
		em.flush();
		
		System.out.println("Removing child child entities");
		ChildChildEntity cch = (ChildChildEntity) em.createQuery(
				"select cch from ChildChildEntity cch where cch.name = 'Child Child 1'").getSingleResult();
		/*
		 * Removing child child entities
		 * Hibernate: 
		 * 	select * from ( 
		 * 		select childchild0_.id as id3_, childchild0_2_.assocEntityA_id as assocEnt4_3_, 
		 * 			childchild0_2_.name as name3_, childchild0_2_.version as version3_, 
		 * 			childchild0_1_.secName as secName4_, childchild0_.data as data6_ 
		 * 		from ChildChildEntity childchild0_ 
		 * 			inner join ChildEntityJoined childchild0_1_ on childchild0_.id=childchild0_1_.id 
		 * 			inner join BaseEntityJoined childchild0_2_ on childchild0_.id=childchild0_2_.id 
		 * 		where childchild0_2_.name='Child Child 1' 
		 * 	) where rownum <= ?
		 * Hibernate: delete from ChildChildEntity where id=?
		 * Hibernate: delete from ChildEntityJoined where id=?
		 * Hibernate: delete from BaseEntityJoined where id=? and version=?
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
		"select be from BaseEntityJoined be where be.name = 'base entity 1'").getSingleResult();
		/* Updating base entities
		 * Hibernate: select * from ( select baseentity0_.id as id3_, baseentity0_.assocEntityA_id as assocEnt4_3_, baseentity0_.name as name3_, baseentity0_.version as version3_, baseentity0_1_.secName as secName4_, baseentity0_2_.data as data5_, baseentity0_3_.tm as tm6_, case when baseentity0_2_.id is not null then 2 when baseentity0_1_.id is not null then 1 when baseentity0_3_.id is not null then 3 when baseentity0_.id is not null then 0 end as clazz_ from BaseEntityJoined baseentity0_ left outer join ChildEntityJoined baseentity0_1_ on baseentity0_.id=baseentity0_1_.id left outer join ChildChildEntity baseentity0_2_ on baseentity0_.id=baseentity0_2_.id left outer join ChildEntityAnother baseentity0_3_ on baseentity0_.id=baseentity0_3_.id where baseentity0_.name='base entity 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntityJoined 
		 * 	set assocEntityA_id=?, name=?, version=? 
		 * 	where id=? and version=?
		 */
		be.setName("some other name 1");
		em.flush();
		
		System.out.println("Updating child entities");
		ChildEntity ch = (ChildEntity) em.createQuery(
		"select ch from ChildEntityJoined ch where ch.name = 'child entity 1'").getSingleResult();
		/* Updating child entities
		 * Hibernate: select * from ( select childentit0_.id as id3_, childentit0_1_.assocEntityA_id as assocEnt4_3_, childentit0_1_.name as name3_, childentit0_1_.version as version3_, childentit0_.secName as secName4_, childentit0_2_.data as data5_, case when childentit0_2_.id is not null then 2 when childentit0_.id is not null then 1 end as clazz_ from ChildEntityJoined childentit0_ inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id left outer join ChildChildEntity childentit0_2_ on childentit0_.id=childentit0_2_.id where childentit0_1_.name='child entity 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntityJoined 
		 * 	set assocEntityA_id=?, name=?, version=? 
		 * 	where id=? and version=?
		 * Hibernate: 
		 * 	update ChildEntityJoined set secName=? where id=?
		 */
		ch.setSecName("some other sec name 1");
		em.flush();
		
		System.out.println("Updating another child entities");
		ChildEntityAnother cha = (ChildEntityAnother) em.createQuery(
		"select cha from ChildEntityAnother cha where cha.name = 'child entity another 1'").getSingleResult();
		/* Updating another child entities
		 * Hibernate: select * from ( select childentit0_.id as id3_, childentit0_1_.assocEntityA_id as assocEnt4_3_, childentit0_1_.name as name3_, childentit0_1_.version as version3_, childentit0_.tm as tm6_ from ChildEntityAnother childentit0_ inner join BaseEntityJoined childentit0_1_ on childentit0_.id=childentit0_1_.id where childentit0_1_.name='child entity another 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntityJoined 
		 * 	set assocEntityA_id=?, name=?, version=? 
		 * 	where id=? and version=?
		 * Hibernate: 
		 * 	update ChildEntityAnother set tm=? where id=?
		 */
		cha.setTm(new Date());
		em.flush();
		
		System.out.println("Updating child child entities");
		ChildChildEntity cch = (ChildChildEntity) em.createQuery(
		"select cch from ChildChildEntity cch where cch.name = 'Child Child 1'").getSingleResult();
		/* Updating child child entities
		 * Hibernate: select * from ( select childchild0_.id as id3_, childchild0_2_.assocEntityA_id as assocEnt4_3_, childchild0_2_.name as name3_, childchild0_2_.version as version3_, childchild0_1_.secName as secName4_, childchild0_.data as data5_ from ChildChildEntity childchild0_ inner join ChildEntityJoined childchild0_1_ on childchild0_.id=childchild0_1_.id inner join BaseEntityJoined childchild0_2_ on childchild0_.id=childchild0_2_.id where childchild0_2_.name='Child Child 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntityJoined 
		 * 	set assocEntityA_id=?, name=?, version=? 
		 * 	where id=? and version=?
		 * Hibernate: 
		 * 	update ChildChildEntity set data=? where id=?
		 */
		cch.setData("some other data 1");
		em.flush();
		
		System.out.println("Updating child child entities 2");
		ChildChildEntity cch2 = (ChildChildEntity) em.createQuery(
		"select cch from ChildChildEntity cch where cch.name = 'Child Child 1'").getSingleResult();
		/* Updating child child entities 2
		 * Hibernate: select * from ( select childchild0_.id as id3_, childchild0_2_.assocEntityA_id as assocEnt4_3_, childchild0_2_.name as name3_, childchild0_2_.version as version3_, childchild0_1_.secName as secName4_, childchild0_.data as data5_ from ChildChildEntity childchild0_ inner join ChildEntityJoined childchild0_1_ on childchild0_.id=childchild0_1_.id inner join BaseEntityJoined childchild0_2_ on childchild0_.id=childchild0_2_.id where childchild0_2_.name='Child Child 1' ) where rownum <= ?
		 * Hibernate: 
		 * 	update BaseEntityJoined 
		 * 	set assocEntityA_id=?, name=?, version=? 
		 * 	where id=? and version=?
		 * Hibernate: update ChildEntityJoined set secName=? where id=?
		 * Hibernate: update ChildChildEntity set data=? where id=?
		 */
		cch2.setData("some other data 2");
		cch2.setSecName("some other sec name 2");
		em.flush();
		
		em.getTransaction().commit();
	}
}
