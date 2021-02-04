package cecs.jaxb;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import model.Doc;
import model.Feed;

public class XmlToObject {

	// To get XML data into the object from a XML File
	public Feed getDataFromXMLFile(String filePath) {
		try {
			File file = new File(filePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(Feed.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Feed feed = (Feed) jaxbUnmarshaller.unmarshal(file);
			return feed;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String marshalMessage(Doc doc) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Doc.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter writer = new StringWriter();
		try {
			jaxbMarshaller.marshal(doc, writer);
			return writer.toString();
		} catch (JAXBException e) {
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}