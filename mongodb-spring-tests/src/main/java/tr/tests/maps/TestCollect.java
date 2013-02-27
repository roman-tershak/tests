package tr.tests.maps;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="test_collect")
public class TestCollect {

	@Id
	private ObjectId id;
	private int i;
	private String str;
	private Map<String, Object> params;
	
	public TestCollect(int i, String str, Map<String, Object> params) {
		this.i = i;
		this.str = str;
		this.params = params;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	@Override
	public String toString() {
		return "TestCollect [id=" + id + ", i=" + i + ", str=" + str + ", params=" + params + "]";
	}
}
