package tests.jpa.entity.lockings;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;

public class Main {
	
	private static EntityManager em = Persistence.createEntityManagerFactory("jpa-tests02-01").createEntityManager();
	
	public static void main(String[] args) {
//		creation1();
//		testReadLockOnOracle();
//		testReadLockOnMicrosoftSqlServer();
//		testReadLockOnPostgreSql();
		testReadLockOnMySql();
	}

	private static void creation1() {
		em.getTransaction().begin();
		
		LockedEntity entity1 = new LockedEntity("lock entity 1");
		LockedEntity entity2 = new LockedEntity("lock entity 2");
		LockedEntity entity3 = new LockedEntity("lock entity 3");
		LockedEntity entity4 = new LockedEntity("lock entity 4");
		em.persist(entity1);
		em.persist(entity2);
		em.persist(entity3);
		em.persist(entity4);
		
		em.getTransaction().commit();
	}
	
	private static void testReadLockOnOracle() {
		creation1();
		
		em.getTransaction().begin();
		
		LockedEntity entity1 = em.find(LockedEntity.class, 1L);
		
		// For LockModeType.WRITE if already locked by another transaction:
		// Exception in thread "main" javax.persistence.PersistenceException: org.hibernate.exception.LockAcquisitionException: could not lock: [tests.jpa.entity.lockings.LockedEntity#1]
		// Caused by: java.sql.SQLException: ORA-00054: resource busy and acquire with NOWAIT specified
		// LockModeType.WRITE uses this:
		// Hibernate: select id from LockedEntity where id =? for update nowait
//		em.lock(entity1, LockModeType.WRITE);
		
		// For LockModeType.READ if already locked by another transaction:
		// select id from LockedEntity where id =? for update
		// This will wait until the lock is released
		em.lock(entity1, LockModeType.READ);
		
		em.getTransaction().commit();
	}
	
	private static void testReadLockOnMicrosoftSqlServer() {
//		creation1();
		
		em.getTransaction().begin();
		
		LockedEntity entity1 = em.find(LockedEntity.class, 1L);
		
		// LockModeType.WRITE uses this:
		// Hibernate: select id from LockedEntity with (updlock, rowlock) where id =?
		// For LockModeType.WRITE if already locked by another transaction:
		// This will wait until the lock is released (no really difference between WRITE and READ)
//		em.lock(entity1, LockModeType.WRITE);
		
		// LockModeType.READ uses this:
		// Hibernate: select id from LockedEntity with (updlock, rowlock) where id =?
		// If LockModeType.READ and if already locked by another transaction:
		// This will wait until the lock is released
		em.lock(entity1, LockModeType.READ);
		
		em.getTransaction().commit();
	}
	
	private static void testReadLockOnPostgreSql() {
//		creation1();
		
		em.getTransaction().begin();
		
		LockedEntity entity1 = em.find(LockedEntity.class, 1L);
		
		// LockModeType.WRITE uses this:
		// Hibernate: select id from LockedEntity where id =? for update
		// For LockModeType.WRITE if already locked by another transaction:
		// waits until the locked rows are released
//		em.lock(entity1, LockModeType.WRITE);
		
		// LockModeType.READ uses this:
		// Hibernate: select id from LockedEntity where id =? for update
		// If LockModeType.READ and if already locked by another transaction:
		// waits until the locked rows are released
		em.lock(entity1, LockModeType.READ);
		
		em.getTransaction().commit();
	}
	
	private static void testReadLockOnMySql() {
//		creation1();
		
		em.getTransaction().begin();
		
		LockedEntity entity1 = em.find(LockedEntity.class, 1L);
		
		// LockModeType.WRITE uses this:
		// Hibernate: select id from LockedEntity where id =? for update
		// If LockModeType.WRITE and if already locked by another transaction:
		// waits until the locked rows are released
		em.lock(entity1, LockModeType.WRITE);
		
		// LockModeType.READ uses this:
		// Hibernate: select id from LockedEntity where id =? for update
		// If LockModeType.READ and if already locked by another transaction:
		// waits until the locked rows are released
//		em.lock(entity1, LockModeType.READ);
		
		em.getTransaction().commit();
	}
	
}
