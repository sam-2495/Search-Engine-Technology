package edu.csulb;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;

import RankingStartegy.PrecisionEvaluation;
import cecs.util.Util;
import cecs429.database.DatabaseUtil;
import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.BiwordIndexer;
import cecs429.index.DiskPositionalIndex;
import cecs429.index.Index;
import cecs429.index.Indexer;
import cecs429.index.Posting;
import cecs429.index.PostingAccumulator;
import cecs429.query.BooleanQueryParser;
import cecs429.query.Query;
import cecs429.ranking.DefaultRanking;
import cecs429.ranking.OkapiBM25Ranking;
import cecs429.ranking.RankedRetrievals;
import cecs429.ranking.RankingStrategy;
import cecs429.ranking.TfIdfRanking;
import cecs429.ranking.WackyRanking;
import cecs429.text.Soundex;
import model.Feed;

public class BetterTermDocumentIndexerWithPositionalInvertedIndex {
	public static final String SECONDS_TO_INDEX = " seconds to index.";
	private static Scanner scanner = new Scanner(System.in);
	public static String corpusType = "";
	public static Feed feed = null;
	public static String docWeightsFile = "/Users/samarthyadav/Desktop/Milestone_3_Final/Index_Disk/docWeights.bin";
	public static String soundexFile = "/Users/samarthyadav/Desktop/Milestone_3_Final/Index_Disk/soundex.bin";
	public static String biwordIndexFile = "/Users/samarthyadav/Desktop/Milestone_3_Final/Index_Disk/biwordIndex.bin";
	public static String diskIndexPath = "/Users/samarthyadav/Desktop/Milestone_3_Final/Index_Disk/Postings.bin";
	public static String folderName;
	public static DocumentCorpus corpus;
	//main method
	public static void main(String[] args) throws IOException {
		System.out.print("Enter corpus path to index : ");
		folderName = scanner.nextLine();
		corpus = extractCorpus(folderName);
		Index index = Indexer.indexCorpus(corpus);
		BiwordIndexer.biwordIndexCorpus(corpus);
		corpusType = "JSON";
		int queryType = -1;
		while (queryType != 4) {
			displayQueryType();
			queryType = scanner.nextInt();
			if (queryType == 1) {
				scanner.nextLine();
				displayMenu();
				String query = scanner.nextLine();
				query = query.toLowerCase();
				BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
				DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex(index.getVocabulary(),
						BetterTermDocumentIndexerWithPositionalInvertedIndex.diskIndexPath);
				while (query != null) {
					if (query.equalsIgnoreCase(":q")) {
						break;
					} /*
						 * else if (query.startsWith(":xml")) { index = xmlIndexer(query); }
						 */else if (query.startsWith(":stem")) {
						runStemmer(query);
					} else if (query.startsWith(":author")) {
						runSoundex(corpus, query, diskPositionalIndex);
					} else if (query.startsWith(":index")) {
						String directoryToIndex = query.replace(":index", "").trim();
						corpus = extractCorpus(directoryToIndex);
						feed = null;
						index = Indexer.indexCorpus(corpus);
						DiskIndexWriter.createBinaryFile(index,
								BetterTermDocumentIndexerWithPositionalInvertedIndex.diskIndexPath);
						BiwordIndexer.biwordIndexCorpus(corpus);
						System.out.println(directoryToIndex + " indexed.");
					} else if (query.equalsIgnoreCase(":vocab")) {
						runVocab(index);
					} else {
						runOtherQueries(corpus, index, query, booleanQueryParser);
					}
					displayMenu();
					query = scanner.nextLine();
				}
			} else if (queryType == 2) {
				try {
					runRankedQuery(corpus);
				} catch (InputMismatchException e) {
					System.out.println("Invalid Input.");
					continue;
				}
			} else if (queryType == 3) {
				PrecisionEvaluation.precEval();
			}
			else if(queryType == 4){
				runExit();
			}
			else {
				System.out.println("Wrong input. Please try again.");
			}
		}
	}

	// Method to close any open files while exiting the program.
	private static void runExit() {
		if (DatabaseUtil.biwordDB == null)
			DatabaseUtil.biwordDB.close();
		if (DatabaseUtil.docWeightsDB == null)
			DatabaseUtil.docWeightsDB.close();
		if (DatabaseUtil.postingsDB == null)
			DatabaseUtil.postingsDB.close();
		if (DatabaseUtil.soundexDB == null)
			DatabaseUtil.soundexDB.close();
		System.out.println("Thank You.");
		System.exit(0);
	}

	// Method to run ranked query by taking appropriate strategy as input from the user.
	private static void runRankedQuery(DocumentCorpus corpus) {
		int rankingType = -1;
		displayRankingMenu();
		rankingType = takeInput();
		while (rankingType != 5) {
			scanner.nextLine();
			System.out.println("Enter Query : ");
			String query = scanner.nextLine();
//			query = query.toLowerCase();
			runRankedQuery(query, corpus, rankingType);
			displayRankingMenu();
			rankingType = takeInput();
		}
	}

	// Method to take user's choice for the ranking strategy based on the ranking menu displayed.
	private static int takeInput() {
		int input = -1;
		while (true) {
			try {
				input = scanner.nextInt();
				break;
			} catch (InputMismatchException e) {
				System.out.println("Invalid Input. Please try again");
				scanner.next();
				displayRankingMenu();
			}
		}
		return input;
	}

	// Method to facilitate the user to opt for the query type to be searched for.
	private static void displayQueryType() {
		System.out.println("1. Boolean Query");
		System.out.println("2. Ranked Query");
		System.out.println("3. Average Precision");
		System.out.println("4. Exit");
	}

	//Method to extract the corpus from the given filepath
	private static DocumentCorpus extractCorpus(String directoryToIndex) throws IOException {
		DocumentCorpus corpus = null;
		File folder = new File(directoryToIndex);
		File f = null;
		if (folder.isDirectory()) {
			f = folder.listFiles()[0];
		}
		if (f.getAbsolutePath().endsWith(".json")) {
			corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(directoryToIndex).toAbsolutePath(), ".json");
			corpusType = "JSON";
		} else {
			corpus = DirectoryCorpus.loadTextDirectory(Paths.get(directoryToIndex).toAbsolutePath(), ".txt");
			corpusType = "TXT";
		}
		return corpus;
	}

	// Method to run the queries supported by Boolean Query Module.
	private static void runOtherQueries(DocumentCorpus corpus, Index index, String query,
			BooleanQueryParser booleanQueryParser) {
		try {
			if (!query.equalsIgnoreCase("")) {
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

				Query obj = booleanQueryParser.parseQuery(finalString.toString().trim().toLowerCase());
				List<Posting> postings = new ArrayList<>();
				postings = obj.getPostings();
				if (postings != null && !postings.isEmpty()) {
					System.out.println("The documents where the term \'" + query.trim() + "\' is present are:");
					for (Posting p : postings) {

						Document content = corpus.getDocument(p.getDocumentId());
						System.out.println("Document ID " + p.getDocumentId());
						String title = corpusType.equalsIgnoreCase("XML")
								? feed.getDoc().get(p.getDocumentId()).getTitle()
								: content.getTitle();
						System.out.println("Title:" + title + "\n");

					}
					System.out.println("Total number of documents in the output : " + postings.size());
					System.out.println("Enter a Document Id for which you want to see the actual text:");

					try {
						int docId = scanner.nextInt();
						if (corpusType.equalsIgnoreCase("XML")) {
							System.out.println(feed.getDoc().get(docId).getContent());
						} else {
							getContentToDisplay(corpus, docId);
						}
					} catch (InputMismatchException e) {
						System.out.println("Invalid Input for Document ID");
					} catch (Exception e) {
						System.out.println("File not Found");
					}
					scanner.nextLine();
				} else {
					System.out.println("Word not found!");
				}

			} else {
				System.out.println("Blank Input");
			}

		} catch (Exception e) {
			System.out.println("Invalid Query");
		}
	}

	// Method to display the content of any requested file by the user
	private static void getContentToDisplay(DocumentCorpus corpus, int docId) throws IOException {
		Document content = corpus.getDocument(docId);
		Reader reader = content.getContent();
		int i = 0;
		int data = reader.read();
		while (data != -1) {
			if (i == 100) {
				System.out.println("");
				i = 0;
			}
			System.out.print((char) data);
			data = reader.read();
			i++;
		}
		reader.close();
	}

	// Method to print the first 1000 terms of the sorted vocabulary.
	private static void runVocab(Index index) {
		List<String> sublist = new ArrayList<>();
		if (index.getVocabulary().size() < 1000) {
			sublist = index.getVocabulary();
		} else {
			sublist = index.getVocabulary().subList(0, 1000);
		}
		sublist.forEach(word -> {
			System.out.print(word + ",");
		});
		System.out.println();
		System.out.println("Total Number of terms in the vocabulary are: "+index.getVocabulary().size());
	}

	// Method to get postings from the soundex index
	private static void runSoundex(DocumentCorpus corpus, String query, DiskPositionalIndex diskPositionalIndex) {
		String author = query.replace(":author", "").trim();
		Soundex soundex = new Soundex();
		String s = soundex.getSoundex(Util.stemWord(author));
		List<Integer> docIds = diskPositionalIndex.getSoundex(s);
		if (!docIds.isEmpty()) {
			System.out.println("Document Ids where the soundex " + s + " for the word " + author + " is present are :");
			for (Integer docId : docIds) {
				System.out
						.println(corpus.getDocument(docId).getTitle() + " : " + corpus.getDocument(docId).getAuthor());
			}
			System.out.println("Total number of documents:" + docIds.size());
		}
	}

	// Method to stem a term
	private static void runStemmer(String query) {
		String wordToStem = query.replace(":stem", "").trim();
		String stemmedWord = Util.stemWord(wordToStem);
		System.out.println("The stemmed word is : " + stemmedWord);
	}

	// Method to boolean query display menu.
	public static void displayMenu() {
		System.out.println("\n=========================BOOLEAN QUERY MENU=========================");
		System.out.println(":q -> Exit boolean query menu.");
		System.out.println(":stem <word> -> To find the token of the word.");
		System.out.println(":index <directory_name> -> To index the given directory.");
		System.out.println(":vocab -> To get the first 1000 words in the vocabulary.");
		System.out.println("Any other word or query -> To search in the index.");
		System.out.println(":xml <directory_name> -> To index the given xml file.");
		System.out.println(":author <author_name> -> To find writings of an author using soundex algorithm.");
		System.out.println("======================================================");
	}

	// Method to display ranking menu.
	public static void displayRankingMenu() {
		System.out.println("\n=====================RANKING QUERY MENU=====================");
		System.out.println("1. Default Ranking.");
		System.out.println("2. TfIdf Ranking.");
		System.out.println("3. Okapi BM25 Ranking.");
		System.out.println("4. Wacky Ranking.");
		System.out.println("5. Exit from Ranked Query.");
		System.out.println("======================================================");
	}

	// Method to run a ranked query
	private static void runRankedQuery(String query, DocumentCorpus corpus, int rankedQueryType) {
		DefaultListModel<String> listModel = new DefaultListModel<>();
		DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex();
		RankingStrategy ranking_strategy = null;
		switch (rankedQueryType) {
		case 1:
			ranking_strategy = new DefaultRanking(diskPositionalIndex);
			break;
		case 2:
			ranking_strategy = new TfIdfRanking(diskPositionalIndex);
			break;
		case 3:
			ranking_strategy = new OkapiBM25Ranking(diskPositionalIndex);
			break;
		case 4:
			ranking_strategy = new WackyRanking(diskPositionalIndex);
			break;
		default:

			break;
		}
		RankedRetrievals r = new RankedRetrievals(query, corpus.getCorpusSize());
		List<PostingAccumulator> postings = new ArrayList<>();
		try {
			postings = r.getPostings(ranking_strategy);
		} catch (Exception e) {
		}
		int j = 0;
		for (PostingAccumulator p : postings) {
			j++;
			Posting posting = p.getPosting();
			listModel.addElement(corpus.getDocument(posting.getDocumentId()).getTitle());
			System.out.println(j + "-" + corpus.getDocument(posting.getDocumentId()).getTitle() + " Accumulator value : "
					+ p.getAccumulator());
		}
	}

}
