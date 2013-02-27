package tr.tests;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="testcolllarge")
public class TestCollLarge {

	@Id 
	private String id;
	private int key1;
	private String key2;
	private Date timestamp;
	private List<Integer> integers;
	@Field(value="innerObj")
	private InnerObject innerObject;
	
	public TestCollLarge(int key1, String key2, Date timestamp) {
		this.key1 = key1;
		this.key2 = key2;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getKey1() {
		return key1;
	}
	public void setKey1(int key1) {
		this.key1 = key1;
	}

	public String getKey2() {
		return key2;
	}
	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public List<Integer> getIntegers() {
		return integers;
	}
	public void setIntegers(List<Integer> integers) {
		this.integers = integers;
	}

	public InnerObject getInnerObject() {
		return innerObject;
	}
	public void setInnerObject(InnerObject innerObject) {
		this.innerObject = innerObject;
	}
	
	@Override
	public String toString() {
		return "TestCollLarge [id=" + id + ", key1=" + key1 + ", key2=" + key2 + ", timestamp=" + timestamp
				+ ", integers=" + integers + ", innerObject=" + innerObject + "]";
	}

}
