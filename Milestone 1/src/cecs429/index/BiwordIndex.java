package cecs429.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Class to create the Biword Index
public class BiwordIndex {

    //Map to store the index
    private static Map<String, Set<Integer>> invertedIndexMap;

    //List for the biword vocabulary
    private static List<String> mVocabulary;

    public BiwordIndex(Collection<String> vocabulary) {
        invertedIndexMap = new HashMap<>();
        mVocabulary = new ArrayList<>();
        mVocabulary.addAll(vocabulary);
        Collections.sort(mVocabulary);
    }

    public BiwordIndex() {
        invertedIndexMap = new HashMap<>();
    }

    public void setmVocabulary(List<String> mVocabulary) {
        BiwordIndex.mVocabulary = mVocabulary;
    }

    public static Set<Integer> getPostings(String term) {
        return invertedIndexMap.get(term);
    }

    public List<String> getVocabulary() {
        return mVocabulary;
    }

    /**
     * Associates the given documentId with the given biword in the index.
     */
    public void addTerm(String myterm, int documentId) {

        if (invertedIndexMap.keySet().contains(myterm)) {
            Set<Integer> currentSet = invertedIndexMap.get(myterm);
            currentSet.add(documentId);
        } else {
            Set<Integer> s = new HashSet<>();
            s.add(documentId);
            invertedIndexMap.put(myterm, s);
        }
    }

}
