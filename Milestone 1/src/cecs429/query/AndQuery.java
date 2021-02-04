package cecs429.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import cecs429.index.Index;
import cecs429.index.Posting;

/**
 * An AndQuery composes other Query objects and merges their postings in an
 * intersection-like operation.
 */
public class AndQuery implements Query {
    private List<Query> mChildren;

    public AndQuery(Iterable<Query> children) {
        mChildren = new ArrayList<>();
        Iterator<Query> iter = children.iterator();
        while (iter.hasNext()) {
            mChildren.add(iter.next());
        }
    }

    @Override
    public List<Posting> getPostings(Index index) {
        List<Posting> finalPostings = new ArrayList<>();
        List<Posting> result = new ArrayList<>();


        for (Query query : mChildren) {
            List<Posting> postings = query.getPostings(index);
            if (postings != null) {
                Collections.sort(postings);
                if (!result.isEmpty()) {
                    int i = 0;
                    int j = 0;
                    while (i < result.size() && j < postings.size()) {
                        if (result.get(i).getDocumentId() == postings.get(j).getDocumentId()) {
                            Posting posting = new Posting();
                            posting.setmDocumentId(result.get(i).getDocumentId());
                            List<Integer> pos = new ArrayList<>();
                            pos.addAll(result.get(i).getPositions());
                            pos.addAll(postings.get(j).getPositions());
                            Collections.sort(pos);
                            posting.setPositions(pos);
                            finalPostings.add(posting);
                            i++;
                            j++;
                        } else if (result.get(i).getDocumentId() < postings.get(j).getDocumentId()) {
                            i++;
                        } else if (result.get(i).getDocumentId() > postings.get(j).getDocumentId()) {
                            j++;
                        }
                    }
                } else {
                    finalPostings.addAll(postings);
                }
                result.clear();
                result.addAll(finalPostings);
                finalPostings.clear();
            } else {
                List<Posting> blank = new ArrayList<Posting>();
                return blank;
            }
        }
        return result;
    }
    @Override
    public String toString() {
        return String.join(" ", mChildren.stream().map(c -> c.toString()).collect(Collectors.toList()));
    }
}
