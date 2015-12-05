package org.xdty.gallery.glide;

import android.content.Context;
import android.net.Uri;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import org.xdty.gallery.model.Media;

import java.io.InputStream;

public class MediaLoader implements StreamModelLoader<Media> {
    private final Context context;
    private final ModelCache<Uri, GlideUrl> modelCache = new ModelCache<>(500);

    public MediaLoader(Context context) {
        this.context = context;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(Media model, int width, int height) {

        GlideUrl result = null;
        if (modelCache != null) {
            result = modelCache.get(Uri.parse(model.getUri()), width, height);
        }

        if (result == null) {

            result = new GlideUrl(model.getUri());

            if (modelCache != null) {
                modelCache.put(Uri.parse(model.getUri()), width, height, result);
            }
        }
        return new MediaDataFetcher(context, model);
    }

    public static class Factory implements ModelLoaderFactory<Media, InputStream> {

        @Override
        public ModelLoader<Media, InputStream> build(Context context,
                GenericLoaderFactory factories) {
            return new MediaLoader(context);
        }

        @Override
        public void teardown() {

        }
    }
}
