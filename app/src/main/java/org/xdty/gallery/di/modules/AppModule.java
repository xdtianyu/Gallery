package org.xdty.gallery.di.modules;

import android.content.Context;

import com.google.gson.Gson;

import org.xdty.gallery.BuildConfig;
import org.xdty.gallery.application.Application;
import org.xdty.gallery.data.MediaDataSource;
import org.xdty.gallery.data.MediaRepository;
import org.xdty.gallery.model.database.Database;
import org.xdty.gallery.model.database.DatabaseImpl;
import org.xdty.gallery.model.db.Models;
import org.xdty.gallery.setting.Setting;
import org.xdty.gallery.setting.SettingImpl;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseSource;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.xdty.gallery.utils.Constants.DB_NAME;
import static org.xdty.gallery.utils.Constants.DB_VERSION;

@Module
public class AppModule {

    private Application mApplication;
    private OkHttpClient mOkHttpClient;

    public AppModule(Application application) {
        mApplication = application;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url()
                        .newBuilder()
                        //.addQueryParameter("timestamp",
                        //        Long.toString(System.currentTimeMillis() / 1000 / 60))
                        .build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        };

        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(interceptor)
                .build();
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

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient() {
        return mOkHttpClient;
    }

    @Singleton
    @Provides
    public Database provideDatabase() {
        return DatabaseImpl.getInstance();
    }

    @Singleton
    @Provides
    public EntityDataStore<Persistable> provideDatabaseSource() {

        DatabaseSource source = new DatabaseSource(mApplication, Models.DEFAULT, DB_NAME,
                DB_VERSION);
        source.setLoggingEnabled(BuildConfig.DEBUG);
        Configuration configuration = source.getConfiguration();

        return new EntityDataStore<>(configuration);
    }

}
