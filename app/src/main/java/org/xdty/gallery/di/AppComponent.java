package org.xdty.gallery.di;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.presenter.MainPresenter;
import org.xdty.gallery.presenter.ViewerPresenter;
import org.xdty.gallery.view.GalleryAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(Application application);

    void inject(MainPresenter mainPresenter);

    void inject(ViewerPresenter viewerPresenter);

    void inject(GalleryAdapter galleryAdapter);
}
