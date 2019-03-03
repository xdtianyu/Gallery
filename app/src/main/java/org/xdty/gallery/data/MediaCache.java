package org.xdty.gallery.data;

import androidx.collection.LruCache;

import org.xdty.gallery.model.Media;

import java.util.List;

public final class MediaCache {

    private LruCache<String, Media> mCache = new LruCache<>(100000);

    private LruCache<String, Integer> mRotateCache = new LruCache<>(500);

    static MediaCache getInstance() {
        return SingletonHelper.INSTANCE;
    }

    void put(Media media) {
        mCache.put(media.getUri(), media);
    }

    Media get(String key) {
        return mCache.get(key);
    }

    void putRotation(String key, int rotate) {
        mRotateCache.put(key, rotate);
    }

    int getRotate(String key) {
        Integer value = mRotateCache.get(key);
        return value != null ? value : 0;
    }

    void put(List<Media> medias) {
        for (Media media : medias) {
            put(media);
        }
    }

    public void clear() {
        mCache.evictAll();
    }

    private static class SingletonHelper {
        private final static MediaCache INSTANCE = new MediaCache();
    }
}
