package cecs429.documents;

import java.io.Reader;
import java.nio.file.Path;

/**
 * Represents a document in an index.
 */
public interface Document {
	/**
	 * The ID used by the index to represent the document.
	 */
	int getId();
	
	/**
	 * Gets a stream over the content of the document.
	 */
	Reader getContent();
	
	/**
	 * The title of the document, for displaying to the user.
	 */
	String getTitle();
	
	String getAuthor();
	/**
	 * The absolute path to the document's file.
	 */
	Path getFilePath();
}
