package cecs429.query;

import java.util.List;

import cecs429.index.Index;
import cecs429.index.Posting;

/**
 * A Query is a piece or whole of a Boolean query, whether that piece is a literal string or represents a merging of
 * other queries. All nodes in a query parse tree are Query objects.
 */
public interface Query {

    List<Posting> getPostings(Index index);
    /**
     * Retrieves a list of postings for the query, using an Index as the source.
     */
}