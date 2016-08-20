package org.xdty.gallery.di.modules;

import org.xdty.gallery.contract.ViewerContact;
import org.xdty.gallery.presenter.ViewerPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewerModule {

    ViewerContact.View mView;

    public ViewerModule(ViewerContact.View view) {
        mView = view;
    }

    @Provides
    public ViewerContact.Presenter providePresenter() {
        return new ViewerPresenter(mView);
    }

}
