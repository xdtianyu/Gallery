package org.xdty.gallery.data;

import org.xdty.gallery.model.Media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MediaRepository implements MediaDataSource {

    private MediaCache mMediaCache;

    private ArrayList<Media> mRoots = new ArrayList<>();
    private HashMap<String, Media> mSupportMedias = new HashMap<>();

    private Media mCurrent;

    public MediaRepository() {
        mMediaCache = MediaCache.getInstance();
    }

    @Override
    public void register(Media media) {
        for (String scheme : media.scheme()) {
            if (!mSupportMedias.containsKey(scheme)) {
                mSupportMedias.put(scheme, media);
            }
        }
    }

    @Override
    public void addRoot(String uri, String username, String password) {

        if (uri.startsWith("/")) {
            uri = "file:/" + uri;
        }

        if (uri.contains("://")) {

            if (!uri.endsWith("/")) {
                uri = uri + "/";
            }

            for (Media media : mRoots) {
                if (uri.equals(media.getUri()) || uri.equals(media.getUri() + "/")) {
                    return;
                }
            }

            String[] parts = uri.split("://");
            String[] parts2 = parts[1].split("/", 2);
            String directory;
            if (parts2.length == 2) {
                directory = parts2[1];
            } else {
                directory = "/";
            }

            if (mSupportMedias.containsKey(parts[0])) {
                mSupportMedias.get(parts[0]).auth(parts[0] + "://" + parts2[0], directory, username,
                        password);
                mRoots.add(fromUri(uri));
                return;
            }
        }
        throw new Media.MediaException("Unknown scheme: " + uri);
    }

    @Override
    public List<Media> roots() {
        return mRoots;
    }

    @Override
    public Media getCurrent() {
        return mCurrent;
    }

    @Override
    public void setCurrent(Media media) {
        mCurrent = media;
    }

    @Override
    public Media getMedia(String uri) {
        Media media = mMediaCache.get(uri);
        if (media != null) {
            return media;
        }
        return fromUri(uri);
    }

    private Media fromUri(String uri) {
        if (uri.contains("://")) {
            String scheme = uri.substring(0, uri.indexOf("://"));
            if (mSupportMedias.containsKey(scheme)) {
                Media media = mSupportMedias.get(scheme).fromUri(uri);
                mMediaCache.put(media);
                return media;
            }
        }
        throw new Media.MediaException("Unknown scheme: " + uri);
    }

    @Override
    public Observable<List<Media>> loadDir(final Media media, final boolean isRefresh) {
        return Observable.create(new Observable.OnSubscribe<List<Media>>() {

            @Override
            public void call(Subscriber<? super List<Media>> subscriber) {
                if (isRefresh) {
                    media.clear();
                }

                List<Media> children = media.children();

                MediaCache.getInstance().put(children);

                List<Media> list = new ArrayList<>();

                for (Media m : children) {
                    if (m.isImage() || m.isDirectory()) {
                        list.add(m);
                    }
                }
                subscriber.onNext(list);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Media>> loadMediaList(final Media media) {
        return Observable.create(new Observable.OnSubscribe<List<Media>>() {

            @Override
            public void call(Subscriber<? super List<Media>> subscriber) {

                List<Media> children = media.children();

                MediaCache.getInstance().put(children);

                List<Media> list = new ArrayList<>();

                for (Media m : children) {
                    if (m.isImage()) {
                        list.add(m);
                    }
                }
                subscriber.onNext(list);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
