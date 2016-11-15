package org.xdty.gallery.di.modules;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import org.xdty.gallery.activity.ViewerActivity;
import org.xdty.gallery.contract.ViewerContact;
import org.xdty.gallery.presenter.ViewerPresenter;

import java.io.InputStream;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class ViewerModule {

    ViewerActivity mView;

    public ViewerModule(ViewerActivity view) {
        mView = view;
    }

    @Provides
    ViewerContact.Presenter providePresenter() {
        return new ViewerPresenter(mView);
    }

    @Singleton
    @Provides
    RequestManager provideGlide(OkHttpClient okHttpClient) {
        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(okHttpClient);
        Glide.get(mView).register(GlideUrl.class, InputStream.class, factory);
        return Glide.with(mView);
    }
}
