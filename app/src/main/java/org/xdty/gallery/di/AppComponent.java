package org.xdty.gallery.di;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.di.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(Application application);
}
