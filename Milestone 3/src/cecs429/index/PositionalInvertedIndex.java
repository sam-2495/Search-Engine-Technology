package cecs429.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Class to store the positional inverted index
public class PositionalInvertedIndex implements Index {
	// Map to store the index
	private final Map<String, List<Posting>> positionalInvertedIndexMap;

	// List to store the vocabulary
	private List<String> mVocabulary;

	public PositionalInvertedIndex(Collection<String> vocabulary) {
		positionalInvertedIndexMap = new HashMap<>();
		mVocabulary = new ArrayList<>();
		mVocabulary.addAll(vocabulary);
	}

	public PositionalInvertedIndex() {
		positionalInvertedIndexMap = new HashMap<>();
	}

	public List<String> getmVocabulary() {
		return mVocabulary;
	}

	public void setmVocabulary(List<String> mVocabulary) {
		this.mVocabulary = mVocabulary;
	}

	// Return the postings of a term
	@Override
	public List<Posting> getPostings(String term) {
		if (positionalInvertedIndexMap.containsKey(term)) {
			return new ArrayList<>(positionalInvertedIndexMap.get(term));
		} else {
			return new ArrayList<>();
		}
	}

	// Return the vocabulary
	@Override
	public List<String> getVocabulary() {
		return mVocabulary;
	}

	// Associate the term with the document and positions in the index map
	public void addTerm(String term, int documentId, int position, double wdt) {
		if (positionalInvertedIndexMap.containsKey(term)) {
			List<Posting> currentPostings = positionalInvertedIndexMap.get(term);
			Posting lastPosting = currentPostings.get(currentPostings.size() - 1);
			if (lastPosting.getDocumentId() == documentId) {
				lastPosting.getPositions().add(position);
			} else {
				Posting posting = new Posting(documentId);
				posting.setWdt(wdt);
				List<Integer> positions = new ArrayList<>();
				positions.add(position);
				posting.setPositions(positions);
				currentPostings.add(posting);
			}
		} else {
			List<Posting> postings = new ArrayList<>();
			Posting posting = new Posting(documentId);
			posting.setWdt(wdt);
			List<Integer> positions = new ArrayList<>();
			positions.add(position);
			posting.setPositions(positions);
			postings.add(posting);
			positionalInvertedIndexMap.put(term, postings);
		}
	}

	@Override
	public List<Posting> getPostingsWithoutPositions(String term) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Integer> getSoundex(String term) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Integer> getBiwordPostings(String term) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Map<String, List<Posting>> getPositionalInvertedIndexMap() {
		return positionalInvertedIndexMap;
	}
}
