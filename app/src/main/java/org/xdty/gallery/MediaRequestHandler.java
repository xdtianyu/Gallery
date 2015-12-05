package org.xdty.gallery;

import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import org.xdty.gallery.model.Media;

import java.io.IOException;

public class MediaRequestHandler extends RequestHandler {

    public final static String TAG = MediaRequestHandler.class.getSimpleName();

    @Override
    public boolean canHandleRequest(Request data) {
        return Media.SCHEME_SAMBA.equals(data.uri.getScheme()) ||
                Media.SCHEME_DAV.equals(data.uri.getScheme()) ||
                Media.SCHEME_DAVS.equals(data.uri.getScheme()) ||
                Media.SCHEME_CACHE.equals(data.uri.getScheme());
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        Media media = new Media(request.uri.toString());
        Log.d(TAG, media.getUri());
        return new Result(media.getInputStream(),
                media.isCache() ? Picasso.LoadedFrom.DISK : Picasso.LoadedFrom.NETWORK);
    }
}
