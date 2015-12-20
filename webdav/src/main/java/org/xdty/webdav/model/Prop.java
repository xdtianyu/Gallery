package org.xdty.webdav.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "prop")
public class Prop {

    @Element(required = false)
    protected String creationdate;
    @Element(required = false)
    protected String displayname;
    @Element(required = false)
    protected String getcontentlanguage;
    @Element(required = false)
    protected int getcontentlength;
    @Element(required = false)
    protected String getcontenttype;
    @Element(required = false)
    protected String getetag;
    @Element(required = false)
    protected String getlastmodified;
    @Element(required = false)
    protected String lockdiscovery;
    @Element(required = false)
    protected Resourcetype resourcetype;
    @Element(required = false)
    protected String source;
    @Element(required = false)
    protected String supportedlock;

    public String getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(String value) {
        this.creationdate = value;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String value) {
        this.displayname = value;
    }

    public String getGetcontentlanguage() {
        return getcontentlanguage;
    }

    public void setGetcontentlanguage(String value) {
        this.getcontentlanguage = value;
    }

    public int getGetcontentlength() {
        return getcontentlength;
    }

    public void setGetcontentlength(int value) {
        this.getcontentlength = value;
    }

    public String getGetcontenttype() {
        return getcontenttype;
    }

    public void setGetcontenttype(String value) {
        this.getcontenttype = value;
    }

    public String getGetetag() {
        return getetag;
    }

    public void setGetetag(String value) {
        this.getetag = value;
    }

    public String getGetlastmodified() {
        return getlastmodified;
    }

    public void setGetlastmodified(String value) {
        this.getlastmodified = value;
    }

    public String getLockdiscovery() {
        return lockdiscovery;
    }

    public void setLockdiscovery(String value) {
        this.lockdiscovery = value;
    }

    public Resourcetype getResourcetype() {
        return resourcetype;
    }

    public void setResourcetype(Resourcetype value) {
        this.resourcetype = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    public String getSupportedlock() {
        return supportedlock;
    }

    public void setSupportedlock(String value) {
        this.supportedlock = value;
    }
}