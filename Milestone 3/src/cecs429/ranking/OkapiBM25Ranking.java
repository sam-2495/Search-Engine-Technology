package cecs429.ranking;

import cecs429.index.DiskPositionalIndex;

public class OkapiBM25Ranking implements RankingStrategy {
	DiskPositionalIndex mIndex;

	public OkapiBM25Ranking(DiskPositionalIndex index) {
		mIndex = index;
	}

	@Override
	public double getWQT(double N, double dft) {
		double a = 0.1;
		double b = Math.log((N - dft + 0.5) / (dft + 0.5));
		double wqt = Math.max(a, b);
		return wqt;
	}

	@Override
	public double getWDT(double tftd, int docID) {
		double doclength = 0, doclengthA = 0;
		try {
			doclength = mIndex.getdocLength(docID);
			doclengthA = mIndex.getdocLengthA();
		} catch (Exception ex) {
		}
		double numerator = 2.2 * tftd;
		double denominator = (1.2 * (0.25 + (0.75) * (doclength / doclengthA))) + tftd;
		double wdt = numerator / denominator;
		return wdt;
	}

	@Override
	public double getLD(int docId) {
		return 1;
	}

}