package cecs429.index;

import java.io.Serializable;
import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting implements Comparable<Posting>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mDocumentId;
	private List<Integer> positions;
	private int mtftd;

	public Posting() {
		super();
	}

	@Override
	public String toString() {
		return "Posting [mDocumentId=" + mDocumentId + ", positions=" + positions + ", mtftd=" + mtftd + "]";
	}

	public int getMtftd() {
		return mtftd;
	}

	public void setMtftd(int mtftd) {
		this.mtftd = mtftd;
	}

	public int getmDocumentId() {
		return mDocumentId;
	}

	public Posting(int mDocumentId, List<Integer> positions, int mtftd) {
		super();
		this.mDocumentId = mDocumentId;
		this.positions = positions;
		this.mtftd = mtftd;
	}

	public void setmDocumentId(int mDocumentId) {
		this.mDocumentId = mDocumentId;
	}

	public List<Integer> getPositions() {
		return positions;
	}

	public void setPositions(List<Integer> positions) {
		this.positions = positions;
	}

	public Posting(int documentId) {
		mDocumentId = documentId;
	}

	public int getDocumentId() {
		return mDocumentId;
	}

	@Override
	public int compareTo(Posting o) {
		return this.getDocumentId() - o.getDocumentId();
	}
}
