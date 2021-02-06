package cecs429.ranking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import cecs429.index.DiskPositionalIndex;
import cecs429.index.Posting;
import cecs429.index.PostingAccumulator;
import cecs429.text.ExtendedTokenProcessor;

public class RankedRetrievals {

	private List<String> mqueries;
	private int msize;

	public RankedRetrievals(String query, int size) {
		mqueries = new ArrayList<>();
		String[] q = query.split(" ");
		for (int i = 0; i < q.length; i++) {
			mqueries.add(q[i]);
		}
		msize = size;
	}

	public List<PostingAccumulator> getPostings(RankingStrategy rank_strategy) throws IOException {
		ExtendedTokenProcessor processor = new ExtendedTokenProcessor();
		DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex();
		for (int i = 0; i < mqueries.size(); i++) {
			List<String> s = new ArrayList<>(processor.processToken(mqueries.get(i), true));
			mqueries.set(i, s.get(s.size() - 1));
		}
		List<PostingAccumulator> results = new ArrayList<>();
		HashMap<Integer, PostingAccumulator> map = new HashMap<>();
		double N = (double) msize;
		for (String s : mqueries) {
			List<Posting> postings = diskPositionalIndex.getPostings(s);
			double dft = postings.size();
			double wqt = rank_strategy.getWQT(N, dft);
			for (Posting p : postings) {
				double tftd = p.getMtftd();
				double wdt = rank_strategy.getWDT(tftd, p.getDocumentId());

				double increment = wdt * wqt;
				if (map.containsKey(p.getDocumentId())) {
					PostingAccumulator postingaccumulator = map.get(p.getDocumentId());
					double Ad = postingaccumulator.getAccumulator() + increment;
					postingaccumulator.setAccumulator(Ad);
					map.put(p.getDocumentId(), postingaccumulator);
				} else {
					map.put(p.getDocumentId(), new PostingAccumulator(p, increment));
				}
			}

		}
		PriorityQueue<PostingAccumulator> PQ = new PriorityQueue<>();

		for (HashMap.Entry<Integer, PostingAccumulator> entry : map.entrySet()) {
			double LD = rank_strategy.getLD(entry.getKey());
			PostingAccumulator p = entry.getValue();
			Double Ad;
			if (p.getAccumulator() != 0) {
				Ad = p.getAccumulator() / LD;
				p.setAccumulator(Ad);
			}
			PQ.add(p);
		}

		int size = PQ.size();
		int i = 0;
		while (i < 10 && i < size) {
			results.add(PQ.poll());
			i++;
		}

		return results;
	}

}