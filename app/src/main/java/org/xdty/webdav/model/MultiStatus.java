package org.xdty.webdav.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "multistatus")
public class MultiStatus {

    @ElementList(inline = true)
    protected List<Response> response;

    public List<Response> getResponse() {
        if (response == null) {
            response = new ArrayList<Response>();
        }
        return this.response;
    }
}