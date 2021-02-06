package cecs429.index;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cecs.util.Util;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.text.EnglishTokenStream;
import cecs429.text.ExtendedTokenProcessor;
import cecs429.text.TokenProcessor;
import edu.csulb.BetterTermDocumentIndexerWithPositionalInvertedIndex;
import edu.csulb.DiskIndexWriter;
import model.Doc;
import model.Feed;

public class BiwordIndexer {
	// Create a biword index
	public static BiwordIndex biwordIndexCorpus(DocumentCorpus corpus) {
		long start = System.currentTimeMillis();
		HashSet<String> vocabulary = new HashSet<>();
		TokenProcessor processor = new ExtendedTokenProcessor();
		Iterable<Document> documents = corpus.getDocuments();

		// Create index for all the documents
		BiwordIndex index = new BiwordIndex();
		HashSet<String> vocab = new HashSet<>();
		documents.forEach(document -> {
			EnglishTokenStream englishTokenStream = new EnglishTokenStream(document.getContent());
			Iterable<String> tokens = englishTokenStream.getTokens();
			Iterator<String> iter = tokens.iterator();
			List<String> t = new ArrayList<>();
			while (iter.hasNext()) {
				t.add(iter.next());
			}
			for (int i = 0; i < t.size() - 1; i++) {
				List<String> p1 = processor.processToken(t.get(i), false);
				List<String> p2 = processor.processToken(t.get(i + 1), false);
				String myterm = Util.stemWord(p1.get(0)).trim() + " " + Util.stemWord(p2.get(0)).trim();
				index.addTerm(myterm, document.getId());
				vocab.add(myterm);
			}

		});
		index.setmVocabulary(new ArrayList<>(vocabulary));
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000 + " seconds to biword index.");
		DiskIndexWriter.createBiwordIndexFile(index.getInvertedIndexMap(), BetterTermDocumentIndexerWithPositionalInvertedIndex.biwordIndexFile);
		return index;
	}

	// Create a biword index from the xml files
	public static BiwordIndex biwordXmlIndexCorpus(Feed feed) {
		long start = System.currentTimeMillis();
		HashSet<String> vocabulary = new HashSet<>();
		TokenProcessor processor = new ExtendedTokenProcessor();
		Iterable<Doc> documents = feed.getDoc();
		// Create the vocabulary
		List<Doc> docs = new ArrayList<>();
		documents.forEach(document -> {
			docs.add(document);
		});

		// Create index for all the documents
		BiwordIndex index = new BiwordIndex();
		HashSet<String> vocab = new HashSet<>();
		for (int i = 0; i < docs.size(); i++) {
			EnglishTokenStream englishTokenStream = new EnglishTokenStream(new StringReader(docs.get(i).getContent()));
			Iterable<String> tokens = englishTokenStream.getTokens();
			Iterator<String> iter = tokens.iterator();
			List<String> t = new ArrayList<>();
			while (iter.hasNext()) {
				t.add(iter.next());
			}
			for (int j = 0; j < t.size() - 1; j++) {
				List<String> p1 = processor.processToken(t.get(j), false);
				List<String> p2 = processor.processToken(t.get(j + 1), false);
				String myterm = Util.stemWord(p1.get(0)) + " " + Util.stemWord(p2.get(0));
				index.addTerm(myterm, i);
				vocab.add(myterm);
			}
			try {
				englishTokenStream.close();
			} catch (IOException e) {
			}
		}
		index.setmVocabulary(new ArrayList<>(vocabulary));
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000 + " seconds to biword index.");
		System.out.println("Saving biword file.");
		DiskIndexWriter.createBiwordIndexFile(BiwordIndex.getInvertedIndexMap(), BetterTermDocumentIndexerWithPositionalInvertedIndex.biwordIndexFile);
		System.out.println("Saved biword file.");
		return index;
	}
}
