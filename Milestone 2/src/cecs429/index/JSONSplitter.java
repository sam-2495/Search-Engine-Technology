package cecs429.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cecs.model.MyDocs;

public class JSONSplitter {
	// Split the json into multiple json
	public static String splitJson(String fileName) {
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
			List<cecs.model.MyDocument> documents = file.getDocuments();
			System.out.println("Number of documents: " + documents.size());
			System.out.println("Creating Documents.");
			for (int i = 0; i < documents.size(); i++) {
				// To Test for less files
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
}
