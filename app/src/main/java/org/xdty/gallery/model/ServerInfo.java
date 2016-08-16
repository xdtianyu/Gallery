package org.xdty.gallery.model;

public class ServerInfo {
    String uri;
    String user;
    String pass;

    public ServerInfo(String uri, String user, String pass) {
        this.uri = uri;
        this.user = user;
        this.pass = pass;
    }

    public String getUri() {
        return uri;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
