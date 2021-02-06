package model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "feed")
public class Feed {
	@XmlElement
	private List<Doc> doc;

	public List<Doc> getDoc() {
		return doc;
	}

	public void setDoc(List<Doc> doc) {
		this.doc = doc;
	}

	@Override
	public String toString() {
		return "[doc = " + doc.toString() + "]";
	}
}