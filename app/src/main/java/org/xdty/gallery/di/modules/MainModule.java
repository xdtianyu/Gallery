package org.xdty.gallery.di.modules;

import org.xdty.gallery.contract.MainContact;
import org.xdty.gallery.presenter.MainPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    private MainContact.View mView;

    public MainModule(MainContact.View view) {
        mView = view;
    }

    @Provides
    MainContact.Presenter providePresenter() {
        return new MainPresenter(mView);
    }

}
