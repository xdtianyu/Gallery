package org.xdty.gallery.di.modules;

import android.content.Context;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.model.setting.Setting;
import org.xdty.gallery.model.setting.SettingImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return mApplication;
    }

    @Singleton
    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Singleton
    @Provides
    Setting provideSetting() {
        return new SettingImpl(mApplication);
    }

}
