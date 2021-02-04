package cecs429.query;

import java.util.List;

import cecs429.index.Index;
import cecs429.index.Posting;

public class DNFLiteral implements Query {
    private String query;

    // Return the postings by parsing the query again
    @Override
    public List<Posting> getPostings(Index index) {
        BooleanQueryParser booleanQueryParser = new BooleanQueryParser();
        Query obj = booleanQueryParser.parseQuery(query);
        return obj.getPostings(index);
    }

    public DNFLiteral(String q) {
        query = q;
    }
}
