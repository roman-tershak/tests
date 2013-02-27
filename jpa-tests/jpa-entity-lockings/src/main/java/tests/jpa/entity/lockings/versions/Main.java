package tests.jpa.entity.lockings.versions;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Main {
	
	private static EntityManager em = Persistence.createEntityManagerFactory("jpa-tests02-01").createEntityManager();
	
	public static void main(String[] args) {
//		creation1();
		testReadLockOnOracle();
//		testReadLockOnMicrosoftSqlServer();
//		testReadLockOnPostgreSql();
//		testReadLockOnMySql();
	}

	private static void creation1() {
		em.getTransaction().begin();
		
		LockedVersionedEntity entity1 = new LockedVersionedEntity("lock entity 1");
		LockedVersionedEntity entity2 = new LockedVersionedEntity("lock entity 2");
		LockedVersionedEntity entity3 = new LockedVersionedEntity("lock entity 3");
		LockedVersionedEntity entity4 = new LockedVersionedEntity("lock entity 4");
		em.persist(entity1);
		em.persist(entity2);
		em.persist(entity3);
		em.persist(entity4);
		
		em.getTransaction().commit();
	}
	
	private static void testReadLockOnOracle() {
		creation1();
		
		em.getTransaction().begin();
		
		LockedVersionedEntity entity1 = em.find(LockedVersionedEntity.class, 1L);
		
		// LockModeType.WRITE uses this:
		// Hibernate: update LockedVersionedEntity set version=? where id=? and version=?
		// If LockModeType.WRITE and if already locked by another transaction:
		em.lock(entity1, LockModeType.WRITE);
		
		// LockModeType.READ uses this:
		// Hibernate: select id from LockedVersionedEntity where id =? and version =? for update
		// If LockModeType.READ and if already locked by another transaction:
//		em.lock(entity1, LockModeType.READ);
		
		em.getTransaction().commit();
	}
	
	private static void testReadLockOnMicrosoftSqlServer() {
		creation1();
		
		em.getTransaction().begin();
		
		LockedVersionedEntity entity1 = em.find(LockedVersionedEntity.class, 1L);
		
		// LockModeType.WRITE uses this:
		// Hibernate: update LockedVersionedEntity set version=? where id=? and version=?
		// For LockModeType.WRITE if already locked by another transaction:
		em.lock(entity1, LockModeType.WRITE);
		
		// LockModeType.READ uses this:
		// Hibernate: select id from LockedVersionedEntity with (updlock, rowlock) where id =? and version =?
		// If LockModeType.READ and if already locked by another transaction:
//		em.lock(entity1, LockModeType.READ);
		
		em.getTransaction().commit();
	}
	
	private static void testReadLockOnPostgreSql() {
		creation1();
		
		em.getTransaction().begin();
		
		LockedVersionedEntity entity1 = em.find(LockedVersionedEntity.class, 1L);
		
		// LockModeType.WRITE uses this:
		// Hibernate: update LockedVersionedEntity set version=? where id=? and version=?
		// For LockModeType.WRITE if already locked by another transaction:
//		em.lock(entity1, LockModeType.WRITE);
		
		// LockModeType.READ uses this:
		// Hibernate: select id from LockedVersionedEntity where id =? and version =? for update
		// If LockModeType.READ and if already locked by another transaction:
		em.lock(entity1, LockModeType.READ);
		
		em.getTransaction().commit();
	}
	
	private static void testReadLockOnMySql() {
//		creation1();
		
		em.getTransaction().begin();
		
		LockedVersionedEntity entity1 = em.find(LockedVersionedEntity.class, 1L);
		
		// LockModeType.WRITE uses this:
		// Hibernate: update LockedVersionedEntity set version=? where id=? and version=?
		// If LockModeType.WRITE and if already locked by another transaction:
//		em.lock(entity1, LockModeType.WRITE);
		
		// LockModeType.READ uses this:
		// Hibernate: select id from LockedVersionedEntity where id =? and version =? for update
		// If LockModeType.READ and if already locked by another transaction:
		em.lock(entity1, LockModeType.READ);
		
		em.getTransaction().commit();
	}
	
}
