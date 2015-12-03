package org.xdty.gallery;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import org.xdty.webdav.WebDavFile;

import java.io.IOException;

public class DavRequestHandler extends RequestHandler {

    private final static String DAV_SCHEME = "dav";
    private final static String DAVS_SCHEME = "davs";

    @Override
    public boolean canHandleRequest(Request data) {
        return DAV_SCHEME.equals(data.uri.getScheme()) || DAVS_SCHEME.equals(data.uri.getScheme());
    }

    @Override
    public Result load(Request request, int networkPolicy) {
        try {
            WebDavFile davFile = new WebDavFile(request.uri.toString());
            return new Result(davFile.getInputStream(), Picasso.LoadedFrom.NETWORK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
