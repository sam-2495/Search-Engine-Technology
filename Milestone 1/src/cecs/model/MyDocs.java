package cecs.model;


import java.util.List;

public class MyDocs {
	private List<MyDocument> documents;

	@Override
	public String toString() {
		return "File [documents=" + documents + "]";
	}

	public MyDocs() {
		super();
	}

	public MyDocs(List<MyDocument> documents) {
		super();
		this.documents = documents;
	}

	public List<MyDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<MyDocument> documents) {
		this.documents = documents;
	}
}
