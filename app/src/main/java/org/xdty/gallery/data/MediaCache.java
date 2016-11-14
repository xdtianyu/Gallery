package org.xdty.gallery.data;

import android.support.v4.util.LruCache;

import org.xdty.gallery.model.Media;

import java.util.List;

public final class MediaCache {

    private LruCache<String, Media> mCache = new LruCache<>(100000);

    static MediaCache getInstance() {
        return SingletonHelper.INSTANCE;
    }

    void put(Media media) {
        mCache.put(media.getUri(), media);
    }

    Media get(String key) {
        return mCache.get(key);
    }

    void put(List<Media> medias) {
        for (Media media : medias) {
            put(media);
        }
    }

    private static class SingletonHelper {
        private final static MediaCache INSTANCE = new MediaCache();
    }
}
