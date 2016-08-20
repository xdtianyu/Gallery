package org.xdty.gallery.di;

import org.xdty.gallery.activity.MainActivity;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.di.modules.MainModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class, MainModule.class })
public interface MainComponent {

    void inject(MainActivity activity);

}
