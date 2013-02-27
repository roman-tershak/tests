package tr.tests.maps;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.Mongo;

public class TestMain {

	public static void main(String[] args) throws Exception {
		MongoTemplate mongoOps = new MongoTemplate(new Mongo(), "test");
		
//		HashMap<String, Object> params = new HashMap<String, Object>();
//		params.put("key1", "value1a");
//		params.put("key2", "value2a");
//		mongoOps.insert(new TestCollect(78, "test2", params));
		
//		Query query = new Query(Criteria.where("i").is(77));
		Query query = new Query();
//		Query query = new Query(Criteria.where("integers").all(433, 117, 199));
		List<TestCollect> objs = mongoOps.find(query, TestCollect.class);
		for (TestCollect obj : objs) {
			System.out.println(obj);
		}
		
		System.out.println("Done");
	}
}
