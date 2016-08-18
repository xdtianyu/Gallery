package org.xdty.gallery.data;

import android.util.Log;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.model.Media;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MediaRepository implements MediaDataSource {

    public MediaRepository() {
        Application.getAppComponent().inject(this);
    }

    @Override
    public Observable<List<Media>> loadDir(final Media media, final boolean isRefresh) {
        return Observable.create(new Observable.OnSubscribe<List<Media>>() {

            @Override
            public void call(Subscriber<? super List<Media>> subscriber) {
                if (isRefresh) {
                    media.clear();
                }

                List<Media> medias = media.children();
                List<Media> list = new ArrayList<>();

                for (Media m : medias) {
                    if (m.isImage() || m.isDirectory()) {
                        list.add(m);
                    }
                }
                subscriber.onNext(list);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
