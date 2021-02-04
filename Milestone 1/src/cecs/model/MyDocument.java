package cecs.model;


public class MyDocument {
	private String body;
	private String url;
	private String title;

	public String getBody() {
		return body;
	}

	public MyDocument() {
		super();
	}

	@Override
	public String toString() {
		return "Document [body=" + body + ", url=" + url + ", title=" + title + "]";
	}

	public MyDocument(String body, String url, String title) {
		super();
		this.body = body;
		this.url = url;
		this.title = title;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
