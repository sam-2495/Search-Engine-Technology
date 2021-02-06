package cecs429.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cecs.model.DocWeight;
import cecs.util.Util;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.text.EnglishTokenStream;
import cecs429.text.ExtendedTokenProcessor;
import cecs429.text.Soundex;
import cecs429.text.TokenProcessor;
import edu.csulb.BetterTermDocumentIndexerWithPositionalInvertedIndex;
import edu.csulb.DiskIndexWriter;

public class Indexer {
	public static Index indexCorpus(DocumentCorpus corpus) throws IOException {
		long start = System.currentTimeMillis();
		HashSet<String> vocabulary = new HashSet<>();
		TokenProcessor processor = new ExtendedTokenProcessor();
		Iterable<Document> documents = corpus.getDocuments();
		// Create the vocabulary
		PositionalInvertedIndex index = new PositionalInvertedIndex();
		List<DocWeight> documentWeights = new LinkedList<>();
		Map<String, List<Integer>> soundexMap = new HashMap<>();
		int docLengthA = 0;
		Iterator<Document> it = documents.iterator();
		int docCount = 0;
		while (it.hasNext()) {
			docCount++;
			Document document = it.next();
			DocWeight docWeight = new DocWeight();
			Map<String, Integer> termWeight = new HashMap<>();
			EnglishTokenStream englishTokenStream = new EnglishTokenStream(document.getContent());
			Iterable<String> tokens = englishTokenStream.getTokens();
			Iterator<String> iter = tokens.iterator();
			int position = 0;
			int docSize = 0;
			while (iter.hasNext()) {
				String token = iter.next();
				List<String> processedTokens = processor.processToken(token, true);
				vocabulary.addAll(processedTokens);
				if (termWeight.keySet().contains(token)) {
					termWeight.put(token, termWeight.get(token) + 1);
				} else {
					termWeight.put(token, 1);
				}
				for (String processedToken : processedTokens) {
					double wdt = 1 + Math.log(termWeight.get(token));
					index.addTerm(processedToken, document.getId(), position, wdt);
				}
				position++;
				docSize = docSize + token.length() * 2;
			}
			docWeight.setDocLength(position);
			docWeight.setByteSize(docSize);
			double tfidfSum = 0;
			try {
				englishTokenStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			double temp = 0.0;
			double wdt = 0.0;
			for (Entry<String, Integer> entry : termWeight.entrySet()) {
				tfidfSum += entry.getValue();
				wdt = 1 + Math.log(entry.getValue());
				temp = temp + Math.pow(wdt, 2);
			}
			docLengthA += tfidfSum;
			docWeight.setAvgtftd(tfidfSum / termWeight.size());
			docWeight.setLd(Math.sqrt(temp));
			documentWeights.add(docWeight);
			// Create soundex Map
			/*
			 * Soundex soundex = new Soundex(); if (document.getAuthor() != null) { String
			 * author = document.getAuthor(); System.out.println(author); for (String word :
			 * author.split(",")) { if (!word.contains(".")){ String s =
			 * soundex.getSoundex(Util.stemWord(word)); if (soundexMap.keySet().contains(s))
			 * { soundexMap.get(s).add(document.getId()); } else { List<Integer> docIds =
			 * new ArrayList<>(); docIds.add(document.getId()); soundexMap.put(s, docIds); }
			 * } } }
			 */
		}
		index.setmVocabulary(new ArrayList<String>(vocabulary));
		long end = System.currentTimeMillis();
		System.out
				.println((end - start) / 1000 + BetterTermDocumentIndexerWithPositionalInvertedIndex.SECONDS_TO_INDEX);
		return saveFiles(index, documentWeights, soundexMap, docLengthA, docCount);
	}

	private static Index saveFiles(PositionalInvertedIndex index, List<DocWeight> documentWeights,
			Map<String, List<Integer>> soundexMap, int docLengthA, int docCount) throws IOException {
		System.out.println("Saving index file.");
		DiskIndexWriter.createBinaryFile(index, BetterTermDocumentIndexerWithPositionalInvertedIndex.diskIndexPath);
		System.out.println("Saved index file.");
		index.getPositionalInvertedIndexMap().clear();
		System.out.println("Saving docWeights file.");
		DiskIndexWriter.createDocumentsWeightFile(documentWeights, docLengthA / docCount,
				BetterTermDocumentIndexerWithPositionalInvertedIndex.docWeightsFile);
		System.out.println("Saved docWeights file.");
		if (soundexMap.size() != 0) {
			System.out.println("Saving soundex file.");
			DiskIndexWriter.createSoundexFile(soundexMap,
					BetterTermDocumentIndexerWithPositionalInvertedIndex.soundexFile);
			System.out.println("Saved soundex file.");
		}
		return index;
	}

}
