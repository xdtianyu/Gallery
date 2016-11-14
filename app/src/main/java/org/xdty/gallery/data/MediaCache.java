package org.xdty.gallery.data;

import org.xdty.gallery.model.Media;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MediaCache {

    private Map<String, Media> mCache = new HashMap<>();

    static MediaCache getInstance() {
        return SingletonHelper.INSTANCE;
    }

    void put(Media media) {
        mCache.put(media.getUri(), media);
    }

    Media get(String key) {
        return mCache.get(key);
    }

    boolean contains(String key) {
        return mCache.containsKey(key);
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
