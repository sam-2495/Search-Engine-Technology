package cecs429.query;

import java.util.List;

import cecs429.index.DiskPositionalIndex;
import cecs429.index.Posting;

/**
 * A TermLiteral represents a single term in a subquery.
 */
public class TermLiteral implements Query {
	private String mTerm;

	public TermLiteral(String term) {
		mTerm = term;
	}

	public String getTerm() {
		return mTerm;
	}

	@Override
	public List<Posting> getPostings() {
		DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex();
		return diskPositionalIndex.getPostings(mTerm.trim());
	}

	@Override
	public String toString() {
		return mTerm;
	}

	@Override
	public boolean gettype() {
		return true;
	}
}
