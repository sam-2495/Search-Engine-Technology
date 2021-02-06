package cecs429.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cecs429.index.DiskPositionalIndex;
import cecs429.index.Posting;

/**
 * Represents a phrase literal consisting of one or more terms that must occur
 * in sequence.
 */

public class PhraseLiteral implements Query {

	@Override
	public boolean gettype() {
		for (int i = 0; i < mTerms.size(); i++) {
			if (mTerms.get(i).contains("-")) {
				return false;
			}
		}
		return true;
	}

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
	public List<Posting> getPostings() {
		DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex();
		boolean wcl = false;
		for (String mTerm : mTerms) {
			if (mTerm.contains("*")) {
				wcl = true;
			}
		}
		if (mTerms.size() == 2 && !wcl) {
			String myterm = mTerms.get(0).trim() + " " + mTerms.get(1).trim();
			List<Posting> postings = new ArrayList<>();
			Set<Integer> p = new HashSet<>(diskPositionalIndex.getBiwordPostings(myterm.replace("-", "")));
			if (p != null && !p.isEmpty()) {
				p.forEach(i -> {
					postings.add(new Posting(i));
				});
			}
			return postings;
		} else {
			List<Posting> finalPostings = new ArrayList<>();
			List<Posting> result = new ArrayList<>();
			for (int iter = 0; iter < mTerms.size(); iter++) {
				String t = mTerms.get(iter);
				List<Posting> termPostings = diskPositionalIndex.getPostings(t.replace("-", ""));
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
								if ((p2.get(b) - p1.get(a)) <= 1 && p2.get(b) > p1.get(a)) {
									pos.add(p2.get(b));
									a++;
									b++;
								} else if (p1.get(a) + 1 < p2.get(b)) {
									a++;
								} else {
									b++;
								}
							}
							posting.setPositions(pos);
							i++;
							j++;
							finalPostings.add(posting);
						} else if (result.get(i).getDocumentId() < termPostings.get(j).getDocumentId()) {
							i++;
						} else {
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
}
