package model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "links")
public class Links
{
	@XmlElement
    private Sublink[] sublink;

    public Sublink[] getSublink ()
    {
        return sublink;
    }

    public void setSublink (Sublink[] sublink)
    {
        this.sublink = sublink;
    }

    @Override
    public String toString()
    {
        return "[sublink = "+sublink+"]";
    }
}
			
		