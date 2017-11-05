package myjavabean.model;

public class EchartsRequest {
	private long id;
	private String sents;
	
	public EchartsRequest() {
		id = System.currentTimeMillis();
		sents = null;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSents() {
		return sents;
	}

	public void setSents(String sents) {
		this.sents = sents;
	}
	
}
