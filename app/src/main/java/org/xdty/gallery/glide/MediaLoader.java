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

import java.io.InputStream;

public class MediaLoader implements StreamModelLoader<Uri> {
    private final Context context;
    private final ModelCache<Uri, GlideUrl> modelCache = new ModelCache<>(500);

    public MediaLoader(Context context) {
        this.context = context;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(Uri model, int width, int height) {

        GlideUrl result = null;
        if (modelCache != null) {
            result = modelCache.get(model, width, height);
        }

        if (result == null) {

            result = new GlideUrl(model.toString());

            if (modelCache != null) {
                modelCache.put(model, width, height, result);
            }
        }
        return new MediaDataFetcher(context, model);
    }

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {

        @Override
        public ModelLoader<Uri, InputStream> build(Context context,
                GenericLoaderFactory factories) {
            return new MediaLoader(context);
        }

        @Override
        public void teardown() {

        }
    }
}
