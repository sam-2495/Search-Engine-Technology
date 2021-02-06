package cecs429.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import cecs429.index.Posting;

/**
 * An OrQuery composes other Query objects and merges their postings with a
 * union-type operation.
 */
public class OrQuery implements Query {
    // The components of the Or query.
    private List<Query> mChildren;

    public OrQuery(Iterable<Query> children) {
        mChildren = new ArrayList<>();
        Iterator<Query> iter = children.iterator();
        while (iter.hasNext()) {
            mChildren.add(iter.next());
        }
    }

    @Override
    public List<Posting> getPostings() {
        List<Posting> finalPostings = new ArrayList<>();
        List<Posting> result = new ArrayList<>();

        // TODO: program the merge for an OrQuery, by gathering the postings of the
        // composed Query children and
        // unioning the resulting postings.
        mChildren.forEach(query -> {
            List<Posting> postings = query.getPostings();
            if (postings != null && !postings.isEmpty()) {
//			Collections.sort(result);
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
                            Posting posting = new Posting();
                            posting.setmDocumentId(result.get(i).getDocumentId());
                            List<Integer> pos = new ArrayList<>();
                            pos.addAll(result.get(i).getPositions());
                            Collections.sort(pos);
                            posting.setPositions(pos);
                            finalPostings.add(posting);
                            i++;
                        } else if (result.get(i).getDocumentId() > postings.get(j).getDocumentId()) {
                            Posting posting = new Posting();
                            posting.setmDocumentId(postings.get(j).getDocumentId());
                            List<Integer> pos = new ArrayList<>();
                            pos.addAll(postings.get(j).getPositions());
                            Collections.sort(pos);
                            posting.setPositions(pos);
                            finalPostings.add(posting);
                            j++;
                        }
                    }
                    if (i < result.size()) {
                        finalPostings.addAll(result.subList(i, result.size()));
                    }
                    if (j < postings.size()) {
                        finalPostings.addAll(postings.subList(j, postings.size()));
                    }
                } else {
                    finalPostings.addAll(postings);
                }
                result.clear();
                result.addAll(finalPostings);
                finalPostings.clear();
            }
        });
        return result;
    }

    @Override
    public String toString() {
        // Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
        return "(" + String.join(" + ", mChildren.stream().map(c -> c.toString()).collect(Collectors.toList())) + " )";
    }
    
    @Override
    public boolean gettype() {
        return true;
    }
}
