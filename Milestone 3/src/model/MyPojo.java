package model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MyPojo
{
	@XmlElement
    private Feed feed;

    public Feed getFeed ()
    {
        return feed;
    }

    public void setFeed (Feed feed)
    {
        this.feed = feed;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [feed = "+feed+"]";
    }
}
	