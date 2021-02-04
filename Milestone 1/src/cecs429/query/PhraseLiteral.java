package cecs429.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cecs429.index.BiwordIndex;
import cecs429.index.Index;
import cecs429.index.Posting;
import edu.csulb.BetterTermDocumentIndexerWithPositionalInvertedIndex;

/**
 * Represents a phrase literal consisting of one or more terms that must occur
 * in sequence.
 */
public class PhraseLiteral implements Query {
    // The list of individual terms in the phrase.
    private List<String> mTerms = new ArrayList<>();

    /**
     * Constructs a PhraseLiteral with the given individual phrase terms.
     */
    public PhraseLiteral(List<String> terms) {
        mTerms.addAll(terms);
    }

    /**
     * Constructs a PhraseLiteral given a string with one or more individual terms
     * separated by spaces.
     */
    public PhraseLiteral(String terms) {
        mTerms.addAll(Arrays.asList(terms.split(" ")));
    }

    @Override
    public List<Posting> getPostings(Index index) {
        boolean d = false;
        if (!mTerms.stream().filter(t -> t.indexOf('(') > -1).collect(Collectors.toList()).isEmpty())
            d = true;
        boolean wcl = false;
        for (String mTerm : mTerms) {
            if (mTerm.contains("*")) {
                wcl = true;
            }
        }
        if (mTerms.size() == 2 && !d && !wcl) {
            String myterm = mTerms.get(0).trim() + " " + mTerms.get(1).trim();
            List<Posting> postings = new ArrayList<>();
            Set<Integer> p = BiwordIndex.getPostings(myterm);
            if (p != null && !p.isEmpty()) {
                p.forEach(i -> {
                    postings.add(new Posting(i));
                });
            }
            return postings;
        } else if (d) {
            int indexDNF = -1;
            for (int i = 0; i < mTerms.size(); i++) {
                if (mTerms.get(i).contains("(")) {
                    indexDNF = i;
                    break;
                }
            }
            String[] words = mTerms.get(indexDNF).replaceAll("[()]", "").split("[+]");
            List<String> strings = new ArrayList<>();
            for (int i = 0; i < words.length; i++) {
                StringBuilder s = new StringBuilder();

                for (int j = 0; j < mTerms.size(); j++) {
                    if (j != indexDNF) {
                        s.append(" ").append(mTerms.get(j));
                    } else {
                        s.append(" ").append(words[i]);
                    }
                }
                String ss = new String(s);
                strings.add("\"" + ss.trim() + "\"");
            }

            String f = String.join("+", strings);
            BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
            Query obj = booleanQueryParser.parseQuery(f);
            return obj.getPostings(index);
        } else {
            List<Posting> finalPostings = new ArrayList<>();
            List<Posting> result = new ArrayList<>();
            for (int iter = 0; iter < mTerms.size(); iter++) {
                String t = mTerms.get(iter);
                List<Posting> termPostings = new ArrayList<>();
                if (t.contains("*")) {
                    BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
                    Query obj = booleanQueryParser.parseQuery(t);
                    termPostings = obj.getPostings(index);
                } else {
                    termPostings = index.getPostings(t);
                }
                Collections.sort(termPostings);
                if (result.isEmpty()) {
                    finalPostings.addAll(termPostings);
                } else {
                    int i = 0;
                    int j = 0;
                    while (i < result.size() && j < termPostings.size()) {
                        if (result.get(i).getDocumentId() == termPostings.get(j).getDocumentId()) {
                            List<Integer> p1 = result.get(i).getPositions();
                            List<Integer> p2 = termPostings.get(j).getPositions();
                            int a = 0;
                            int b = 0;
                            Posting posting = new Posting(result.get(i).getDocumentId());
                            List<Integer> pos = new ArrayList<>();
                            while (a < p1.size() && b < p2.size()) {
                                if (p1.get(a) + iter == p2.get(b)) {
                                    pos.add(p1.get(a));
                                    a++;
                                    b++;
                                } else if (p1.get(a) + 1 < p2.get(b)) {
                                    a++;
                                } else if (p1.get(a) + 1 > p2.get(b)) {
                                    b++;
                                }
                            }
                            posting.setPositions(pos);
                            i++;
                            j++;
                            finalPostings.add(posting);
                        } else if (result.get(i).getDocumentId() < termPostings.get(j).getDocumentId()) {
                            i++;
                        } else if (result.get(i).getDocumentId() > termPostings.get(j).getDocumentId()) {
                            j++;
                        }
                    }
                }
                result.clear();
                result.addAll(finalPostings);
                finalPostings.clear();
            }
//		}
            return result;
        }
    }

    @Override
    public String toString() {
        return "\"" + String.join(" ", mTerms) + "\"";
    }
    /*
     * public static void main(String[] args) { String s = "explore+national";
     * System.out.println(s.split("[+]")[0]); }
     */
}
