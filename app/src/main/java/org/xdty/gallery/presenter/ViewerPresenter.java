package org.xdty.gallery.presenter;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.contract.ViewerContact;
import org.xdty.gallery.model.Media;

import java.util.ArrayList;
import java.util.List;

public class ViewerPresenter implements ViewerContact.Presenter {

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
    public void loadData(String uri, String host, int position) {
        mMedias.addAll(Media.Builder.getCurrent().children());
        mView.replaceData(mMedias);
        mView.setCurrentItem(position);
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
