package cecs.util;

public class Util {

	// Method to stem a word using the snowball stemmer
	public static String stemWord(String term) {
		Class stemClass;
		try {
			stemClass = Class.forName("cecs.util.EnglishStemmer");
			SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
			stemmer.setCurrent(term);
			stemmer.stem();
			return stemmer.getCurrent();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return term;
	}
}
