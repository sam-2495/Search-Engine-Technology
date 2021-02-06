package cecs429.ranking;

import cecs429.index.DiskPositionalIndex;

public class TfIdfRanking implements RankingStrategy {
	DiskPositionalIndex mIndex;

	public TfIdfRanking(DiskPositionalIndex index) {
		mIndex = index;
	}

	@Override
	public double getWQT(double N, double dft) {
		return Math.log(N / dft);
	}

	@Override
	public double getWDT(double tftd, int docID) {
		return tftd;
	}

	@Override
	public double getLD(int docId) {
		return mIndex.getLd(docId);
	}

}