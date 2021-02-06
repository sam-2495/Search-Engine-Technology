package RankingStartegy;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.DiskPositionalIndex;
import cecs429.index.Posting;
import cecs429.index.PostingAccumulator;
import cecs429.ranking.*;
import cecs429.text.ExtendedTokenProcessor;
import com.sun.xml.bind.v2.TODO;
import org.jfree.ui.RefineryUtilities;
import cecs429.ranking.RankingStrategy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rank {

    String query;
    private static String mPath;
    private String input;
    private List<String> listKeys;
    private static DocumentCorpus corpus;
    private List<Posting> result_docs = new ArrayList();
    private List<PostingAccumulator> Ranking_results = new ArrayList<>();
    private static ExtendedTokenProcessor processor = new ExtendedTokenProcessor();
    private static RankingStrategy ranking_strategy;
    private static String query_path;
    private static String qrel_path;


    static void QueryIndex() {
        //mPath = "C:\\Docs\\Study\\SET\\Data\\relevance_cranfield";
        corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(mPath).toAbsolutePath(), ".json");
        System.out.println(corpus.getCorpusSize());
        DiskPositionalIndex DII = new DiskPositionalIndex();//new DiskPositionalIndex(mPath + "\\index\\");
        DiskPositionalIndex DII_biword = new DiskPositionalIndex();//new DiskPositionalIndex(mPath + "\\index\\biword\\");
//        index = new Indexes(DII, DII_biword);
        String query;
        //DiskInvertedIndex DII = new DiskInvertedIndex(mPath + "\\index\\");


    }

    private static List<PostingAccumulator> mMethod(String query) throws IOException {
        String result = "";
        RankedRetrievals r = new RankedRetrievals(query, corpus.getCorpusSize());// new RankedRetrievals(query, mPath, corpus.getCorpusSize());
        List<PostingAccumulator> Ranking_results = new ArrayList<>();
        try {
            Ranking_results = r.getPostings(ranking_strategy);
//            TODO: remove
            //r.getPostings(indexes, processor, ranking_strategy);

        } catch (IOException ex) {
            Logger.getLogger(Rank.class.getName()).log(Level.SEVERE, null, ex);
        }
        int j = 0;
        for (PostingAccumulator p : Ranking_results) {
            j++;
            Posting posting = p.getPosting();
            //System.out.println(posting.getDocumentId());
            String s = corpus.getDocument(posting.getDocumentId()).getTitle() + " Accum value - " + p.getAccumulator();
            result += s + "\n";
//        System.out.println(j + ")" + corpus.getDocument(posting.getDocumentId()).getTitle() + " Accum value - " + p.getAccumulator());
        }

        return Ranking_results;
    }

    public static void findMAP() throws IOException {
        query_path = mPath + "\\relevance\\queries";
        qrel_path = mPath + "\\relevance\\qrel";
        Scanner sc1 = new Scanner(new File(query_path));
        Scanner sc2 = new Scanner(new File(qrel_path));
        String query = "";
        String rel = "";
        List<PostingAccumulator> results = new ArrayList<>();
        double MAP = 0;
        double MRT = 0;
        double number_queries = 0;
        while (sc1.hasNextLine()) {
            number_queries++;
            String doc_list = "";
            query = sc1.nextLine();
            rel = sc2.nextLine();
//          System.out.println(query);
            Set<Integer> rel_list = new HashSet<>();
            for (String s : rel.split(" ")) {
                rel_list.add(Integer.parseInt(s));
            }
            long startTime = System.currentTimeMillis();
            results = mMethod(query);
            long endTime = System.currentTimeMillis();
            long queryTime = (endTime - startTime);
            double AP = 0;
            double numerator = 0;
            double denominator = 0;
            for (PostingAccumulator p : results) {
                denominator++;
                Posting posting = p.getPosting();
                int doc_id = posting.getDocumentId();
                Document d = corpus.getDocument(doc_id);

                //TODO: Catch Null pointer exception for folder name
                Path doc_path = d.getFilePath();
                String doc_name = doc_path.getFileName().toString();
                // System.out.println(doc_name);
                doc_name = doc_name.replaceFirst("[.][^.]+$", "");
                int doc_no = Integer.parseInt(doc_name);
                if (rel_list.contains(doc_no)) {
                    numerator++;
                    doc_list += doc_no + " ";
                    double Pik = numerator / denominator;
                    AP += Pik;
                }
            }
            AP = AP / (double) rel_list.size();
//        System.out.println(AP);
            MAP += AP;
            MRT += queryTime;
        }
        double throughput = 256 / (MRT / 1000);
        MAP = MAP / number_queries;
        MRT = MRT / number_queries;
        System.out.println("MAP = " + MAP);
        System.out.println("Throughput = " + throughput);
        System.out.println("MRT = " + MRT + " miliseconds");
    }

    public static void plotgraph(String title) throws IOException {
        query_path = mPath + "\\relevance\\queries";
        qrel_path = mPath + "\\relevance\\qrel";
        Scanner sc1 = new Scanner(new File(query_path));
        Scanner sc2 = new Scanner(new File(qrel_path));
        String query = sc1.nextLine();
        String rel = sc2.nextLine();

        List<PostingAccumulator> results = new ArrayList<>();
        double MAP = 0;
        double MRT = 0;
        double number_queries = 0;

        number_queries++;
        String doc_list = "";
//          System.out.println(query);
        Set<Integer> rel_list = new HashSet<>();
        for (String s : rel.split(" ")) {
            rel_list.add(Integer.parseInt(s));
        }
        results = mMethod(query);
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
            // System.out.println(doc_name);
            doc_name = doc_name.replaceFirst("[.][^.]+$", "");
            int doc_no = Integer.parseInt(doc_name);
            if (rel_list.contains(doc_no)) {
                numerator++;
                //doc_list += doc_no + " ";
            }
            precision = numerator / denominator;
            precision_list.add(precision);
            recall = numerator / rel_list.size();
            recall_list.add(recall);


        }
        final XYPlot demo = new XYPlot(title, recall_list, precision_list);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);


    }

    public static void turn_in_results(String query) throws IOException {
        qrel_path = mPath + "\\relevance\\qrel";
        //Scanner sc1 = new Scanner(new File(query_path));
        Scanner sc2 = new Scanner(new File(qrel_path));

        String rel = sc2.nextLine();


        double MAP = 0;
        double MRT = 0;
        double number_queries = 0;
        String doc_list = "";
//          System.out.println(query);
        Set<Integer> rel_list = new HashSet<>();
        for (String s : rel.split(" ")) {
            rel_list.add(Integer.parseInt(s));
        }
        List<PostingAccumulator> results = new ArrayList<>();
        results = mMethod(query);
        double AP = 0;
        double numerator = 0;
        double denominator = 0;
        double precision, recall;
        System.out.println("Ranked retrieval results ");
        for (PostingAccumulator p : results) {
            denominator++;
            Posting posting = p.getPosting();
            int doc_id = posting.getDocumentId();
            Document d = corpus.getDocument(doc_id);

            Path doc_path = d.getFilePath();
            String doc_name = doc_path.getFileName().toString();
            System.out.print(doc_name + " ");
            doc_name = doc_name.replaceFirst("[.][^.]+$", "");
            int doc_no = Integer.parseInt(doc_name);
            if (rel_list.contains(doc_no)) {
                numerator++;
                double Pik = numerator / denominator;
                AP += Pik;
                doc_list += doc_no + " ";
            }
            precision = numerator / denominator;
            // precision_list.add(precision);
            recall = numerator / rel_list.size();
            //recall_list.add(recall);
        }
        System.out.println();
        System.out.println("Relevant docs " + doc_list);
        System.out.println("The Average Precision for Default ranking at K=50 " + AP);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 30; i++) {

            results = mMethod(query);
        }
        long endTime = System.currentTimeMillis();
        long queryTime = (endTime - startTime);
        long throughput = 30 / (queryTime / 1000);
        System.out.println("Throughput -- " + throughput);
    }

    public static void main(String args[]) throws IOException {

        System.out.println("Enter the corpus path : ");
        Scanner sc = new Scanner(System.in);
        mPath = sc.nextLine();
        QueryIndex();
        DiskPositionalIndex DII = new DiskPositionalIndex();//new DiskPositionalIndex(mPath + "\\index\\");

        query_path = mPath + "\\relevance\\queries";
        qrel_path = mPath + "\\relevance\\qrel";

        ranking_strategy = new DefaultRanking(DII);
        System.out.println("Default Ranking");
        turn_in_results("what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft . ");
        ranking_strategy = new OkapiBM25Ranking(DII);
        System.out.println("OkapiBM25 Ranking");
        turn_in_results("what similarity laws must be obeyed when constructing aeroelastic models of heated high speed aircraft . ");

        ranking_strategy = new DefaultRanking(DII);
        System.out.println("Default Ranking");
        plotgraph("Default Ranking");
        findMAP();
        System.out.println();
        ranking_strategy = new OkapiBM25Ranking(DII);
        System.out.println("OkapiBM25 Ranking");
        plotgraph("OkapiBM25 Ranking");
        findMAP();
        System.out.println();
        ranking_strategy = new TfIdfRanking(DII);
        System.out.println("Tf_Idf Ranking");
        plotgraph("Tf_Idf Ranking");
        findMAP();
        System.out.println();
        ranking_strategy = new WackyRanking(DII);
        System.out.println("Wacky Ranking");
        plotgraph("Wacky Ranking");
        findMAP();

    }
}
