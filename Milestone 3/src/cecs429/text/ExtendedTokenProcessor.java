package cecs429.text;

import java.util.ArrayList;
import java.util.List;

import cecs.util.Util;

public class ExtendedTokenProcessor implements TokenProcessor {

    @Override
    public List<String> processToken(String token, boolean doStemming) {
        // Remove non-alphanumeric characters from the beginning or end of a token
        token = token.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "");
        // Remove single and double quotes from the token
        token = token.replaceAll("\'", "").replaceAll("\"", "");
        // Convert the token into lower case
        token = token.toLowerCase();

        // Find the stemmed words for all the tokens
        if (doStemming) {
            // Remove the hyphens and create new tokens
            List<String> newTokens = new ArrayList<>();
            if (token.contains("-")) {
                token = token.replaceAll("-", "");
                newTokens.add(token);
//                String[] tokens = token.split("-");
//                for (String a : token) {
//                    newTokens.add(a);
                }
             else {
                newTokens.add(token);
                }

            List<String> stemmedTokens = new ArrayList<>();
            for (String a : newTokens) {
                String stemmedToken = Util.stemWord(a);
                if (!newTokens.contains(stemmedToken)) {
                    stemmedTokens.add(stemmedToken);
                }
            }
            // Add all the stemmed words to the list
            newTokens.addAll(stemmedTokens);
            return newTokens;
        }
        List<String> tokens = new ArrayList<>();
        tokens.add(token);
        return tokens;
    }
}
