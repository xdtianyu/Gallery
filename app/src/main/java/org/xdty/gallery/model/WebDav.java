package org.xdty.gallery.model;

import org.xdty.webdav.WebDavAuth;
import org.xdty.webdav.WebDavFile;

import java.net.MalformedURLException;

public class WebDav {
    public static void add(String url, String user, String password) {

        if (WebDavAuth.getAuth(url) == null) {
            WebDavAuth.addAuth(url, user, password);
        }
    }

    public static WebDavFile root(String domain) {
        WebDavFile webDavFile = null;
        WebDavAuth.Auth auth = WebDavAuth.getAuth(domain);
        if (auth != null) {
            try {
                webDavFile = new WebDavFile(auth.getUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return webDavFile;
    }
}
