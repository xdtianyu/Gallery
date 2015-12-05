package org.xdty.gallery.glide;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import org.xdty.gallery.model.Media;

import java.io.IOException;
import java.io.InputStream;

public class MediaDataFetcher implements DataFetcher<InputStream> {

    private static final String TAG = MediaDataFetcher.class.getSimpleName();
    private final Uri uri;
    private final Context context;
    private InputStream data;

    public MediaDataFetcher(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        data = new Media(uri.toString()).getInputStream();
        return data;
    }

    @Override
    public void cleanup() {
        if (data != null) {
            try {
                data.close();
            } catch (IOException e) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "failed to close data", e);
                }
            }
        }
    }

    @Override
    public String getId() {
        return uri.toString();
    }

    @Override
    public void cancel() {

    }
}
