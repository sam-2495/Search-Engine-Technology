package model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sublink")
public class Sublink
{
	@XmlElement
    private String anchor;

	@XmlAttribute
    private String linktype;

	@XmlElement
    private String link;

    public String getAnchor ()
    {
        return anchor;
    }

    public void setAnchor (String anchor)
    {
        this.anchor = anchor;
    }

    public String getLinktype ()
    {
        return linktype;
    }

    public void setLinktype (String linktype)
    {
        this.linktype = linktype;
    }

    public String getLink ()
    {
        return link;
    }

    public void setLink (String link)
    {
        this.link = link;
    }

    @Override
    public String toString()
    {
        return "[anchor = "+anchor+", linktype = "+linktype+", link = "+link+"]";
    }
}
	