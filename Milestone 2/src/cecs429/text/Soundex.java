package cecs429.text;

import java.util.ArrayList;
import java.util.List;

public class Soundex {

	private List<Character> toReplaceWithZero = new ArrayList<>();
	private List<Character> toReplaceWithOne = new ArrayList<>();
	private List<Character> toReplaceWithTwo = new ArrayList<>();
	private List<Character> toReplaceWithThree = new ArrayList<>();
	private List<Character> toReplaceWithFour = new ArrayList<>();
	private List<Character> toReplaceWithFive = new ArrayList<>();
	private List<Character> toReplaceWithSix = new ArrayList<>();

	public Soundex() {
		toReplaceWithZero.add('A');
		toReplaceWithZero.add('E');
		toReplaceWithZero.add('I');
		toReplaceWithZero.add('O');
		toReplaceWithZero.add('U');
		toReplaceWithZero.add('H');
		toReplaceWithZero.add('W');
		toReplaceWithZero.add('Y');
		toReplaceWithOne.add('B');
		toReplaceWithOne.add('F');
		toReplaceWithOne.add('P');
		toReplaceWithOne.add('V');
		toReplaceWithTwo.add('C');
		toReplaceWithTwo.add('G');
		toReplaceWithTwo.add('J');
		toReplaceWithTwo.add('K');
		toReplaceWithTwo.add('Q');
		toReplaceWithTwo.add('S');
		toReplaceWithTwo.add('X');
		toReplaceWithTwo.add('Z');
		toReplaceWithThree.add('D');
		toReplaceWithThree.add('T');
		toReplaceWithFour.add('L');
		toReplaceWithFive.add('M');
		toReplaceWithFive.add('N');
		toReplaceWithSix.add('R');
	}

	public String getSoundex(String term) {
//		System.out.println(term);
		if (term != "") {
			term = term.replace(".", "");
			term = term.toUpperCase();
			StringBuilder soundexTerm = new StringBuilder();
			soundexTerm.append(term.charAt(0));
			for (int i = 1; i < term.length(); i++) {
				Character character = term.charAt(i);
				if (character == '\'') {
					continue;
				}
				Character charToAppend = null;
				if (toReplaceWithZero.contains(character)) {
					charToAppend = '0';
				} else if (toReplaceWithOne.contains(character)) {
					charToAppend = '1';
				} else if (toReplaceWithTwo.contains(character)) {
					charToAppend = '2';
				} else if (toReplaceWithThree.contains(character)) {
					charToAppend = '3';
				} else if (toReplaceWithFour.contains(character)) {
					charToAppend = '4';
				} else if (toReplaceWithFive.contains(character)) {
					charToAppend = '5';
				} else if (toReplaceWithSix.contains(character)) {
					charToAppend = '6';
				}
				if (soundexTerm.charAt(soundexTerm.length() - 1) != charToAppend) {
					soundexTerm.append(charToAppend);
				}
			}
			String soundexString = new String(soundexTerm);
			soundexString = soundexString.replaceAll("0", "");
			if (soundexString.length() == 4) {
				return soundexString;
			} else if (soundexString.length() < 4) {
				int padding = 4 - soundexString.length();
				for (int i = 0; i < padding; i++) {
					soundexString = soundexString + "0";
				}
				return soundexString;
			} else {
				return soundexString.substring(0, 5);
			}
		}
		return term;
	}

	public static void main(String[] args) {
		String s = new Soundex().getSoundex("O'Connel");
		System.out.println(s);
	}

}
