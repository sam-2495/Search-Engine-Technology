package cecs429.index;

import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting implements Comparable<Posting> {
    private int mDocumentId;
    private List<Integer> positions;

    public Posting() {
        super();
    }

    @Override
    public String toString() {
        return "Posting [mDocumentId=" + mDocumentId + ", positions=" + positions + "]";
    }

    public Posting(int mDocumentId, List<Integer> positions) {
        super();
        this.mDocumentId = mDocumentId;
        this.positions = positions;
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
