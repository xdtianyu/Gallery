package org.xdty.gallery.data;

import org.xdty.gallery.model.Media;

import java.util.List;

import rx.Observable;

public interface MediaDataSource {
    Observable<List<Media>> loadDir(Media media, boolean isRefresh);
}
