package org.xdty.gallery;

import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import org.xdty.gallery.model.Samba;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jcifs.smb.SmbFile;

public class SambaRequestHandler extends RequestHandler {

    public final static String TAG = SambaRequestHandler.class.getSimpleName();

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
        Log.d(TAG, request.uri.toString());
        try {
            SmbFile smbFile = new SmbFile(request.uri.toString(), Samba.getAuth(request.uri.getHost()));
            return smbFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
