package org.xdty.gallery.application;

import android.os.StrictMode;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.xdty.gallery.utils.OkHttp;

import io.reactivex.Completable;

public class DebugApplication extends Application {
    private final static String TAG = DebugApplication.class.getSimpleName();

    @Override
    public void onCreate() {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                //.penaltyDeath()
                .build());

        Completable.fromRunnable(() ->
                Stetho.initialize(Stetho.newInitializerBuilder(DebugApplication.this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(DebugApplication.this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(
                                DebugApplication.this))
                        .build())
        ).subscribe();

        OkHttp.getInstance().addNetworkInterceptor(new StethoInterceptor());

        super.onCreate();
    }
}
