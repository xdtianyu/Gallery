package org.xdty.webdav.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "response")
public class Response {

    @Element
    protected String href;
    @Element
    protected Propstat propstat;


    public String getHref() {
        return href;
    }


    public void setHref(String value) {
        this.href = value;
    }

    public Propstat getPropstat() {
        return propstat;
    }

    public void setPropstat(Propstat value) {
        this.propstat = value;
    }
}