package cecs429.ranking;

import cecs429.index.DiskPositionalIndex;

public class DefaultRanking implements RankingStrategy {
	DiskPositionalIndex mIndex;

	public DefaultRanking(DiskPositionalIndex index) {
		mIndex = index;
	}

	@Override
	public double getWQT(double N, double dft) {
		return Math.log(1 + (N / dft));
	}

	@Override
	public double getWDT(double tftd, int docID) {
		return 1 + Math.log(tftd);
	}

	@Override
	public double getLD(int docId) {
		return mIndex.getLd(docId);
	}

}