package org.xdty.webdav.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "collection")
public class Collection {
    @Element(name = "content", required = false)
    protected String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
