package cecs429.query;

import java.util.List;

import cecs429.index.Posting;

public class NotQuery implements Query {

    private Query mComponent;

    public NotQuery(Query component) {
        mComponent = component;
    }

    @Override
    public List<Posting> getPostings() {
        return mComponent.getPostings();
    }

    @Override
    public boolean gettype() {
        return false;
    }

}