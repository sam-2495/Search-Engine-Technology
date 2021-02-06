package cecs.model;


public class MyDocument {
	private String body;
	private String url;
	private String title;
	private String author;

	public String getBody() {
		return body;
	}

	public MyDocument() {
		super();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "MyDocument [body=" + body + ", url=" + url + ", title=" + title + ", author=" + author + "]";
	}

	public MyDocument(String body, String url, String title, String author) {
		super();
		this.body = body;
		this.url = url;
		this.title = title;
		this.author = author;
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
