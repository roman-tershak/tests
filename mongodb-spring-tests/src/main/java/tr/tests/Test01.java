package tr.tests;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.Mongo;

public class Test01 {

	public static void main(String[] args) throws Exception {
		MongoTemplate mongoOps = new MongoTemplate(new Mongo(), "test");
		Query query = new Query(Criteria.where("key1").is(77));
//		Query query = new Query(Criteria.where("integers").all(433, 117, 199));
		List<TestCollLarge> objs = mongoOps.find(query, TestCollLarge.class);
		
		for (TestCollLarge testCollLarge : objs) {
			System.out.println(testCollLarge);
		}
		
		System.out.println("Done");
	}

}
