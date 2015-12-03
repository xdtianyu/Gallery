package org.xdty.webdav;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {

    static final URLStreamHandler DAV_HANDLER = new Handler();

    protected int getDefaultPort() {
        return 80;
    }
    public URLConnection openConnection( URL u ) throws IOException {
        return null;
    }
    protected void parseURL( URL u, String spec, int start, int limit ) {
        String host = u.getHost();
        String path, ref;
        int port;

        super.parseURL( u, spec, start, limit );
        path = u.getPath();
        ref = u.getRef();
        if (ref != null) {
            path += '#' + ref;
        }
        port = u.getPort();
        if( port == -1 ) {
            port = getDefaultPort();
        }
        if (spec.equals( "dav://" )) {
            setURL( u, "dav", u.getHost(), port,
                    u.getAuthority(), u.getUserInfo(),
                    path, u.getQuery(), null );
        } else if(spec.equals( "davs://" )) {
            setURL( u, "davs", u.getHost(), port,
                    u.getAuthority(), u.getUserInfo(),
                    path, u.getQuery(), null );
        }

    }
}
