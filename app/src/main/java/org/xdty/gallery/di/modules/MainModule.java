package org.xdty.gallery.di.modules;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import org.xdty.gallery.activity.MainActivity;
import org.xdty.gallery.contract.MainContact;
import org.xdty.gallery.presenter.MainPresenter;

import java.io.InputStream;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class MainModule {

    private MainActivity mView;

    public MainModule(MainActivity view) {
        mView = view;
    }

    @Provides
    MainContact.Presenter providePresenter() {
        return new MainPresenter(mView);
    }

    @Singleton
    @Provides
    RequestManager provideGlide(OkHttpClient okHttpClient) {
        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(okHttpClient);
        Glide.get(mView).register(GlideUrl.class, InputStream.class, factory);
        return Glide.with(mView);
    }

}
