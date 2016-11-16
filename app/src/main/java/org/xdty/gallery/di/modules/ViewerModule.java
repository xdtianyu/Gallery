package org.xdty.gallery.di.modules;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;

import org.xdty.gallery.activity.ViewerActivity;
import org.xdty.gallery.contract.ViewerContact;
import org.xdty.gallery.glide.GifDrawableBytesTranscoder;
import org.xdty.gallery.glide.MediaLoader;
import org.xdty.gallery.glide.StreamByteArrayResourceDecoder;
import org.xdty.gallery.model.Media;
import org.xdty.gallery.presenter.ViewerPresenter;

import java.io.InputStream;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import pl.droidsonroids.gif.GifDrawable;

@Module
public class ViewerModule {

    private ViewerActivity mView;

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

    @Singleton
    @Provides
    GenericRequestBuilder<Media, InputStream, byte[], GifDrawable> provideGifRequestBuilder(
            RequestManager requestManager) {

        return requestManager.using(new MediaLoader(mView), InputStream.class)
                .from(Media.class)
                .as(byte[].class)
                .transcode(new GifDrawableBytesTranscoder(), GifDrawable.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .decoder(new StreamByteArrayResourceDecoder())
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<>(new StreamByteArrayResourceDecoder()));
    }
}
