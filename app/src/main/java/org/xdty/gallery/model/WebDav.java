package org.xdty.gallery.model;

import org.xdty.webdav.WebDavAuth;

public class WebDav {
    public static void add(String url, String user, String password) {

        if (WebDavAuth.getAuth(url) == null) {
            WebDavAuth.addAuth(url, user, password);
        }
    }
}
