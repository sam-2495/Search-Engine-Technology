package cecs429.index;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cecs429.text.EnglishTokenStream;
import cecs429.text.ExtendedTokenProcessor;
import cecs429.text.TokenProcessor;
import edu.csulb.BetterTermDocumentIndexerWithPositionalInvertedIndex;
import model.Doc;
import model.Feed;

public class XMLIndexer {
	/*
	 * public static Index indexXmlCorpus(Feed feed) { String folderName =
	 * "Output XML Files"; Path path = Paths.get(folderName); try {
	 * Files.createDirectories(path); } catch (IOException e1) {
	 * e1.printStackTrace(); } long start = System.currentTimeMillis();
	 * TokenProcessor processor = new ExtendedTokenProcessor(); Iterable<Doc>
	 * documents = feed.getDoc(); List<Doc> docs = new ArrayList<>();
	 * documents.forEach(document -> { docs.add(document); });
	 * 
	 * // Create index for all the documents PositionalInvertedIndex index = new
	 * PositionalInvertedIndex(); HashSet<String> vocab = new HashSet<>(); for (int
	 * i = 0; i < docs.size(); i++) { EnglishTokenStream englishTokenStream = new
	 * EnglishTokenStream(new StringReader(docs.get(i).getContent()));
	 * Iterable<String> tokens = englishTokenStream.getTokens(); Iterator<String>
	 * iter = tokens.iterator(); int position = 1; while (iter.hasNext()) {
	 * List<String> processedTokens = processor.processToken(iter.next(), true); for
	 * (String processedToken : processedTokens) { index.addTerm(processedToken, i,
	 * position); vocab.add(processedToken); } position++; } try {
	 * englishTokenStream.close(); } catch (IOException e) { e.printStackTrace(); }
	 * System.out.println(docs.get(i).getTitle()); } index.setmVocabulary(new
	 * ArrayList<>(vocab)); long end = System.currentTimeMillis(); System.out
	 * .println((end - start) / 1000 +
	 * BetterTermDocumentIndexerWithPositionalInvertedIndex.SECONDS_TO_INDEX);
	 * return index; }
	 */}
