package tr.tests;

public class InnerObject {
	
	private int k1;
	private String staticText;
	private Long keyLong;
	
	public InnerObject(int k1, String staticText, Long keyLong) {
		this.k1 = k1;
		this.staticText = staticText;
		this.keyLong = keyLong;
	}

	public int getK1() {
		return k1;
	}
	public void setK1(int k1) {
		this.k1 = k1;
	}

	public String getStaticText() {
		return staticText;
	}
	public void setStaticText(String staticText) {
		this.staticText = staticText;
	}

	public Long getKeyLong() {
		return keyLong;
	}
	public void setKeyLong(Long keyLong) {
		this.keyLong = keyLong;
	}

	@Override
	public String toString() {
		return "InnerObject [k1=" + k1 + ", staticText=" + staticText + ", keyLong=" + keyLong + "]";
	}
}
