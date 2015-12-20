package org.xdty.webdav;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebDavAuth {

    private static List<Auth> authList = new ArrayList<>();

    private WebDavAuth() {
    }

    public static void addAuth(String url, String user, String password) {

        try {
            Auth auth = new Auth(url, user, password);

            if (authList.contains(auth)) {
                throw new WebDavAuthException("Auth already exist.");
            } else {
                authList.add(auth);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static Auth getAuth(String url) {
        try {
            URL u = new URL(null, url, Handler.DAV_HANDLER);
            String host = u.getHost();
            int port = u.getPort();

            for (Auth auth : authList) {
                if (auth.getHost().equals(host) && auth.getPort() == port) {
                    return auth;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Auth {
        private URL url;
        private String user;
        private String pass;
        public Auth(String url, String user, String pass) throws MalformedURLException {
            this.url = new URL(null, url, Handler.DAV_HANDLER);
            this.user = user;
            this.pass = pass;
        }

        public String getUrl() {
            return url.toString();
        }

        public String getHost() {
            return url.getHost();
        }

        public int getPort() {
            return url.getPort();
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        @Override
        public boolean equals(Object object) {
            boolean isEqual = false;

            if (object != null && object instanceof Auth) {
                isEqual = this.url.equals(((Auth) object).url) &&
                        this.user.equals(((Auth) object).user);
            }

            return isEqual;
        }
    }

    public static class WebDavAuthException extends RuntimeException {

        public WebDavAuthException(String detailMessage) {
            super(detailMessage);
        }
    }

}