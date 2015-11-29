package org.xdty.gallery;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SambaRequestHandler extends RequestHandler {

    private static final String SAMBA_SCHEME = "smb";

    @Override
    public boolean canHandleRequest(Request data) {
        return SAMBA_SCHEME.equals(data.uri.getScheme());
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        return new Result(getInputStream(request), Picasso.LoadedFrom.NETWORK);
    }

    InputStream getInputStream(Request request) throws FileNotFoundException {
        return null;
    }
}
