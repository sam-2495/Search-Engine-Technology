package cecs429.documents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

import com.google.gson.Gson;

import cecs.model.MyDocument;

public class JsonFileDocument implements FileDocument {
    private int mDocumentId;
    private Path mFilePath;
    private String title;

    public JsonFileDocument(int mDocumentId, Path mFilePath) {
        this.mDocumentId = mDocumentId;
        this.mFilePath = mFilePath;
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mFilePath.toString()));
            MyDocument doc = gson.fromJson(bufferedReader, MyDocument.class);
            if (doc != null) {
                this.title = doc.getTitle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get the content of the JSON File
    @Override
    public Reader getContent() {
        Gson gson = new Gson();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mFilePath.toString()));
            MyDocument doc = gson.fromJson(bufferedReader, MyDocument.class);
            if (doc != null) {
                return new StringReader(doc.getBody().replaceAll("\\s+", " "));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public Path getFilePath() {
        return mFilePath;
    }

    @Override
    public int getId() {
        return mDocumentId;
    }

    public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
        return new JsonFileDocument(documentId, absolutePath);
    }
}
