package org.xdty.webdav.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "resourcetype")
public class Resourcetype {

    @Element(name = "collection", required = false)
    protected String content;

    public String getContent() {
        return this.content;
    }
}