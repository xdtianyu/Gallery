package org.xdty.gallery.application;

import org.xdty.gallery.BuildConfig;
import org.xdty.gallery.di.AppComponent;
import org.xdty.gallery.di.DaggerAppComponent;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.setting.Setting;

import javax.inject.Inject;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.reactivex.plugins.RxJavaPlugins;

public class Application extends android.app.Application {

    private static AppComponent sAppComponent;

    @Inject
    protected Setting mSetting;

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        sAppComponent.inject(this);

        if (BuildConfig.DEBUG || mSetting.isCatchCrashEnable()) {
            CustomActivityOnCrash.install(this);
        }

        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);
    }

}

