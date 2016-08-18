package org.xdty.gallery.presenter;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.contract.ViewerContact;

public class ViewerPresenter implements ViewerContact.Presenter {

    private ViewerContact.View mView;

    public ViewerPresenter(ViewerContact.View view) {
        mView = view;

        Application.getAppComponent().inject(this);
    }

    @Override
    public void start() {

    }
}
