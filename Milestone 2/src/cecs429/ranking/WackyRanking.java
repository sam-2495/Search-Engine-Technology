package cecs429.ranking;

import cecs429.index.DiskPositionalIndex;

public class WackyRanking implements RankingStrategy {

	DiskPositionalIndex mIndex;

	public WackyRanking(DiskPositionalIndex index) {
		mIndex = index;
	}

	@Override
	public double getWQT(double N, double dft) {
		double a = Math.log((N - dft) / dft);
		double wqt = Math.max(0, a);
		return wqt;
	}

	@Override
	public double getWDT(double tftd, int docID) {
		double avg_tftd = 0;
		try {
			avg_tftd = mIndex.getavgTftd(docID);
		} catch (Exception ex) {
		}
		double numerator = 1 + Math.log(tftd);
		double denominator = 1 + Math.log(avg_tftd);
		double wdt = numerator / denominator;
		return wdt;
	}

	@Override
	public double getLD(int docId) {
		double bytesize = 0;
		try {
			bytesize = mIndex.getbyteSize(docId);
		} catch (Exception ex) {
		}
		double LD = Math.sqrt(bytesize);
		return LD;
	}

}