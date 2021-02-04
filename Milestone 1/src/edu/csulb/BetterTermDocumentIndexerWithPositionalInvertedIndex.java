package edu.csulb;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.bind.JAXBException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cecs.jaxb.XmlToObject;
import cecs.util.Util;
import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.BiwordIndex;
import cecs429.index.Index;
import cecs429.index.KGramIndex;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.Query;
import cecs429.text.EnglishTokenStream;
import cecs429.text.ExtendedTokenProcessor;
import cecs429.text.TokenProcessor;
import model.Doc;
import model.Feed;
import model.MyDocs;
import model.MyDocument;

public class BetterTermDocumentIndexerWithPositionalInvertedIndex {
    private static Scanner scanner = new Scanner(System.in);
    public static KGramIndex kGramIndex = new KGramIndex();
    public static String corpusType = "";
    public static Feed feed = null;

    public static void main(String[] args) {
        // commented line below so as to not generated the json files again.
//        String folderName = splitJson("Users/samarthyadav/Desktop/all-nps-sites.json");
        String folderName = "Users/samarthyadav/Desktop/SET Projects/Milestone 1 Output Files/Output JSON Files";
        String fileName = "Users/samarthyadav/Desktop/SET Projects/Postings.bin";
        DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(folderName).toAbsolutePath(), ".json");
        Index index = indexCorpus(corpus);
        try {
            createbinpostings(index, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        try {
//            readbinpostings(fileName);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        BiwordIndex biwordIndex = biwordIndexCorpus(corpus);
        corpusType = "JSON";
        displayMenu();
        String query = scanner.nextLine();
        BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
        while (query != null) {
            if (query.equalsIgnoreCase(":q")) {
                System.out.println("Thank You!");
                System.exit(0);
            } else if (query.startsWith(":xml")) {
                String fileToIndex = query.replace(":xml", "").trim();
                XmlToObject xmlToObject = new XmlToObject();
                feed = xmlToObject.getDataFromXMLFile(fileToIndex);
                index = indexXmlCorpus(feed);
                biwordIndex = biwordXmlIndexCorpus(feed);
                corpusType = "XML";
                folderName = "";
            } else if (query.startsWith(":stem")) {
                String wordToStem = query.replace(":stem", "").trim();
                String stemmedWord = Util.stemWord(wordToStem);
                System.out.println("The stemmed word is : " + stemmedWord);
            } else if (query.startsWith(":index")) {
                String directoryToIndex = query.replace(":index", "").trim();
                if (directoryToIndex.endsWith(".json")) {
                    folderName = splitJson(directoryToIndex);
                    corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(folderName).toAbsolutePath(), ".json");
                    corpusType = "JSON";
                } else {
                    corpus = DirectoryCorpus.loadTextDirectory(Paths.get(directoryToIndex).toAbsolutePath(), ".txt");
                    corpusType = "TXT";
                }
                feed = null;
                index = indexCorpus(corpus);
                biwordIndex = biwordIndexCorpus(corpus);
                System.out.println(directoryToIndex + " indexed.");
            } else if (query.equalsIgnoreCase(":vocab")) {
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
            } else {
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

//                        Query obj = booleanQueryParser.parseQuery(query.toLowerCase());
                        Query obj = booleanQueryParser.parseQuery(finalString.toString().trim().toLowerCase());
                        List<Posting> postings = new ArrayList<>();
                        postings = obj.getPostings(index);
                        if (postings != null && !postings.isEmpty()) {
//                            int count = 0;
                            System.out.println("The documents where the term \'" + query.trim() + "\' is present are:");
                            for (Posting p : postings) {

                                Document content = corpus.getDocument(p.getDocumentId());
                                System.out.println("Document ID " + p.getDocumentId());
                                String title = corpusType.equalsIgnoreCase("XML") ? feed.getDoc().get(p.getDocumentId()).getTitle() : content.getTitle();
                                System.out.println("Title:" + title + "\n");

                                if (p.getPositions() != null && !p.getPositions().isEmpty()) {
                                    for (int pos :
                                            p.getPositions()) {
//                                        count++;
                                    }
                                    System.out.println();
                                }

                            }
                            System.out.println("Total number of documents in the output : " + postings.size());
                            System.out.println("Enter a Document Id for which you want to see the actual text:");

                            try {
                                int docId = scanner.nextInt();
                                if (corpusType.equalsIgnoreCase("XML")) {
                                    System.out.println(feed.getDoc().get(docId).getContent());
                                } else {
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
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid Input for Document ID");
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
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
            displayMenu();
            query = scanner.nextLine();
        }
    }

    private static Index indexCorpus(DocumentCorpus corpus) {
        kGramIndex.resetIndex();
        long start = System.currentTimeMillis();
        HashSet<String> vocabulary = new HashSet<>();
        TokenProcessor processor = new ExtendedTokenProcessor();
        Iterable<Document> documents = corpus.getDocuments();
        // Create the vocabulary
        PositionalInvertedIndex index = new PositionalInvertedIndex();

        documents.forEach(document -> {
            EnglishTokenStream englishTokenStream = new EnglishTokenStream(document.getContent());
            Iterable<String> tokens = englishTokenStream.getTokens();
            Iterator<String> iter = tokens.iterator();

            int position = 0;
            while (iter.hasNext()) {
                List<String> processedTokens = processor.processToken(iter.next(), true);
                vocabulary.addAll(processedTokens);
                for (String processedToken : processedTokens) {
                    index.addTerm(processedToken, document.getId(), position);
                }
                position++;
            }
            try {
//                System.out.println(document.getId());
                englishTokenStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        index.setmVocabulary(new ArrayList<String>(vocabulary));
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000 + " seconds to index.");
        for (String v : vocabulary) {
            kGramIndex.addToken(v);
        }

        return index;
    }

    private static Index indexXmlCorpus(Feed feed) {
        String folderName = "Output XML Files";
        Path path = Paths.get(folderName);
        FileWriter writer = null;
        try {
            Files.createDirectories(path);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        kGramIndex.resetIndex();
        long start = System.currentTimeMillis();
        HashSet<String> vocabulary = new HashSet<>();
        TokenProcessor processor = new ExtendedTokenProcessor();
        Iterable<Doc> documents = feed.getDoc();
        List<Doc> docs = new ArrayList<>();
        documents.forEach(document -> {
            docs.add(document);
        });

        // Create index for all the documents
        PositionalInvertedIndex index = new PositionalInvertedIndex();
        HashSet<String> vocab = new HashSet<>();
        for (int i = 0; i < docs.size(); i++) {
            EnglishTokenStream englishTokenStream = new EnglishTokenStream(new StringReader(docs.get(i).getContent()));
            Iterable<String> tokens = englishTokenStream.getTokens();
            Iterator<String> iter = tokens.iterator();
            int position = 1;
            while (iter.hasNext()) {
                List<String> processedTokens = processor.processToken(iter.next(), true);
                for (String processedToken : processedTokens) {
                    index.addTerm(processedToken, i, position);
                    vocab.add(processedToken);
                }
                position++;
            }
            try {
                englishTokenStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(docs.get(i).getTitle());
        }
        for (String v : vocab ) {
            kGramIndex.addToken(v);
        }
        index.setmVocabulary(new ArrayList<>(vocab));
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000 + " seconds to index.");
        return index;
    }

    // Split the json into multiple json
    private static String splitJson(String fileName) {
        String folderName = "";
        Gson gson = new Gson();
        MyDocs file = null;
        try {
            System.out.println("Reading JSON from a file");
            System.out.println("----------------------------");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            file = gson.fromJson(bufferedReader, MyDocs.class);
            System.out.println("JSON File read.");
            System.out.println("Creating Folder.");
            folderName = "/Users/anuditverma/Desktop/Output JSON Files";
            Path path = Paths.get(folderName);
            Files.createDirectories(path);
            System.out.println("Folder is created! " + folderName);
            List<MyDocument> documents = file.getDocuments();
            System.out.println("Number of documents: " + documents.size());
            System.out.println("Creating Documents.");
            for (int i = 0; i < documents.size(); i++) {
//                 To Test for less files
                if (i == 10)
                    break;
                Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
                String json = gson2.toJson(documents.get(i));
                FileWriter writer = null;
                try {
                    writer = new FileWriter(folderName + "/article" + (i) + ".json");
                    writer.write(json);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    writer.close();
                }

            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        System.out.println("Documents created");
        return folderName;
    }

    // Method to display menu
    public static void displayMenu() {
        System.out.println("\n=========================MENU=========================");
        System.out.println(":q -> Quit the program.");
        System.out.println(":stem <word> -> To find the token of the word.");
        System.out.println(":index <directory_name> -> To index the given directory.");
        System.out.println(":vocab -> To get the first 1000 words in the vocabulary.");
        System.out.println("Any other word or query -> To search in the index.");
        System.out.println(":xml <directory_name> -> To index the given xml file.");
        System.out.println("======================================================");
    }

    // Create a biword index
    private static BiwordIndex biwordIndexCorpus(DocumentCorpus corpus) {
        long start = System.currentTimeMillis();
        HashSet<String> vocabulary = new HashSet<>();
        TokenProcessor processor = new ExtendedTokenProcessor();
        Iterable<Document> documents = corpus.getDocuments();

        // Create index for all the documents
        BiwordIndex index = new BiwordIndex();
        HashSet<String> vocab = new HashSet<>();
        documents.forEach(document -> {
            EnglishTokenStream englishTokenStream = new EnglishTokenStream(document.getContent());
            Iterable<String> tokens = englishTokenStream.getTokens();
            Iterator<String> iter = tokens.iterator();
            List<String> t = new ArrayList<>();
            while (iter.hasNext()) {
                t.add(iter.next());
            }
            int position = 1;
            for (int i = 0; i < t.size() - 1; i++) {
                List<String> p1 = processor.processToken(t.get(i), false);
                List<String> p2 = processor.processToken(t.get(i + 1), false);
                String myterm = Util.stemWord(p1.get(0)).trim() + " " + Util.stemWord(p2.get(0)).trim();
                index.addTerm(myterm, document.getId());
                vocab.add(myterm);
                position++;
            }

        });
        index.setmVocabulary(new ArrayList<>(vocabulary));
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000 + " seconds to index.");
        return index;
    }

    // Create a biword index from the xml files
    private static BiwordIndex biwordXmlIndexCorpus(Feed feed) {
        long start = System.currentTimeMillis();
        HashSet<String> vocabulary = new HashSet<>();
        TokenProcessor processor = new ExtendedTokenProcessor();
        Iterable<Doc> documents = feed.getDoc();
        // Create the vocabulary
        List<Doc> docs = new ArrayList<>();
        documents.forEach(document -> {
            docs.add(document);
        });

        // Create index for all the documents
        BiwordIndex index = new BiwordIndex();
        HashSet<String> vocab = new HashSet<>();
        for (int i = 0; i < docs.size(); i++) {
            EnglishTokenStream englishTokenStream = new EnglishTokenStream(new StringReader(docs.get(i).getContent()));
            Iterable<String> tokens = englishTokenStream.getTokens();
            Iterator<String> iter = tokens.iterator();
            List<String> t = new ArrayList<>();
            while (iter.hasNext()) {
                t.add(iter.next());
            }
            int position = 1;
            for (int j = 0; j < t.size() - 1; j++) {
                List<String> p1 = processor.processToken(t.get(j), false);
                List<String> p2 = processor.processToken(t.get(j + 1), false);
                String myterm = Util.stemWord(p1.get(0)) + " " + Util.stemWord(p2.get(0));
                index.addTerm(myterm, i);
                vocab.add(myterm);
                position++;
            }
            try {
                englishTokenStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        index.setmVocabulary(new ArrayList<>(vocabulary));
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000 + " seconds to index.");
        return index;
    }

    private static void createbinpostings(Index index, String fileName) throws FileNotFoundException {
        FileOutputStream file = new FileOutputStream(fileName);
        DataOutputStream data = new DataOutputStream(file);
        index.getVocabulary().forEach(term -> {
            List<Posting> termPosting = index.getPostings(term);
            termPosting.forEach(posting -> {
                try {
                    data.writeInt(posting.getDocumentId());
                    data.writeChars("->");
                    posting.getPositions().forEach(position -> {
                        try {
                            data.writeInt(position);
                        } catch (IOException e) {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    data.writeChars("===");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
//    private static void readbinpostings(String fileName) throws FileNotFoundException {
//        InputStream input = new FileInputStream(fileName);
//        try {
//            try (DataInputStream dis = new DataInputStream(input)) {
//                int count = input.available();
//                byte[] ary = new byte[count];
//                dis.read(ary);
//                for (byte bt : ary) {
//                    long k = (long) bt;
//                    System.out.print(k);
//                }
//                dis.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
