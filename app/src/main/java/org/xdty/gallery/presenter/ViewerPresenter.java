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

    private int mSelectedPosition = -1;

    private List<Media> mMedias = new ArrayList<>();

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

        Media parent = mDataSource.getMedia(parentUri);
        mDataSource.loadMediaList(parent).subscribe(new Action1<List<Media>>() {
            @Override
            public void call(List<Media> medias) {
                mMedias.clear();
                mMedias.addAll(medias);
                mView.replaceData(mMedias, position);
                mView.setTitle(mMedias.get(position).getName());
            }
        });
    }

    @Override
    public void pageSelected(int position) {
        Media media = mMedias.get(position);
        mView.setTitle(media.getName());
        mSelectedPosition = position;
    }

    @Override
    public int getPosition() {
        return mSelectedPosition;
    }

    @Override
    public void clear() {
        mMedias.clear();
    }
}
