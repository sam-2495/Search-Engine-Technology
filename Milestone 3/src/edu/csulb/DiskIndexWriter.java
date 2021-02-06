package edu.csulb;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cecs.model.DocWeight;
import cecs429.database.DatabaseUtil;
import cecs429.index.Index;
import cecs429.index.Posting;

public class DiskIndexWriter {
	// Write the Postings on disk.
	public static void createBinaryFile(Index index, String fileName) throws IOException {
		Map<String, Integer> indexMap = new LinkedHashMap<>();
		FileOutputStream file = new FileOutputStream(fileName);
		DataOutputStream data = new DataOutputStream(file);
		List<String> vocab = index.getVocabulary();
		Collections.sort(vocab);
		int currentIndexBytes = 0;
		for (int j = 0; j < vocab.size(); j++) {
			String term = vocab.get(j);
			if (!term.equalsIgnoreCase("")) {
				List<Posting> termPosting = index.getPostings(term);
				termPosting.sort(Posting.wdtComparator);
				data.writeInt(termPosting.size());
				for (int k = 0; k < termPosting.size(); k++) {
					Posting posting = termPosting.get(k);
					indexMap.put(term, currentIndexBytes);
					try {
						if (k == 0) {
							data.writeInt(posting.getDocumentId());
						} else {
							data.writeInt(posting.getDocumentId() - termPosting.get(k - 1).getDocumentId());
						}
						List<Integer> positions = posting.getPositions();
						data.writeInt(positions.size());
						// Storing data on disk using GAPS
						List<Integer> positionGaps = new ArrayList<>();
						positionGaps.add(positions.get(0));
						for (int i = 0; i < positions.size() - 1; i++) {
							positionGaps.add(positions.get(i) + 1 - positions.get(i));
						}
						Iterator<Integer> iter = positionGaps.iterator();
						while (iter.hasNext()) {
							data.writeInt(iter.next());
						}
					} catch (IOException e) {
					}
				}
				currentIndexBytes = data.size();
			}
		}
		data.close();
		// Storing the byte position of the terms to database.
		DatabaseUtil.saveTermIndexToDB(indexMap);
	}

	public static void createDocumentsWeightFile(List<DocWeight> docWeights, double doclengthA, String filepath) {
		Map<Integer, Integer> indexMap = new HashMap<>();
		FileOutputStream file = null;
		DataOutputStream data = null;
		try {
			file = new FileOutputStream(filepath);
			data = new DataOutputStream(file);
			for (int i = 0; i < docWeights.size(); i++) {
				indexMap.put(i, data.size());
				DocWeight docWeight = docWeights.get(i);
				data.writeDouble(docWeight.getLd());
				data.writeDouble(docWeight.getDocLength());
				data.writeDouble(docWeight.getByteSize());
				data.writeDouble(docWeight.getAvgtftd());
			}
			data.writeDouble(doclengthA);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to create file");
		} catch (IOException e) {
			System.out.println("Unable to write data to file");
		}
		try {
			if (data != null)
				data.close();
			if (file != null)
				file.close();
		} catch (IOException e) {
			System.out.println("Doc Weights file already closed");
		}
		DatabaseUtil.saveDocWeightsToDB(indexMap);
	}

	public static void createSoundexFile(Map<String, List<Integer>> soundexMap, String filepath) {
		Map<String, Integer> indexMap = new HashMap<>();
		FileOutputStream file = null;
		DataOutputStream data = null;
		try {
			file = new FileOutputStream(filepath);
			data = new DataOutputStream(file);
			for (Map.Entry<String, List<Integer>> entry : soundexMap.entrySet()) {
				indexMap.put(entry.getKey(), data.size());
				List<Integer> docIds = entry.getValue();
				data.writeInt(docIds.size());
				for (int i = 0; i < docIds.size(); i++) {
					if (i == 0) {
						data.writeInt(docIds.get(i));
					} else {
						data.writeInt(docIds.get(i) - docIds.get(i - 1));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Some error occured.");
		}
		try {
			data.close();
			file.close();
		} catch (IOException e) {
			System.out.println("Soundex file already closed");
		}

		DatabaseUtil.saveSoundexToDB(indexMap);
	}

	public static void createBiwordIndexFile(Map<String, Set<Integer>> biwordMap, String filepath) {
		Map<String, Integer> indexMap = new HashMap<>();
		FileOutputStream file = null;
		DataOutputStream data = null;
		try {
			file = new FileOutputStream(filepath);
			data = new DataOutputStream(file);
			for (Map.Entry<String, Set<Integer>> entry : biwordMap.entrySet()) {
				indexMap.put(entry.getKey(), data.size());
				List<Integer> docIds = new ArrayList<>(entry.getValue());
				data.writeInt(docIds.size());
				for (int i = 0; i < docIds.size(); i++) {
					if (i == 0) {
						data.writeInt(docIds.get(i));
					} else {
						data.writeInt(docIds.get(i) - docIds.get(i - 1));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Some error occured.");
		}
		try {
			data.close();
			file.close();
		} catch (IOException e) {
			System.out.println("Biword index file already closed");
		}
		DatabaseUtil.saveBiwordIndexToDB(indexMap);
	}
}
