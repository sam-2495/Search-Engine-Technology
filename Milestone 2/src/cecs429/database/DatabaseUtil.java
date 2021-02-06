package cecs429.database;

import java.util.Map;
import java.util.Map.Entry;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class DatabaseUtil {

	public static DB soundexDB = null;
	public static BTreeMap<String, Integer> soundexMap = null;
	public static DB postingsDB = null;
	public static BTreeMap<String, Integer> postingMap = null;
	public static DB biwordDB = null;
	public static BTreeMap<String, Integer> biwordMap = null;
	public static DB docWeightsDB = null;
	public static BTreeMap<Integer, Integer> docWeightsMap = null;

	public DatabaseUtil() {
		String indexFile = "/Users/samarthyadav/Desktop/MLP2 DT/dpIndex.db/";
		if (postingsDB == null) {
			postingsDB = DBMaker.fileDB(indexFile).make();
			postingMap = postingsDB.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.INTEGER)
					.createOrOpen();
		}
		String biwordFile = "/Users/samarthyadav/Desktop/MLP2 DT/biwordIndex.db/";
		if (biwordDB == null) {
			biwordDB = DBMaker.fileDB(biwordFile).make();
			biwordMap = biwordDB.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.INTEGER)
					.createOrOpen();
		}
		String docWeightsFile = "/Users/samarthyadav/Desktop/MLP2 DT/docWeights.db/";
		if (docWeightsDB == null) {
			docWeightsDB = DBMaker.fileDB(docWeightsFile).make();
			docWeightsMap = docWeightsDB.treeMap("map").keySerializer(Serializer.INTEGER)
					.valueSerializer(Serializer.INTEGER).createOrOpen();
		}
	}

	public static void saveSoundexToDB(Map<String, Integer> soundexMap) {
		String fileName = "/Users/samarthyadav/Desktop/MLP2 DT/soundex.db/";
		DB db = DBMaker.fileDB(fileName).make();
		BTreeMap<String, Integer> map = null;
		try {
			map = db.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.INTEGER).createOrOpen();
			for (Map.Entry<String, Integer> entry : soundexMap.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			System.out.println("Some Error Occured in saveSoundexToDB.");
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	public int getSoundexFromDB(String soundex) {
		String soundexFile = "/Users/samarthyadav/Desktop/MLP2 DT/soundex.db/";
		try {
			if (soundexDB == null) {
				soundexDB = DBMaker.fileDB(soundexFile).make();
				soundexMap = soundexDB.treeMap("map").keySerializer(Serializer.STRING)
						.valueSerializer(Serializer.INTEGER).createOrOpen();
			}
			return (int) soundexMap.get(soundex);
		} catch (Exception e) {
			System.out.println("Some Error Occured in getSoundexFromDB.");
			e.printStackTrace();
		}
		return -1;
	}

	public static void saveTermIndexToDB(Map<String, Integer> indexMap) {
		String fileName = "/Users/samarthyadav/Desktop/MLP2 DT/dpIndex.db/";
		DB db = null;
		BTreeMap<String, Integer> map = null;
		try {
			db = DBMaker.fileDB(fileName).make();
			map = db.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.INTEGER).createOrOpen();
			for (Map.Entry<String, Integer> entry : indexMap.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			System.out.println("Some Error Occured in saveTermIndexToDB.");
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	public int getTermIndexFromDB(String term) {
		try {
			return (int) postingMap.get(term);
		} catch (Exception e) {
			System.out.println("Some Error Occured in getTermIndexFromDB.");
			e.printStackTrace();
		}
		return -1;
	}

	public static void saveBiwordIndexToDB(Map<String, Integer> indexMap) {
		String fileName = "/Users/samarthyadav/Desktop/MLP2 DT/biwordIndex.db/";
		DB db = null;
		BTreeMap<String, Integer> map = null;
		try {
			db = DBMaker.fileDB(fileName).make();
			map = db.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.INTEGER).createOrOpen();
			for (Map.Entry<String, Integer> entry : indexMap.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			System.out.println("Some Error Occured in saveBiwordIndexToDB.");
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	public int getBiwordIndexFromDB(String term) {
		try {
			return (int) biwordMap.get(term);
		} catch (Exception e) {
			System.out.println("Some Error Occured in getBiwordIndexFromDB.");
			e.printStackTrace();
		}
		return -1;
	}

	public static void saveDocWeightsToDB(Map<Integer, Integer> indexMap) {
		String fileName = "/Users/samarthyadav/Desktop/MLP2 DT/docWeights.db/";
		DB db = null;
		BTreeMap<Integer, Integer> map = null;
		try {
			db = DBMaker.fileDB(fileName).make();
			map = db.treeMap("map").keySerializer(Serializer.INTEGER).valueSerializer(Serializer.INTEGER)
					.createOrOpen();
			for (Entry<Integer, Integer> entry : indexMap.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			System.out.println("Some Error Occured in saveDocWeightsToDB.");
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

	public int getDocWeightsFromDB(int docId) {
		try {
			return docWeightsMap.get(docId);
		} catch (Exception e) {
			System.out.println("Some Error Occured in getDocWeightsFromDB.");
			e.printStackTrace();
		}
		return -1;
	}
}
