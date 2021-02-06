package RankingStartegy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import cecs.util.Util;
import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.DiskPositionalIndex;
import cecs429.index.Posting;
import cecs429.index.PostingAccumulator;
import cecs429.query.BooleanQueryParser;
import cecs429.query.Query;
import cecs429.ranking.DefaultRanking;
import cecs429.ranking.OkapiBM25Ranking;
import cecs429.ranking.RankedRetrievals;
import cecs429.ranking.RankingStrategy;
import cecs429.text.ExtendedTokenProcessor;
import edu.csulb.BetterTermDocumentIndexerWithPositionalInvertedIndex;
import org.jetbrains.annotations.NotNull;
import org.jfree.ui.RefineryUtilities;

public class PrecisionEvaluation {

	String query;
	private static String mPath;
	private String input;
	private List<String> listKeys;
	private static DocumentCorpus corpus;
	private List<Posting> result_docs = new ArrayList();
	private List<PostingAccumulator> rank_results = new ArrayList<>();
	private static ExtendedTokenProcessor processor = new ExtendedTokenProcessor();
	private static RankingStrategy ranking_strategy;
	private static String query_path;
	private static String qrel_path;

	static void QueryIndex() {
		corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(mPath).toAbsolutePath(), ".json");
	}

	private static List<PostingAccumulator> getRankResults(String query) throws IOException {
		String result = "";
		String q="";
		q = query;
		q = q.replace('.',' ').trim();
		RankedRetrievals r = new RankedRetrievals(q, corpus.getCorpusSize());
		List<PostingAccumulator> rank_results = new ArrayList<>();
		try {
			rank_results = r.getPostings(ranking_strategy);
		} catch (IOException ex) {
			System.out.println("Some error occured in getting ranking results");
		}
		return rank_results;
	}

	public static void calcMAP() throws IOException {
		Scanner sc1 = new Scanner(new File(query_path));
		Scanner sc2 = new Scanner(new File(qrel_path));
		String query = "";
		String relevant = "";
		List<PostingAccumulator> results = new ArrayList<>();
		double MAP = 0;
		double MRT = 0;
		double number_queries = 0;
		double non_zero_acc = 0;
		while (sc1.hasNextLine()) {
			number_queries++;
			String doc_list = "";
			query = sc1.nextLine();
			relevant = sc2.nextLine();
			Set<Integer> relevant_list = new HashSet<>();
			for (String s : relevant.split(" ")) {
				relevant_list.add(Integer.parseInt(s));
			}
			long startTime = System.currentTimeMillis();
			results = getRankResults(query);
			long endTime = System.currentTimeMillis();
			long queryTime = (endTime - startTime);
			double AP = 0;
			double numerator = 0;
			int temp_c= 0;
			for (PostingAccumulator p : results) {
				temp_c++;
				if(p.getAccumulator()!=0){
					non_zero_acc++;
				}

				Posting posting = p.getPosting();
				int doc_id = posting.getDocumentId();
				Document d = corpus.getDocument(doc_id);
				Path doc_path = d.getFilePath();
				String doc_name = doc_path.getFileName().toString();
				doc_name = doc_name.replaceFirst("[.][^.]+$", "");
				int doc_no = Integer.parseInt(doc_name);
				if (relevant_list.contains(doc_no)) {
					numerator++;
					doc_list += doc_no + " ";
					AP += numerator/temp_c;
				}
			}
			AP = AP / relevant_list.size();
			MAP += AP;
			MRT += queryTime;
		}

		MAP = MAP / number_queries;
		MRT = MRT / number_queries;
		double throughput = 1/ (MRT/1000);
		double avg_non_zero_acc = non_zero_acc/number_queries;
		System.out.println("Mean Average Precision : " + MAP);
		System.out.println("Average number of non-zero accumulators: "+avg_non_zero_acc);
		System.out.println("Mean Response Time to satisfy a query: " + MRT + " milliseconds");
		System.out.println("Throughput obtained : " + throughput);
	}

	public static void precrecallcurve(String title) throws IOException {
		
		Scanner sc1 = new Scanner(new File(query_path));
		Scanner sc2 = new Scanner(new File(qrel_path));
		String query = sc1.nextLine();
		String relevant = sc2.nextLine();
		List<PostingAccumulator> results = new ArrayList<>();
		double MAP = 0;
		double MRT = 0;
		double number_queries = 0;

		number_queries++;
		String doc_list = "";
		Set<Integer> relevant_list = new HashSet<>();
		for (String s : relevant.split(" ")) {
			relevant_list.add(Integer.parseInt(s));
		}
		results = getRankResults(query);
		double numerator = 0;
		double denominator = 0;
		double precision, recall;
		List<Double> precision_list = new ArrayList<>();
		List<Double> recall_list = new ArrayList<>();
		for (PostingAccumulator p : results) {
			denominator++;
			Posting posting = p.getPosting();
			int doc_id = posting.getDocumentId();
			Document d = corpus.getDocument(doc_id);

			Path doc_path = d.getFilePath();
			String doc_name = doc_path.getFileName().toString();
			doc_name = doc_name.replaceFirst("[.][^.]+$", "");
			int doc_no = Integer.parseInt(doc_name);
			if (relevant_list.contains(doc_no)) {
				numerator++;
			}
			precision = numerator / denominator;
			precision_list.add(precision);
			recall = numerator / relevant_list.size();
			recall_list.add(recall);

		}
        final XYPlot demo = new XYPlot(title, recall_list, precision_list);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
	}

	public static void turnInResults(String query) throws IOException {

		Scanner sc2 = new Scanner(new File(qrel_path));
		String relevant = sc2.nextLine();

		double MAP = 0;
		double MRT = 0;
		double numberQueries = 0;
		String doc_list = "";

		Set<Integer> relevant_list = new HashSet<>();
		for (String s : relevant.split(" ")) {
			relevant_list.add(Integer.parseInt(s));
		}
		List<PostingAccumulator> results = new ArrayList<>();
		results = getRankResults(query);

		double AP = 0;
		double numerator = 0;
		double denominator = 0;
		double precision=0;
		System.out.println("Ranked retrieval results at K=50");
		System.out.println();
		int temp_c=0;

		for (PostingAccumulator p : results) {
			temp_c++;
			Posting posting = p.getPosting();
			int doc_id = posting.getDocumentId();
			Document d = corpus.getDocument(doc_id);

			Path doc_path = d.getFilePath();
			String doc_name = doc_path.getFileName().toString();
			System.out.print(doc_name + " ");
			doc_name = doc_name.replaceFirst("[.][^.]+$", "");
			int doc_no = Integer.parseInt(doc_name);
			if (relevant_list.contains(doc_no)) {
				numerator++;
				doc_list += doc_no + " ";
				System.out.print(" relevant at index: ");
				System.out.print(temp_c);
				AP += numerator/temp_c;
			}
			else {
				System.out.print(" Irrelevant");
			}
			System.out.println();
			precision = (AP/relevant_list.size());
		}
		System.out.println();
		System.out.println("Average Precision at K=50 " + precision);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 30; i++) {

			results = getRankResults(query);
		}
		long endTime = System.currentTimeMillis();
		long queryTime = (endTime - startTime);
		long throughput = 30 / (queryTime / 1000);
		System.out.println("Throughput averaged over 30 iterations: " + throughput);
	}
	public static void booleanEval()throws IOException {

		BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
		Scanner sc1 = new Scanner(new File(query_path));
		String query = "";
		double MRT = 0;
		double number_queries = 0;
		List<Posting> postings = new ArrayList<>();
		long queryTime = 0;
		while (sc1.hasNextLine()) {
			number_queries++;
			query = sc1.nextLine();
			if (!query.equalsIgnoreCase("")) {
				StringBuilder finalString = stemQuery(query);
				Query obj = booleanQueryParser.parseQuery(finalString.toString().trim().toLowerCase());
				long startTime = System.currentTimeMillis();
				postings = obj.getPostings();
				long endTime = System.currentTimeMillis();
				queryTime = (endTime - startTime);
			}
			MRT += queryTime;
		}
		MRT = MRT / number_queries;
		double throughput = 1/ (MRT/1000);
		System.out.println("Boolean Query Evaluation: ");
		System.out.println("Mean Response Time: "+ MRT + " milliseconds");
		System.out.println("Throughput obtained: "+ throughput);

	}

	@NotNull
	private static StringBuilder stemQuery(String query) {
		StringTokenizer s1 = new StringTokenizer(query, " ");
		StringBuilder finalString = new StringBuilder();
		while (s1.hasMoreElements()) {
			String temp = s1.nextToken().replace("\"", "");
			String stemmedWord = Util.stemWord(temp);
			finalString.append(stemmedWord + " ");
		}
		if (query.contains("\"")) {
			finalString = new StringBuilder("\"" + finalString.toString().trim() + "\"");
		}
		return finalString;
	}

	public static void precEval()throws IOException {

		mPath = BetterTermDocumentIndexerWithPositionalInvertedIndex.folderName;
		QueryIndex();
		DiskPositionalIndex DII = new DiskPositionalIndex();

		query_path = mPath + "relevance/queries";
		qrel_path = mPath + "relevance/qrel";

		ranking_strategy = new DefaultRanking(DII);
		System.out.println("Default Ranking");
		turnInResults("what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft . ");
		System.out.println();

		ranking_strategy = new OkapiBM25Ranking(DII);
		System.out.println("OkapiBM25Ranking");
		turnInResults("what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft . ");
		System.out.println();

		ranking_strategy = new DefaultRanking(DII);
		System.out.println("Default Ranking");
		precrecallcurve("Default Ranking with Impact Ordering");
		calcMAP();
		System.out.println();
		booleanEval();
		System.out.println();
	}
}
