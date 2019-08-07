package org.xdty.gallery.data;

import org.xdty.gallery.model.Media;

import java.util.List;

import io.reactivex.Observable;

public interface MediaDataSource {

    void register(Media media);

    void addRoot(String uri, String username, String password);

    List<Media> roots();

    Media getCurrent();

    void setCurrent(Media media);

    Media getMedia(String uri);

    int getRotate(String uri);

    void setRotate(String uri, int rotate);

    Observable<List<Media>> loadDir(Media media, boolean isRefresh);

    Observable<List<Media>> loadMediaList(Media media);

    void clearCache();

    void setMediaPosition(int position);

    int getMediaPosition();

    void setFilePosition(int position);

    int getFilePosition();
}
