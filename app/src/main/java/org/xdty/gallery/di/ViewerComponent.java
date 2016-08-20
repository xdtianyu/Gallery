package org.xdty.gallery.di;

import org.xdty.gallery.activity.ViewerActivity;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.di.modules.ViewerModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class, ViewerModule.class })
public interface ViewerComponent {
    void inject(ViewerActivity viewerActivity);
}
