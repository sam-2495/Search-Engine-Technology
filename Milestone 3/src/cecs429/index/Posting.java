package cecs429.index;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mDocumentId;
	private List<Integer> positions;
	private int mtftd;
	private double wdt;

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

	public Posting(int mDocumentId, List<Integer> positions, int mtftd, double wdt) {
		super();
		this.mDocumentId = mDocumentId;
		this.positions = positions;
		this.mtftd = mtftd;
		this.wdt = wdt;
	}

	public int getmDocumentId() {
		return mDocumentId;
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

	public double getWdt() {
		return wdt;
	}

	public void setWdt(double wdt) {
		this.wdt = wdt;
	}

	public static Comparator<Posting> idComparator = new Comparator<Posting>() {
		public int compare(Posting p1, Posting p2) {
			return p1.getDocumentId() - p2.getDocumentId();
		}
	};

	public static Comparator<Posting> wdtComparator = new Comparator<Posting>() {
		public int compare(Posting p1, Posting p2) {
			return Double.compare(p2.getWdt(), p1.getWdt());
		}
	};

}
