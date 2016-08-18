package org.xdty.gallery.di.modules;

import android.content.Context;

import com.google.gson.Gson;

import org.xdty.gallery.application.Application;
import org.xdty.gallery.data.MediaDataSource;
import org.xdty.gallery.data.MediaRepository;
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

    @Singleton
    @Provides
    Gson provideGson() {
        return new Gson();
    }

    @Singleton
    @Provides
    MediaDataSource provideMediaDataSource() {
        return new MediaRepository();
    }

}
