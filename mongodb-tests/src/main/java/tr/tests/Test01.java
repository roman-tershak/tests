package tr.tests;

import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class Test01 {

	public static void main(String[] args) throws Exception {
//		test01();
		populateDbWithHugeCollectonOfDocs();
	}

	protected static void populateDbWithHugeCollectonOfDocs() throws Exception {
		getMongoCollection("testcolllarge").drop();
		DBCollection collection = getMongoCollection("testcolllarge");
		
		System.out.println(collection.count());
		
		for (int i = 0; i < 500000; i++) {
			DBObject dbObject = new BasicDBObject();
			dbObject.put("key1", i);
			dbObject.put("key2", "val_" + i);
			dbObject.put("timestamp", new Date());

			List<Integer> integers = new LinkedList<Integer>();
			for (int j = 0; j < 10; j++) {
				int r = (int) (Math.random() * 1000);
				integers.add(r);
			}
			dbObject.put("integers", integers);
			dbObject.put("integersList", integers.toString());
			
			BasicDBObject innerDbObject = new BasicDBObject();
			innerDbObject.put("k1", i * 872);
			innerDbObject.put("staticText", "Roman Tershak");
			innerDbObject.put("keyLong", System.currentTimeMillis());
			dbObject.put("innerObj", innerDbObject);
			
			StringBuffer sb = new StringBuffer(1000);
			for (int j = 0; j < 10000; j++) {
				char c = (char) (Math.random() * 96 + 32);
				sb.append(c);
			}
			dbObject.put("dummyText", sb.toString());
			
			collection.insert(dbObject);
			
			if (i % 100 == 0) {
				System.out.println("Inserted: " + i);
			}
		}
	}
	
	protected static void test01() throws Exception {
		DBCollection collection = getMongoCollection("testcoll");
		DBCursor cursor = collection.find();
		for (DBObject dbObject : cursor) {
			System.out.println(dbObject.toMap().toString());
		}
	}

	private static DBCollection getMongoCollection(String name) throws UnknownHostException {
		DB db = getMongoDB();
		DBCollection collection = db.getCollection(name);
		return collection;
	}

	private static DB getMongoDB() throws UnknownHostException {
		Mongo mongo = new Mongo("localhost:27017");
		DB db = mongo.getDB("test");
		return db;
	}

}
