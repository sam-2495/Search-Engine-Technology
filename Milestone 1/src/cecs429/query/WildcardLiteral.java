package cecs429.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import cecs429.index.Index;
import cecs429.index.KGramIndex;
import cecs429.index.Posting;
import edu.csulb.BetterTermDocumentIndexerWithPositionalInvertedIndex;

public class WildcardLiteral implements Query {
    private String query;

    // Method to return the postings of a wildcard literal using the kgram index
    @Override
    public List<Posting> getPostings(Index index) {
        KGramIndex kGramIndex = BetterTermDocumentIndexerWithPositionalInvertedIndex.kGramIndex;
        String b = new String(query).replace("*", "(.*)");
        String[] qs = query.split("\\*");
        if (!query.startsWith("*")) {
            qs[0] = "$" + qs[0];
        }
        if (!query.endsWith("*")) {
            qs[qs.length - 1] = qs[qs.length - 1] + "$";
        }
        List<String> kgrams = new ArrayList<>();
        for (int i = 0; i < qs.length; i++) {
            for (int j = 1; j <= qs[i].length(); j++) {
                kgrams.addAll(kGramIndex.generateKGrams(j, qs[i]));
            }
        }
        PriorityQueue<String> postings = new PriorityQueue<>();
        for (int i = 0; i < kgrams.size(); i++) {
            PriorityQueue<String> newPostings = kGramIndex.getPostings(kgrams.get(i));
            if (newPostings != null && !newPostings.isEmpty()) {
                if (!postings.isEmpty()) {
                    postings = KGramIndex.intersect(postings, newPostings);
                } else {
                    postings.addAll(newPostings);
                }
            }
        }
        LinkedList<String> finalPostings = KGramIndex.postFilter(postings, b);
        List<Query> list = new ArrayList<>();
        finalPostings.forEach(posting -> list.add(new TermLiteral(posting)));
        Query q = new OrQuery(list);
        return q.getPostings(index);
    }

    public WildcardLiteral(String q) {
        query = q;
    }

    public List<String> generateKGrams(int k, String token) {
        List<String> grams = new LinkedList<>();
        for (int i = 0; i <= token.length() - k; i++) {
            String kGram = token.substring(i, i + k);
            grams.add(kGram);
        }
        return grams;
    }

}
