package cecs429.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Collections.sort(mVocabulary);
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
        return positionalInvertedIndexMap.get(term);
    }

    // Return the vocabulary
    @Override
    public List<String> getVocabulary() {
        return mVocabulary;
    }

    // Associate the term with the document and positions in the index map
    public void addTerm(String term, int documentId, int position) {
        if (positionalInvertedIndexMap.containsKey(term)) {
            List<Posting> currentPostings = positionalInvertedIndexMap.get(term);
            Posting lastPosting = currentPostings.get(currentPostings.size() - 1);
            if (lastPosting.getDocumentId() == documentId) {
                lastPosting.getPositions().add(position);
            } else {
                Posting posting = new Posting();
                posting.setmDocumentId(documentId);
                List<Integer> positions = new ArrayList<>();
                positions.add(position);
                posting.setPositions(positions);
                currentPostings.add(posting);
            }
        } else {
            List<Posting> postings = new ArrayList<>();
            Posting posting = new Posting();
            posting.setmDocumentId(documentId);
            List<Integer> positions = new ArrayList<>();
            positions.add(position);
            posting.setPositions(positions);
            postings.add(posting);
            positionalInvertedIndexMap.put(term, postings);
        }
    }
}
