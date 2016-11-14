package org.xdty.gallery.data;

import org.xdty.gallery.model.Media;

import java.util.List;

import rx.Observable;

public interface MediaDataSource {

    void register(Media media);

    void addRoot(String uri, String username, String password);

    List<Media> roots();

    Media getCurrent();

    void setCurrent(Media media);

    Media getMedia(String uri);

    Observable<List<Media>> loadDir(Media media, boolean isRefresh);

    Observable<List<Media>> loadMediaList(Media media);

    void clearCache();
}
