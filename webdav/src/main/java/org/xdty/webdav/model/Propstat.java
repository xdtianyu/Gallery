package org.xdty.webdav.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "propstat")
public class Propstat {

    @Element
    protected Prop prop;
    @Element
    protected String status;

    public Prop getProp() {
        return prop;
    }

    public void setProp(Prop value) {
        this.prop = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }
}