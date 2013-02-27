package tr.tests;

import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class TestLoad01 {

	public static void main(String[] args) throws UnknownHostException, MongoException {
		final MongoTemplate mongoOps = new MongoTemplate(new Mongo(), "test");
//		Query query = new Query(Criteria.where("integers").all(433, 117, 199));

		Thread[] threads = new Thread[50];
		
		final AtomicInteger counter = new AtomicInteger(0);
		final AtomicInteger alives = new AtomicInteger(threads.length);
		final AtomicBoolean stop = new AtomicBoolean(false);
		
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread() {
				public void run() {
					try {
						while (!stop.get()) {
							int key = (int) (Math.random() * 500000);
							Query query = new Query(Criteria.where("key1").is(key));
							
							TestCollLarge obj = mongoOps.findOne(query, TestCollLarge.class);
//							for (TestCollLarge testCollLarge : objs) {
//							System.out.println(this.getName() + ", counter=" + counter.getAndIncrement() + ", key=" + key);
							int loccou = counter.getAndIncrement();
							if (loccou % 1000 == 0) {
								System.out.println(MessageFormat.format("{0}, counter={1}, key={2}, obj={3}", 
										this.getName(), loccou, key, obj));
							}
//							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						alives.decrementAndGet();
					}
				};
			};
		}

		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//			}
		}
		
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
		}
		
		stop.set(true);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		System.out.println("Done, alives=" + alives + ", counter=" + counter);
	}

}
