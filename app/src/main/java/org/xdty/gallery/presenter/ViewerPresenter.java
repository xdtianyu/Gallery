package org.xdty.gallery.presenter;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.contract.ViewerContact;
import org.xdty.gallery.data.MediaDataSource;
import org.xdty.gallery.model.Media;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;

public class ViewerPresenter implements ViewerContact.Presenter {

    @Inject
    MediaDataSource mDataSource;

    private ViewerContact.View mView;

    private List<Media> mMedias = new ArrayList<>();
    private List<Media> mFiles = new ArrayList<>();

    public ViewerPresenter(ViewerContact.View view) {
        mView = view;

        Application.getAppComponent().inject(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void loadData(String uri, String parentUri, String host, final int position) {
        Media media = mDataSource.getMedia(uri);
        mView.load(media);

        final Media parent = mDataSource.getMedia(parentUri);

        mDataSource.loadDir(parent, false).subscribe(new Action1<List<Media>>() {
            @Override
            public void call(List<Media> medias) {
                mFiles.clear();
                mFiles.addAll(medias);

                loadMediaList(parent, position);
            }
        });

    }

    private void loadMediaList(Media parent, final int position) {
        mDataSource.loadMediaList(parent).subscribe(new Action1<List<Media>>() {
            @Override
            public void call(List<Media> medias) {
                mMedias.clear();
                mMedias.addAll(medias);
                if (mFiles.size() > position) {
                    mDataSource.setMediaPosition(mMedias.indexOf(mFiles.get(position)));
                } else {
                    mDataSource.setMediaPosition(position);
                }
                mView.replaceData(mMedias, mDataSource.getMediaPosition());
                mView.setTitle(mMedias.get(mDataSource.getMediaPosition()).getName());
                mView.startTransition();
            }
        });
    }

    @Override
    public void pageSelected(int position) {
        Media media = mMedias.get(position);
        mView.setTitle(media.getName());
        mDataSource.setMediaPosition(position);
    }

    @Override
    public int getSelectedPosition() {
        return mDataSource.getMediaPosition();
    }

    @Override
    public int getPosition() {
        int position = mFiles.indexOf(mMedias.get(mDataSource.getMediaPosition()));
        mDataSource.setFilePosition(position);
        return position;
    }

    @Override
    public void clear() {
        mFiles.clear();
        mMedias.clear();
    }

    @Override
    public String getCurrentName() {
        return mMedias.get(mDataSource.getMediaPosition()).getName();
    }
}
