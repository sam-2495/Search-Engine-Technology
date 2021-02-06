package cecs429.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
	public List<Posting> getPostings() {
		List<Query> negative = new ArrayList<>();
		List<Posting> finalPostings = new ArrayList<>();
		List<Posting> result = new ArrayList<>();
		for (Query query : mChildren) {
			if (query.gettype()) {
				List<Posting> postings = query.getPostings();
				if (postings != null) {
					Collections.sort(postings, Posting.idComparator);
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
			} else {
				negative.add(query);
			}

			for (Query qc : negative) {
				List<Posting> posting = qc.getPostings();
				List<Posting> temp = new ArrayList<Posting>(result);
				result.clear();
				int i = 0, j = 0;
				while (i < temp.size() && j < posting.size()) {
					if (temp.get(i).getDocumentId() == posting.get(j).getDocumentId()) {
						i++;
						j++;
					} else {
						if (temp.get(i).getDocumentId() > posting.get(j).getDocumentId()) {
							j++;
						} else {
							result.add(temp.get(i));
							i++;
						}
					}
				}
				while (i < temp.size()) {
					result.add(temp.get(i));
					i++;
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return String.join(" ", mChildren.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}

	@Override
	public boolean gettype() {
		return true;
	}
}
