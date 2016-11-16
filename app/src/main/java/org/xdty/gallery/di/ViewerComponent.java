package org.xdty.gallery.di;

import org.xdty.gallery.activity.ViewerActivity;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.di.modules.ViewerModule;
import org.xdty.gallery.fragment.GifImageFragment;
import org.xdty.gallery.fragment.ImageFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class, ViewerModule.class })
public interface ViewerComponent {
    void inject(ViewerActivity viewerActivity);

    void inject(ImageFragment imageFragment);

    void inject(GifImageFragment gifImageFragment);
}
